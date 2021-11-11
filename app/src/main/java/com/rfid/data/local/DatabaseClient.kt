package com.rfid.data.local

import android.content.Context
import androidx.room.Room
import com.rfid.data.local.user.UserDao

object DatabaseClient {

    private lateinit var appDatabase: AppDatabase

    fun createDatabase(context: Context) {
        appDatabase = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "database"
        ).build()
    }

    fun userDao(): UserDao {
        return appDatabase.userDao()
    }
}