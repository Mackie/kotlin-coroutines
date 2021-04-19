package com.m4cki3.kotlin.ktor.web.websocket

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.m4cki3.kotlin.ktor.domain.model.User
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach

fun Routing.websocket(producer: SendChannel<User>, consumer: ReceiveChannel<String>) {
    val mapper = jacksonObjectMapper()
    webSocket("/socket") {
        incoming.consumeEach { frame ->
            when (frame) {
                is Frame.Text -> {
                    try {
                        val msg: User = mapper.readValue(frame.readText())
                        producer.send(msg)
                    }catch (e: Exception){

                    }

                }
                else -> throw Exception("Cant resolve Type ${frame.frameType.name}")
            }
        }
        consumer.consumeEach {
            val node: JsonNode = mapper.valueToTree(it)
            outgoing.send(Frame.Text(node.toString()))
        }
    }
}