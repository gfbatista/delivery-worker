package com.deliveryworker.domain.payment.entity

import com.deliveryworker.domain.order.entity.Order
import com.deliveryworker.domain.order.entity.enum.MethodEnum
import com.deliveryworker.domain.order.entity.enum.StatusEnum
import com.deliveryworker.domain.user.entity.User
import java.util.UUID

data class Payment(
    val uuid: UUID,
    val amount: String,
    val status: StatusEnum,
    val method: MethodEnum,
    val user: User,
    val order: Order
)
