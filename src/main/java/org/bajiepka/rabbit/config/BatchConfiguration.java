package org.bajiepka.rabbit.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.bajiepka.rabbit.batch.JobCompletionNotificationListener;
import org.bajiepka.rabbit.batch.ProductItemProcessor;
import org.bajiepka.rabbit.batch.ProductRandomItemProcessor;
import org.bajiepka.rabbit.entity.Product;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
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
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    private SqlSessionFactory sessionFactory;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    //region service

    //endregion

    //region Readers
    @Bean(name = "csvReader")
    public FlatFileItemReader<Product> csvReader() {
        return new FlatFileItemReaderBuilder<Product>()
                .name("productItemReader")
                .resource(new ClassPathResource("import-products.csv"))
                .delimited().delimiter(";")
                .names(new String[]{"name", "description"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<Product>() {{
                    setTargetType(Product.class);
                }})
                .encoding("UTF-8")
                .build();
    }

    @Bean(name = "myBatisReader")
    public MyBatisCursorItemReader<Product> batisReader() {
        MyBatisCursorItemReader<Product> reader = new MyBatisCursorItemReader<Product>();
        reader.setQueryId("org.bajiepka.rabbit.mappers.ProductMapper.selectAll");
        reader.setSqlSessionFactory(sessionFactory);
        return reader;
    }
    //endregion

    //region Writers
    @Bean(destroyMethod = "")
    public StaxEventItemWriter<Product> staxEventItemWriter(Jaxb2Marshaller marshaller) {
        StaxEventItemWriter<Product> writer = new StaxEventItemWriter<>();
        writer.setResource(new FileSystemResource("D:\\dev\\demos\\rabbit\\src\\main\\resources\\products.xml")); //    new ClassPathResource(""));
        writer.setMarshaller(marshaller);
        writer.setRootTagName("products");
        return writer;
    }

    @Bean
    public MyBatisBatchItemWriter<Product> writer(SqlSessionFactory factory){
        return new MyBatisBatchItemWriterBuilder<Product>()
                .sqlSessionFactory(factory)
                .statementId("org.bajiepka.rabbit.mappers.ProductMapper.save")
                .build();
    }
    //endregion

    //region Processors
    @Bean
    public ProductRandomItemProcessor csvToDbProcessor() {
        return new ProductRandomItemProcessor();
    }

    public ProductItemProcessor dbToXmlProcessor(){
        return new ProductItemProcessor();
    }

    //endregion

    //region Jobs
    @Bean(name = "csvToPostgresJob")
    public Job importProductsJob(JobCompletionNotificationListener listener,
                             @Qualifier("csvToDb") Step step1,
                             @Qualifier("dbToXml") Step step2) {
        return jobBuilderFactory.get("importProductsJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .next(step2)
                .end()
                .build();
    }
    //endregion

    //region Steps
    @Bean(name = "csvToDb")
    public Step step1(MyBatisBatchItemWriter<Product> writer) {
        return stepBuilderFactory.get("step1")
                .<Product, Product> chunk(20)
                .reader(csvReader())
                .processor(csvToDbProcessor())
                .writer(writer)
                .build();
    }

    @Bean(name = "dbToXml")
    public Step step2(StaxEventItemWriter<Product> writer) {
        return stepBuilderFactory.get("step2")
                .<Product, Product> chunk(20)
                .reader(batisReader())
                .processor(dbToXmlProcessor())
                .writer(writer)
                .build();
    }
    //endregion




}
