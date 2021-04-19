package com.m4cki3.kotlin.ktor.domain.event

data class EventType(
    val domain : String,
    val obj : String,
    val operation : String,
    val version : Int
)