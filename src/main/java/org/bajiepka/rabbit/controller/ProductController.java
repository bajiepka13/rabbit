package org.bajiepka.rabbit.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.bajiepka.rabbit.rabbit.Producer;
import org.bajiepka.rabbit.domain.Product;
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
    @ApiOperation(value = "Показать всю номенклатуру", httpMethod = "GET", notes = "Выбирает все существующие записи в таблицу products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> allProducts = products.selectAll();
        return ResponseEntity.ok(allProducts);
    }

    @RequestMapping(value = "/findByName", method = RequestMethod.GET)
    @ApiOperation(value = "Find user by name", httpMethod = "GET", notes = "Выбрать номенклатуру по наименованию")
    public ResponseEntity<Product> getProductNyName(
            @RequestParam("name")
            @ApiParam(value = "Наименование номенклатуры", defaultValue = "Икра минтая") String name) {
        Product product = products.selectByName(name).orElse(emptyProduct);
        return ResponseEntity.ok(product);
    }

    @RequestMapping(value = "/sendToRabit", method = RequestMethod.POST)
    @ApiOperation(value = "Send Product to rabbit", httpMethod = "POST", notes = "Отсылаем новый продукт в RabbitMQ")
    public ResponseEntity<Product> sendToRabbit(
            @RequestParam("name")
            @ApiParam(value = "Наименование", defaultValue = "Кабачковая икра", type = "string")
                    String name,
            @RequestParam("description")
            @ApiParam(value = "Описание", defaultValue = "Фабрика Сорокино, 250 мл", type = "string")
                    String description,
            @RequestParam("cost")
            @ApiParam(value = "Цена", defaultValue = "125.00", type = "float")
                    Double cost,
            @RequestParam("weight")
            @ApiParam(value = "Вес", defaultValue = "250", type = "int")
                    Integer weight
    ){
        Product product = new Product(name, description, cost, weight);
        producer.produce(product);
        return ResponseEntity.ok(product);
    }

    @RequestMapping(value = "/processCsvFile", method = RequestMethod.GET)
    @ApiOperation(value = "Обрабатывает номенклатуру в файле, который находится по указанному пути", httpMethod = "GET", notes = "Обрабатываем файл и сохраняем результаты в базу")
    public ResponseEntity executeJob(@RequestParam("file") @ApiParam(value = "Наименование файла", defaultValue = "import-products.csv") String file) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("type", file)
                .toJobParameters();
        return ResponseEntity.ok(jobLauncher.run(job, jobParameters).getExitStatus().getExitCode());
    }

    @RequestMapping(value = "/clearProducts", method = RequestMethod.DELETE)
    @ApiOperation(value = "Очистка таблицы номенклатуры", httpMethod = "DELETE", notes = "Сервисная функция для очистки таблицы.")
    public ResponseEntity clearProducts(){
        products.clear();
        return ResponseEntity.ok("Очищено.");
    }

}
