package com.deliveryworker.domain.order.entity

import java.util.UUID

data class Order(
    val uuid: UUID,
    val date: String,
)