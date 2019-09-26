package org.bajiepka.rabbit.rabbit;

import org.bajiepka.rabbit.domain.Product;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Producer {

    @Autowired
    @Qualifier(value = "myRabbitTemplate")
    private AmqpTemplate amqpTemplate;

    @Value("${jsa.rabbitmq.exchange}")
    private String exchange;

    @Value("${jsa.rabbitmq.routingkey}")
    private String routingkey;

    public void produce(Product product){
        amqpTemplate.convertAndSend(exchange, routingkey, product);
        System.out.println("Send msg = " + product);
    }

}
