package aero.sita.mgt.auth_service.Configurations;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue sendResetToken() {
        return new  Queue("auth.events.pass.reset");
    }

    @Bean
    public DirectExchange resetTokenDirect() {
        return new DirectExchange("auth.events.pass.reset.exchange");
    }

    @Bean
    public Binding resetTokenBinding(Queue sendResetToken,  DirectExchange resetTokenDirect) {
        return BindingBuilder.bind(sendResetToken).to(resetTokenDirect).with("auth.events.pass.reset.key");
    }

    @Bean
    public Queue newUser() {
        return new  Queue("auth.events.new.user");
    }

    @Bean
    public DirectExchange newUserDirect() {
        return new DirectExchange("auth.events.new.user.exchange");
    }

    @Bean
    public Binding newUserBinding(Queue newUser,  DirectExchange newUserDirect) {
        return BindingBuilder.bind(newUser).to(newUserDirect).with("auth.events.new.user.key");
    }
}