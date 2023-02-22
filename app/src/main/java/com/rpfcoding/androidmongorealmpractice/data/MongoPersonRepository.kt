package com.rpfcoding.androidmongorealmpractice.data

import com.rpfcoding.androidmongorealmpractice.domain.Person
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import org.mongodb.kbson.ObjectId
import javax.inject.Inject

@ExperimentalCoroutinesApi
class MongoPersonRepository @Inject constructor(
    private val realm: Realm
) : PersonRepository {

    override fun getAll(): Flow<List<Person>> {
        return realm.query<PersonEntity>().asFlow().flatMapLatest {
            flow {
                val transformedList = it.list.map { entity ->
                    Person(
                        name = entity.name,
                        age = entity.age,
                        timestamp = entity.timestamp.toInstant(),
                        id = entity._id.toHexString()
                    )
                }
                emit(transformedList)
            }
        }
    }

    override fun filterByName(name: String): Flow<List<Person>> {
        return realm.query<PersonEntity>(query = "name CONTAINS[c] $0", name).asFlow()
            .flatMapLatest {
                flow {
                    val transformedList = it.list.map { entity ->
                        Person(
                            name = entity.name,
                            age = entity.age,
                            timestamp = entity.timestamp.toInstant(),
                            id = entity._id.toHexString()
                        )
                    }
                    emit(transformedList)
                }
            }
    }

    override suspend fun insert(person: Person): String {
        var newId = ""

        realm.write {
            val personToAdd = PersonEntity().apply {
                name = person.name
                age = person.age
            }

            copyToRealm(personToAdd)

            newId = personToAdd._id.toHexString()
        }

        return newId
    }

    override suspend fun update(person: Person): Boolean {
        realm.write {
            val personToUpdate = query<PersonEntity>(query = "_id == $0", ObjectId(hexString = person.id!!)).first().find() ?: return@write
            personToUpdate.apply {
                name = person.name
            }
        }

        return true
    }

    override suspend fun deleteById(id: String): Boolean {
        var isSuccessful = false

        realm.write {
            val personToDelete = query<PersonEntity>(query = "_id == $0", ObjectId(hexString = id)).first().find() ?: return@write
            try {
                delete(personToDelete)
                isSuccessful = true
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }

        return isSuccessful
    }
}