package com.m4cki3.kotlin.ktor.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule

fun ObjectMapper.defaultConfig(): ObjectMapper = this
    .registerModule(JavaTimeModule())
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    .enable(SerializationFeature.INDENT_OUTPUT)