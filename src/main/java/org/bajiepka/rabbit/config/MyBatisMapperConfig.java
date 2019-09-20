package org.bajiepka.rabbit.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("org.bajiepka.rabbit.mappers")
public class MyBatisMapperConfig {
}
