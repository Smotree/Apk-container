package com.apkcontainer.di

import com.apkcontainer.data.repository.AppRepositoryImpl
import com.apkcontainer.data.repository.NetworkRepositoryImpl
import com.apkcontainer.domain.repository.AppRepository
import com.apkcontainer.domain.repository.NetworkRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAppRepository(impl: AppRepositoryImpl): AppRepository

    @Binds
    @Singleton
    abstract fun bindNetworkRepository(impl: NetworkRepositoryImpl): NetworkRepository
}
