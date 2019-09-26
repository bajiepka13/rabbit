package org.bajiepka.rabbit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.bajiepka.rabbit.entity.Product;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;
import java.util.Random;

@Configuration
public class BeanConfig {

    @Autowired
    private DataSource dataSource;

    @Bean
    public SqlSessionFactory sqlSessionFactory() throws Exception {

        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setVfs(SpringBootVFS.class);
        return factoryBean.getObject();
    }

    @Autowired
    ObjectMapper objectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        return mapper;
    }

    @Bean
    @Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
    public Product person() {
        return new Product();
    }

    @Bean(name = "emptyProduct")
    @Scope("singleton")
    Product emptyProduct(){
        return new Product(
                "Пустая номенклатура",
                "Сервисная сущность, отражающая пустую номенклатуру",
                0.0,
                0);
    }

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
        jaxb2Marshaller.setClassesToBeBound(Product.class);
        return jaxb2Marshaller;
    }

    @Bean
    Random randomizer(){
        return new Random();
    }

}
