package com.rpfcoding.androidmongorealmpractice.di

import com.rpfcoding.androidmongorealmpractice.data.MongoPersonRepository
import com.rpfcoding.androidmongorealmpractice.data.PersonRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindPersonRepository(impl: MongoPersonRepository): PersonRepository
}