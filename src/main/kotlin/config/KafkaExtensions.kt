package com.m4cki3.kotlin.ktor.config

import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.Logger
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface KafkaExtensions {

    suspend fun <K : Any, V : Any> KafkaProducer<K, V>.suspendSend(record: ProducerRecord<K, V>): RecordMetadata {
        return suspendCoroutine { continuation ->
            val callback = Callback { metadata, exception ->
                if (metadata == null) {
                    continuation.resumeWithException(exception)
                } else {
                    continuation.resume(metadata)
                }
            }
            this.send(record, callback)
        }
    }

    fun Logger.info(meta: RecordMetadata){
        this.info("Sent message to Kafka Broker: Topic: ${meta.topic()}, at ${meta.timestamp()} ")
    }
}
