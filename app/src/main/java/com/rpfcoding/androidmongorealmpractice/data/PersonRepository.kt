package com.rpfcoding.androidmongorealmpractice.data

import com.rpfcoding.androidmongorealmpractice.domain.Person
import kotlinx.coroutines.flow.Flow

interface PersonRepository {
    fun getAll(): Flow<List<Person>>
    fun filterByName(name: String): Flow<List<Person>>
    suspend fun insert(person: Person): String
    suspend fun update(person: Person): Boolean
    suspend fun deleteById(id: String): Boolean
}