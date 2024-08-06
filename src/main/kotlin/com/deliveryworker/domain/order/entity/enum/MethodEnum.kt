package com.deliveryworker.domain.order.entity.enum

enum class MethodEnum(val value: String) {
    UNDEFINED_METHOD("UNDEFINED_METHOD"),
    CREDIT("CREDIT"),
    DEBIT("DEBIT");

    companion object {
        fun getEnum(value: String): MethodEnum {
            return entries.firstOrNull { it.value == value } ?: UNDEFINED_METHOD
        }
    }
}