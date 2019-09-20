package org.bajiepka.rabbit;

import org.bajiepka.rabbit.mappers.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class RabbitApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RabbitApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Running task");
    }
}
