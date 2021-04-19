package com.m4cki3.kotlin.ktor.domain.event

interface Event<T>{

    val key : String
    val type: EventType
    val data: T

    fun topicName() = "${type.domain}.${type.obj}"
    fun eventName() = "${topicName()}.${type.operation}.${type.version}"
}