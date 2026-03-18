package com.apkcontainer.di

import android.content.Context
import androidx.room.Room
import com.apkcontainer.data.db.AppDatabase
import com.apkcontainer.data.db.dao.InstalledAppDao
import com.apkcontainer.data.db.dao.NetworkLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "apk_container.db"
        ).build()
    }

    @Provides
    fun provideInstalledAppDao(db: AppDatabase): InstalledAppDao = db.installedAppDao()

    @Provides
    fun provideNetworkLogDao(db: AppDatabase): NetworkLogDao = db.networkLogDao()
}
