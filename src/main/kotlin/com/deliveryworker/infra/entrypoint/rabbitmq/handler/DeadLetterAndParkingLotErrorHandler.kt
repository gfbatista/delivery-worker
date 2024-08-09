package com.deliveryworker.infra.entrypoint.rabbitmq.handler

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component(value = "DeadLetterAndParkingLotErrorHandler")
class DeadLetterAndParkingLotErrorHandler(
    @Value("\${spring.rabbitmq.delivery-order.main.queue.max-retry-attempts}")
    private val maxRetryAttempts: Int,
    @Value("\${spring.rabbitmq.delivery-order.main.queue.ttl-retry}")
    private val ttlRetry: Int,
    @Value("\${spring.rabbitmq.delivery-order.parking-lot.queue.routing.key}")
    private val parkingLotRoutingKey: String,
    @Value("\${spring.rabbitmq.delivery-order.wait.queue.exchange.name}")
    private val waitQueueExchangeName: String,
    @Value("\${spring.rabbitmq.delivery-order.wait.queue.routing.key}")
    private val waitQueueRoutingKey: String,
    @Qualifier("rabbitTemplate")
    private val rabbitTemplate: RabbitTemplate,
) : RabbitListenerErrorHandler {
    companion object {
        const val X_RETRY_COUNT = "x-retry-count"
    }

    private val log = LoggerFactory.getLogger(DeadLetterAndParkingLotErrorHandler::class.java)

    override fun handleError(
        message: Message,
        message1: org.springframework.messaging.Message<*>?,
        e: ListenerExecutionFailedException,
    ): Any? {
        val retryAttempts = this.getRetryAttempts(message)

        if (retryAttempts > this.maxRetryAttempts) {
            log.info("Retries exceeded :: Putting message into parking lot")

            message.messageProperties.setHeader(X_RETRY_COUNT, 0)

            this.rabbitTemplate.send(
                this.parkingLotRoutingKey,
                message,
            )

            return null
        }

        log.info("Rejecting message and sending to wait queue :: Attempt={}", retryAttempts)

        message.messageProperties.expiration = (this.ttlRetry * retryAttempts).toString()
        message.messageProperties.setHeader(X_RETRY_COUNT, retryAttempts)

        this.rabbitTemplate.send(
            this.waitQueueExchangeName,
            this.waitQueueRoutingKey,
            message,
        )

        return null
    }

    private fun getRetryAttempts(message: Message): Int {
        return (message.messageProperties.headers[X_RETRY_COUNT] as? Int)?.let { it + 1 } ?: 1
    }
}
