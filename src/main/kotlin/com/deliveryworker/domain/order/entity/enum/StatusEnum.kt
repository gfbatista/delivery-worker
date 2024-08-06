package com.deliveryworker.domain.order.entity.enum

enum class StatusEnum(val value: String) {
    UNDEFINED_STATUS("UNDEFINED_STATUS"),
    UNPAID("UNPAID"),
    PAID("PAID");

    companion object {
        fun getEnum(value: String): StatusEnum {
            return entries.firstOrNull { it.value == value } ?: UNDEFINED_STATUS
        }
    }
}