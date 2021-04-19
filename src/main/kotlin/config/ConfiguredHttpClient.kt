package com.m4cki3.kotlin.ktor.config

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.JacksonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging

object ConfiguredHttpClient {
    fun createClient() = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = JacksonSerializer() {
                this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
}