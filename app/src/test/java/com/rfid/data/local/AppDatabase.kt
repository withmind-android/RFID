package com.rfid.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rfid.data.local.user.UserDao
import com.rfid.data.local.user.UserEntity

@Database(entities = [UserEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}