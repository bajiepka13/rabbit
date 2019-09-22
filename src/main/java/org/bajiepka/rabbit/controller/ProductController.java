package org.bajiepka.rabbit.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.bajiepka.rabbit.rabbit.Producer;
import org.bajiepka.rabbit.entity.Product;
import org.bajiepka.rabbit.mappers.ProductMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("csvToPostgresJob")
    private Job job;

    @Autowired
    private ProductMapper products;

    @Autowired
    Producer producer;

    @Autowired
    @Qualifier("emptyProduct")
    private Product emptyProduct;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @Operation(summary = "Find all users", method = "GET", description = "Выбрать всю номенклатуру")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> allProducts = products.selectAll();
        return ResponseEntity.ok(allProducts);

    }

    @RequestMapping(value = "/findByName", method = RequestMethod.GET)
    @Operation(summary = "Find user by name", method = "GET", description = "Выбрать номенклатуру по наименованию")
    public ResponseEntity<Product> getProductNyName(@RequestParam("name") String name) {
        Product product = products.selectByName(name).orElse(emptyProduct);
        return ResponseEntity.ok(product);
    }

    @RequestMapping(value = "/sendToRabit", method = RequestMethod.GET)
    @Operation(summary = "Send Product to rabbit", method = "GET", description = "Отсылаем новый продукт в RabbitMQ")
    public ResponseEntity<Product> sendToRabbit(@RequestParam("name") String name, @RequestParam("description") String description){
        Product product = new Product(name, description);
        producer.produce(product);
        return ResponseEntity.ok(product);
    }

    @RequestMapping(value = "/processCsvFile", method = RequestMethod.GET)
    @Operation(summary = "Обрабатывает номенклатуру в файле, который находится по указанному пути", method = "GET", description = "Обрабатываем файл и сохраняем результаты в базу")
    public ResponseEntity executeJob(@RequestParam("file") String file) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("type", file)
                .toJobParameters();
        jobLauncher.run(job, jobParameters).getExitStatus().getExitCode();
        return ResponseEntity.ok("Запущено выполнение задания по парсингу CSV файла.");
    }
}
