package org.bajiepka.rabbit.batch;

import org.bajiepka.rabbit.entity.Product;
import org.springframework.batch.item.ItemProcessor;

public class ProductItemProcessor implements ItemProcessor<Product, Product> {

    @Override
    public Product process(final Product item) throws Exception {

        return new Product(item.getName(),
                item.getDescription().toUpperCase(),
                item.getCost(),
                item.getWeight());
    }

}
