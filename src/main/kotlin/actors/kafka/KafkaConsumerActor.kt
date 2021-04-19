package com.m4cki3.kotlin.ktor.actors.kafka

import com.m4cki3.kotlin.ktor.config.CustomJsonDeserializer
import com.m4cki3.kotlin.ktor.config.CustomJsonSerializer
import com.m4cki3.kotlin.ktor.config.LoggerDelegate
import com.m4cki3.kotlin.ktor.domain.event.UserCreatedEvent
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import java.time.Duration
import java.util.*

class KafkaConsumerActor(scope: CoroutineScope, private val userChannel: SendChannel<UserCreatedEvent>) :
    CoroutineScope by scope {

    private val logger by LoggerDelegate()
    private val consumer = KafkaConsumer<String, Any>(createConfiguration())

    @ObsoleteCoroutinesApi
    operator fun invoke() = launch {
        logger.info("Start Kafka consumer")
        consumer.subscribe(listOf("customers.user"))
        try {
            while (true) {
                consumer.poll(Duration.ofSeconds(5L))
                    .forEach() { record ->
                        logger.info("Consume: $record")
                        when (record.value()) {
                            is UserCreatedEvent -> userChannel.send(record.value() as UserCreatedEvent)
                        }
                    }
                consumer.commitAsync()
            }
        } catch (e: Exception) {
            logger.error("Commit failed", e)
        } finally {
            consumer.use { consumer ->
                logger.info("Shutdown. Commit offset sync")
                consumer.commitSync()
            }
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
        props["key.deserializer"] = StringDeserializer::class.java.name
        props["value.deserializer"] = CustomJsonDeserializer::class.java.name
        props["group.id"] = "user-service"
        props["enable.auto.commit"] = false
        return props
    }
}