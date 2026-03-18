package com.apkcontainer.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apkcontainer.data.db.entity.InstalledAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InstalledAppDao {
    @Query("SELECT * FROM installed_apps ORDER BY installedAt DESC")
    fun getAllApps(): Flow<List<InstalledAppEntity>>

    @Query("SELECT * FROM installed_apps WHERE id = :id")
    suspend fun getAppById(id: Long): InstalledAppEntity?

    @Query("SELECT * FROM installed_apps WHERE packageName = :packageName")
    suspend fun getAppByPackageName(packageName: String): InstalledAppEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertApp(app: InstalledAppEntity): Long

    @Update
    suspend fun updateApp(app: InstalledAppEntity)

    @Delete
    suspend fun deleteApp(app: InstalledAppEntity)
}
