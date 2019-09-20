package org.bajiepka.rabbit;

import org.bajiepka.rabbit.entity.Product;
import org.bajiepka.rabbit.mappers.ProductMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Consumer {

    @Autowired
    private ProductMapper products;

    @RabbitListener(queues="${jsa.rabbitmq.queue}", containerFactory="jsaFactory")
    public void recievedMessage(Product product) {
        products.save(product);
    }
}
