package com.rpfcoding.androidmongorealmpractice.domain

import java.time.Instant

data class Person(
    val name: String,
    val age: Int,
    val timestamp: Instant = Instant.now(),
    val id: String? = null
) {
    val adult: Boolean get() = age >= 18
}