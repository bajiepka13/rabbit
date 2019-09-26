package org.bajiepka.rabbit.batch;

import org.bajiepka.rabbit.entity.Product;
import org.springframework.batch.item.ItemProcessor;

import java.util.Random;

public class ProductRandomItemProcessor implements ItemProcessor<Product, Product> {

    private Random random = new Random();

    @Override
    public Product process(final Product item) throws Exception {

        return new Product(
                item.getName().toUpperCase(),
                item.getDescription().toUpperCase(),
                Integer.valueOf(random.nextInt(1000)).doubleValue(),
                random.nextInt(200));
    }
}
