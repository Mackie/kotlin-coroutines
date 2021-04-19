package com.m4cki3.kotlin.ktor

import com.m4cki3.kotlin.ktor.actors.UserActor
import com.m4cki3.kotlin.ktor.actors.kafka.KafkaConsumerActor
import com.m4cki3.kotlin.ktor.actors.kafka.KafkaProducerActor
import com.m4cki3.kotlin.ktor.config.defaultConfig
import com.m4cki3.kotlin.ktor.web.api.user
import freemarker.cache.ClassTemplateLoader
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.jackson.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.slf4j.event.Level

@ObsoleteCoroutinesApi
fun Application.main() {

    val userActor = UserActor(this)
    val kafkaProducer = KafkaProducerActor(this)
    KafkaConsumerActor(this, userActor()).invoke()
    install(DefaultHeaders)
    install(ContentNegotiation) {
        jackson {
            this.defaultConfig()
        }
    }
    install(CallLogging) {
        level = Level.INFO
    }
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }

    install(WebSockets)
    install(Routing) {
        user(kafkaProducer())
    }
}

