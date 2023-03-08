package com.rpfcoding.androidmongorealmpractice.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rpfcoding.androidmongorealmpractice.data.MongoPersonRepository
import com.rpfcoding.androidmongorealmpractice.domain.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class HomeViewModel : ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _objectId = MutableStateFlow("")
    val objectId = _objectId.asStateFlow()

    private val _filtered = MutableStateFlow(false)
    val filtered = _filtered.asStateFlow()

    private val _data = MutableStateFlow<List<Person>>(emptyList())
    val data = _data.asStateFlow()

    private var fetchPeopleJob: Job? = null

    init {
        initPeopleJob()
    }

    private fun initPeopleJob(shouldReset: Boolean = false) {
        fetchPeopleJob = viewModelScope.launch {
            MongoPersonRepository.getAll().collect { people ->
                if (shouldReset) {
                    _filtered.update { false }
                    _name.update { "" }
                    _objectId.update { "" }
                }
                _data.update { people }
            }
        }
    }

    fun updateName(value: String) {
        _name.update { value }
    }

    fun updateObjectId(value: String) {
        _objectId.update { value }
    }

    fun insertPerson() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_name.value.isNotBlank()) {
                _objectId.update {
                    MongoPersonRepository.insert(
                        Person(
                            name = _name.value,
                            age = Random.nextInt(12, 50),
                        )
                    ) ?: ""
                }
            }
        }
    }

    fun updatePerson() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_objectId.value.isNotBlank() && _name.value.isNotBlank()) {
                val personToUpdate =
                    _data.value.firstOrNull { it.id == _objectId.value } ?: return@launch

                MongoPersonRepository.update(
                    personToUpdate.copy(
                        name = _name.value,
                        id = _objectId.value
                    )
                )
            }
        }
    }

    fun deletePerson() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_objectId.value.isNotBlank()) {
                MongoPersonRepository.deleteById(_objectId.value)
            }
        }
    }

    fun filterData() {
        fetchPeopleJob?.cancel()
        fetchPeopleJob = null

        viewModelScope.launch(Dispatchers.IO) {
            if (_filtered.value) {
//                personRepository.getAll().collect { people ->
//                    _filtered.update { false }
//                    _name.update { "" }
//                    _data.update { people }
//                }

                initPeopleJob(shouldReset = true)
            } else {
                MongoPersonRepository.filterByName(_name.value).collect { people ->
                    _filtered.update { true }
                    _data.update { people }
                }
            }
        }
    }
}