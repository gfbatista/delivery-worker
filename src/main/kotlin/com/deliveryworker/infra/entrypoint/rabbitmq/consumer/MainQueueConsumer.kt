package com.deliveryworker.infra.entrypoint.rabbitmq.consumer

import com.deliveryworker.infra.entrypoint.rabbitmq.process.ProcessDeliveryOrderMessage
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Service

@Service
class MainQueueConsumer(
    private val processDeliveryOrderMessage: ProcessDeliveryOrderMessage,
) {
    @RabbitListener(
        autoStartup = "\${spring.rabbitmq.delivery-order.main.queue.auto-startup}",
        queues = ["\${spring.rabbitmq.delivery-order.main.queue.name}"],
        errorHandler = "DeadLetterAndParkingLotErrorHandler",
        containerFactory = "rabbitListener",
    )
    fun consume(
        message: Message,
    ) {
        this.processDeliveryOrderMessage.process(
            message,
            queueName = message.messageProperties.consumerQueue,
        )
    }
}
