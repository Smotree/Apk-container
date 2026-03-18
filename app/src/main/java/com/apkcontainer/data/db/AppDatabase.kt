package com.apkcontainer.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.apkcontainer.data.db.dao.InstalledAppDao
import com.apkcontainer.data.db.dao.NetworkLogDao
import com.apkcontainer.data.db.entity.InstalledAppEntity
import com.apkcontainer.data.db.entity.NetworkLogEntity

@Database(
    entities = [
        InstalledAppEntity::class,
        NetworkLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun installedAppDao(): InstalledAppDao
    abstract fun networkLogDao(): NetworkLogDao
}
