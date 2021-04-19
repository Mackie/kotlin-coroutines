package com.m4cki3.kotlin.ktor.actors.kafka

import com.m4cki3.kotlin.ktor.config.CustomJsonSerializer
import com.m4cki3.kotlin.ktor.config.KafkaExtensions
import com.m4cki3.kotlin.ktor.config.LoggerDelegate
import com.m4cki3.kotlin.ktor.domain.event.Event
import com.m4cki3.kotlin.ktor.domain.model.User
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

class KafkaProducerActor(scope: CoroutineScope) : CoroutineScope by scope, KafkaExtensions {

    private val logger by LoggerDelegate()
    private val producer = KafkaProducer<String, Any>(createConfiguration())

    @ObsoleteCoroutinesApi
    operator fun invoke() = actor<Event<*>>(capacity = 10) {
        consumeEach { record ->
            withContext(Dispatchers.IO) {
                launch{
                    logger.info(producer.suspendSend(topicResolver(record)))
                }
            }
        }
    }

    private fun <T> topicResolver(event: Event<T>): ProducerRecord<String, Any> {
        return when (event.data) {
            is User -> ProducerRecord(event.topicName(), event.key, event)
            else -> throw Exception("No Topic Mapping found")
        }
    }

    private fun createConfiguration(): Properties {
        val config = ConfigFactory.parseResources("kafka.conf").resolve()
        val props = Properties()
        val kafkaUsername = config.getString("kafka.saslJaasUsername")
        val kafkaPassword = config.getString("kafka.saslJaasPassword")
        props["bootstrap.servers"] = config.getString("kafka.bootstrapServers")
        props["security.protocol"] = config.getString("kafka.securityProtocol")
        props["sasl.jaas.config"] = "org.apache.kafka.common.security.plain.PlainLoginModule   required username=\"$kafkaUsername\"   password=\"$kafkaPassword\";"
        props["sasl.mechanism"] = config.getString("kafka.saslMechanism")
        props["ssl.endpoint.identification.algorithm"] = config.getString("kafka.sslEnpointIdentificationAlgo")
        props["key.serializer"] = StringSerializer::class.java.name
        props["value.serializer"] = CustomJsonSerializer::class.java.name
        props["acks"] = "all"
        return props
    }
}