package com.rpfcoding.androidmongorealmpractice.data

import com.rpfcoding.androidmongorealmpractice.domain.Person
import com.rpfcoding.androidmongorealmpractice.domain.PersonRepository
import com.rpfcoding.androidmongorealmpractice.util.Constants
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import org.mongodb.kbson.ObjectId

@ExperimentalCoroutinesApi
object MongoPersonRepository : PersonRepository {

    private val app = App.create(Constants.APP_ID)
    private val user = app.currentUser
    private lateinit var realm: Realm

    init {
        configure()
    }

    override fun configure() {
        if (user != null) {
            val config = SyncConfiguration.Builder(
                user,
                setOf(PersonEntity::class)
            ).initialSubscriptions { sub ->
                add(query = sub.query<PersonEntity>(query = "owner_id == $0", user.id))
            }.log(LogLevel.ALL)
                .build()

            realm = Realm.open(config)
        }
    }

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

    override suspend fun insert(person: Person): String? {
        var newId: String? = null

        realm.write {
            val personToAdd = PersonEntity().apply {
                name = person.name
                age = person.age
                owner_id = user?.id ?: return@write
            }

            copyToRealm(personToAdd)

            newId = personToAdd._id.toHexString()
        }

        return newId
    }

    override suspend fun update(person: Person): Boolean {
        var isSuccessful = false

        realm.write {
            val personToUpdate = query<PersonEntity>(
                query = "_id == $0",
                ObjectId(hexString = person.id!!)
            ).first().find() ?: return@write

            personToUpdate.apply {
                name = person.name
            }

            isSuccessful = true
        }

        return isSuccessful
    }

    override suspend fun deleteById(id: String): Boolean {
        var isSuccessful = false

        realm.write {
            val personToDelete = query<PersonEntity>(
                query = "_id == $0",
                ObjectId(hexString = id)
            ).first().find() ?: return@write

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