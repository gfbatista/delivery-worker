package com.deliveryworker.infra.entrypoint.rabbitmq.process

import com.deliveryworker.infra.entrypoint.rabbitmq.dto.MessageDto
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ProcessDeliveryOrderMessage(
    @Value("\${spring.rabbitmq.delivery-order.parking-lot.queue.routing.key}")
    private val parkingLotRoutingKey: String,
    private val objectMapper: ObjectMapper,
    @Qualifier("rabbitTemplate")
    private val rabbitTemplate: RabbitTemplate,
) {
    private val log = LoggerFactory.getLogger(ProcessDeliveryOrderMessage::class.java)

    fun process(
        message: Message,
        queueName: String,
    ) {
        try {
            val messageDto = this.adaptMessageToDto(message)

            log.info("Processing message from queue = $queueName")

            val deliveryOrderMessage = MessageDto.Adapter.toDomain(dto = messageDto)

            log.info("Message: $deliveryOrderMessage")

            //this.publishUltraFastShippingRegistrationInteractor.execute(deliveryOrderMessage)

            log.info("Message successfully processed")
        } catch (ex: MismatchedInputException) {
            this.handleMismatchedInputException(ex, message)
        } catch (ex: Exception) {
            log.error("Failed to process the message :: Cause: ${ex.message}")

            throw ex
        }
    }

    private fun adaptMessageToDto(message: Message): MessageDto =
        this.objectMapper.readValue(message.body, MessageDto::class.java)

    private fun sendToParkingLotQueue(
        logMessage: String,
        message: Message,
    ) {
        log.warn(logMessage)
        this.rabbitTemplate.send(this.parkingLotRoutingKey, message)
    }

    private fun handleMismatchedInputException(
        ex: MismatchedInputException,
        message: Message,
    ) {
        val missingAttributeName = ex.path.last().fieldName
        val missingAttributeCompletePath =
            ex.path
                .filter { it.fieldName != null }
                .joinToString(separator = ".") { it.fieldName }

        this.sendToParkingLotQueue(
            logMessage =
            "Sending message to parking lot queue :: Cause: message is missing attribute: $missingAttributeName :: Complete path: $missingAttributeCompletePath",
            message,
        )
    }

}
