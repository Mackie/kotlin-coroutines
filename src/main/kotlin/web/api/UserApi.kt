package com.m4cki3.kotlin.ktor.web.api

import com.m4cki3.kotlin.ktor.config.ConfiguredHttpClient
import com.m4cki3.kotlin.ktor.config.LoggerDelegate
import com.m4cki3.kotlin.ktor.domain.event.Event
import com.m4cki3.kotlin.ktor.domain.event.UserCreatedEvent
import com.m4cki3.kotlin.ktor.domain.model.User
import io.ktor.application.call
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.request.*
import io.ktor.response.respond
import io.ktor.routing.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.SendChannel

fun Routing.user(channel: SendChannel<Event<*>>) {
    route("/users") {
        get("/") {
            val delayTimeMillis = call.request.queryParameters["delay"]?.toLong()?.let { it * 5000 }
            call.respond(ConcurrentApiCalls.fetch(delayTimeMillis))
        }

        get ("/{id}"){
            call.parameters["id"]?.let {
                call.respond(User(it, "User $it"))
            } ?: run {
                call.respond(HttpStatusCode.BadRequest, "No id?")
            }
        }
        post("/") {
            call.receive<User>().let { user ->

                channel.send(UserCreatedEvent(user))
                call.respond(it)
            }
        }
    }
}

object ConcurrentApiCalls {

    private const val BASE_URL = "http://localhost:8080"

    suspend fun fetch(delayTimeMillis: Long?) = coroutineScope<List<User>> {
        ConfiguredHttpClient.createClient().let { client ->
            listOf<Deferred<User>>()
                .plus(async {
                    delayTimeMillis?.let { delay(it) }
                    client.get<User>("$BASE_URL/users/1")
                })
                .plus(async {
                    delayTimeMillis?.let { delay(it) }
                    client.get<User>("$BASE_URL/users/2")
                })
                .plus(async {
                    delayTimeMillis?.let { delay(it) }
                    client.get<User>("$BASE_URL/users/3")
                })
                .awaitAll()
        }
    }
}
