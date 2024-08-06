package com.deliveryworker.infra.config

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {
    @Value("\${spring.rabbitmq.host}")
    private lateinit var rabbitHost: String

    @Value("\${spring.rabbitmq.port}")
    private lateinit var rabbitPort: String

    @Value("\${spring.rabbitmq.username}")
    private lateinit var rabbitUsername: String

    @Value("\${spring.rabbitmq.password}")
    private lateinit var rabbitPassword: String

    @Value("\${spring.rabbitmq.virtual-host}")
    private lateinit var rabbitVirtualHost: String

    fun rabbitFactory(): ConnectionFactory {
        val connectionFactory = CachingConnectionFactory(rabbitHost, rabbitPort.toInt())
        connectionFactory.username = rabbitUsername
        connectionFactory.setPassword(rabbitPassword)
        connectionFactory.virtualHost = rabbitVirtualHost
        return connectionFactory
    }

    @Bean("rabbitTemplate")
    fun rabbitTemplate(): RabbitTemplate {
        val rabbitTemplate = RabbitTemplate(rabbitFactory())
        return rabbitTemplate
    }

    @Bean("rabbitListener")
    fun rabbitListener(): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setConnectionFactory(rabbitFactory())
        return factory
    }
}
