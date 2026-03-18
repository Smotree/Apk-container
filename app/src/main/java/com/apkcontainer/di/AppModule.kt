package com.apkcontainer.di

import android.content.Context
import com.apkcontainer.data.apk.ApkAnalyzer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApkAnalyzer(@ApplicationContext context: Context): ApkAnalyzer {
        return ApkAnalyzer(context)
    }
}
