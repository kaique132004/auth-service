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
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public TopicExchange authExchange() {
        return new TopicExchange("auth.events");
    }

    @Bean
    public Queue logsQueue() {
        return new Queue("queue.logs.authentication");
    }

    @Bean
    public Binding binding() {
        return BindingBuilder.bind(logsQueue()).to(authExchange()).with("auth.users.*.*");
    }


    @Bean
    public Queue emailQueue() {
        return new Queue("queue.email.new-users");
    }

    @Bean
    public Queue emailQueueReset() {
        return new Queue("queue.email.reset");
    }


    @Bean
    public Binding bindingLogsLogin() {
        return BindingBuilder.bind(logsQueue()).to(authExchange()).with("auth.users.login.*");
    }

    @Bean
    public Binding bindingLogsUpdated() {
        return BindingBuilder.bind(logsQueue()).to(authExchange()).with("auth.users.updated.*");
    }

    @Bean
    public Binding bindingEmailNewUser() {
        return BindingBuilder.bind(emailQueue()).to(authExchange()).with("auth.users.created.*");
    }

    @Bean
    public Binding bindingEmailResetPassword() {
        return BindingBuilder.bind(emailQueueReset()).to(authExchange()).with("auth.users.reset");
    }

}
