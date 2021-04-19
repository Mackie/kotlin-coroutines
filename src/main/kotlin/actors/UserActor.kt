package com.m4cki3.kotlin.ktor.actors

import com.m4cki3.kotlin.ktor.config.LoggerDelegate
import com.m4cki3.kotlin.ktor.domain.event.UserCreatedEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach

class UserActor(scope: CoroutineScope) : CoroutineScope by scope {

    private val log by LoggerDelegate()

    @ObsoleteCoroutinesApi
    operator fun invoke() = actor<UserCreatedEvent> {
        consumeEach { userCreateEvent ->
                log.info("Created: ${userCreateEvent.data}")
        }
    }
}