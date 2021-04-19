package com.m4cki3.kotlin.ktor.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.m4cki3.kotlin.ktor.domain.event.EventType
import com.m4cki3.kotlin.ktor.domain.event.UserCreatedEvent
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer

class CustomJsonSerializer : Serializer<Any> {
    override fun serialize(topic: String?, data: Any?): ByteArray? =
        jacksonObjectMapper().defaultConfig().writeValueAsBytes(data)
}

class CustomJsonDeserializer : Deserializer<Any> {
    override fun deserialize(topic: String?, data: ByteArray): Any {
        val event: Event = jacksonObjectMapper().defaultConfig().readValue(data)
        val clazz = EventMapper.mappings.find { it.type == event.type }?.clazz
        return jacksonObjectMapper().defaultConfig().readValue(data, clazz)
    }

    data class Event(
        val type: EventType
    )
}

object EventMapper {
    val mappings = listOf<EventMapping>()
        .plus(
            EventMapping(
                type = EventType(domain = "customers", obj = "user", operation = "create", version = 1),
                clazz = UserCreatedEvent::class.java
            )
        )
}

data class EventMapping(
    val type: EventType,
    val clazz: Class<*>
)
