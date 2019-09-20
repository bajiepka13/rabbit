package org.bajiepka.rabbit.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.bajiepka.rabbit.entity.Product;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.sql.DataSource;

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

    @Bean(name = "emptyProduct")
    @Scope("singleton")
    Product emptyProduct(){
        return new Product(
                "Пустая номенклатура",
                "Сервисная сущность, отражающая пустую номенклатуру");
    }
}
