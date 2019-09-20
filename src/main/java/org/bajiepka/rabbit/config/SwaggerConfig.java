package org.bajiepka.rabbit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
public class SwaggerConfig {

    @Bean
    Docket docket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.bajiepka.rabbit.controller"))
                .paths(PathSelectors.ant("/products/*"))
                .build()
                .apiInfo(retrieveApiInto());

    }

    private ApiInfo retrieveApiInto() {
        return new ApiInfo("Rabbit application",
                "Spring Boot WebMVC + Swagger + PostgreSQL + MyBatis application",
                "1.0",
                "http://no-terms-of-usage.really.com",
                new Contact("Valerii C.", "www.nourl.com", "nomail@mail.ru"),
                "Use this application at own risk.",
                "nolicense.com",
                Collections.emptyList());
    }

}
