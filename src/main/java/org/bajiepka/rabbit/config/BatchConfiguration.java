package org.bajiepka.rabbit.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.bajiepka.rabbit.batch.JobCompletionNotificationListener;
import org.bajiepka.rabbit.batch.ProductItemProcessor;
import org.bajiepka.rabbit.entity.Product;
import org.bajiepka.rabbit.service.ExecuteTasklet;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public FlatFileItemReader<Product> reader() {
        return new FlatFileItemReaderBuilder<Product>()
                .name("productItemReader")
                .resource(new ClassPathResource("import-products.csv"))
                .delimited().delimiter(";")
                .names(new String[]{"name", "description"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Product>() {{
                    setTargetType(Product.class);
                }})
                .build();
    }

    @Bean
    public ProductItemProcessor processor() {
        return new ProductItemProcessor();
    }

    @Bean
    public MyBatisBatchItemWriter<Product> writer(SqlSessionFactory factory){
        return new MyBatisBatchItemWriterBuilder<Product>()
                .sqlSessionFactory(factory)
                .statementId("org.bajiepka.rabbit.mappers.ProductMapper.save")
                .build();
    }

    @Bean(name = "csvToPostgresJob")
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory.get("importProductsJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

    @Bean
    public Step step1(MyBatisBatchItemWriter<Product> writer) {
        return stepBuilderFactory.get("step1")
                .<Product, Product> chunk(5)
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }

    public ExecuteTasklet executeJobTasklet(){
        ExecuteTasklet tasklet = new ExecuteTasklet();
        beanFactory.autowireBean(tasklet);
        return tasklet;
    }

}
