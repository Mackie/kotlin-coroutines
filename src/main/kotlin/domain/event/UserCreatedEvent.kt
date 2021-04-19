package com.m4cki3.kotlin.ktor.domain.event

import com.m4cki3.kotlin.ktor.domain.model.User

class UserCreatedEvent(override val data: User) : Event<User> {
    override val key = data.id
    override val type = EventType(
        domain = "customers",
        obj = "user",
        operation = "create",
        version = 1
    )
}