package com.rpfcoding.androidmongorealmpractice.data

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class PersonEntity : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId.invoke()
    var name: String = ""
    var age: Int = 0
    var timestamp: RealmInstant = RealmInstant.now()
    var owner_id: String = ""
}