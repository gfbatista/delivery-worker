package com.deliveryworker.domain.payment.entity

import com.deliveryworker.domain.order.entity.enum.MethodEnum
import com.deliveryworker.domain.order.entity.enum.StatusEnum
import java.util.UUID

data class Payment(
    val uuid: UUID,
    val amount: Double,
    val status: StatusEnum,
    val method: MethodEnum,
)
