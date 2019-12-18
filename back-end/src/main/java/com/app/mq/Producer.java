package com.app.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class Producer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routingkey}")
    private String routingkey;

    public void setQueue(String queue) {
        this.queue = queue;
    }

    private String queue;

    public void send(String msg)
    {
        rabbitTemplate.convertAndSend(exchange, routingkey, msg);
    }

    public Queue queue(){
        return new Queue(queue, false);
    }

    public TopicExchange exchange(){
        return new TopicExchange(exchange);
    }

    public Binding binding(Queue queue, TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(routingkey);
    }
//    producer.setQueue(userRepository.findByEmail(principal.getName()).getEmail());
//    producer.queue();
//    producer.exchange();
//    producer.binding(producer.queue(), producer.exchange());
//    producer.send("Hello try");
}
