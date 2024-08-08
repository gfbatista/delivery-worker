package com.deliveryworker.infra.entrypoint.rabbitmq.dto

import com.deliveryworker.domain.order.entity.Order
import com.deliveryworker.domain.order.entity.enum.MethodEnum
import com.deliveryworker.domain.order.entity.enum.StatusEnum
import com.deliveryworker.domain.payment.entity.Payment
import com.deliveryworker.domain.user.entity.User
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.UUID

data class MessageDto(val payment: PaymentDto) {
    object Adapter {
        fun toDomain(dto: MessageDto): Payment {

            return Payment(
                uuid = dto.payment.uuid,
                amount = dto.payment.amount,
                status = StatusEnum.getEnum(dto.payment.status),
                method = MethodEnum.getEnum(dto.payment.method),
                user = User(uuid = dto.payment.user.uuid),
                order = Order(
                    uuid = dto.payment.order.uuid,
                    date = dto.payment.order.date
                )
            )
        }
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PaymentDto(
    val uuid: UUID,
    val amount: String,
    val status: String,
    val method: String,
    val user: UserDto,
    val order: OrderDto,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDto(
    val uuid: UUID,
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrderDto(
    val uuid: UUID,
    val date: String,
)
