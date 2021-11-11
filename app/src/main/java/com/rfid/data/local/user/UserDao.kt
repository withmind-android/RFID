package com.rfid.data.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface UserDao {
    @Query(value = "SELECT * FROM UserEntity")
    fun getUser(): Single<UserEntity>
    @Insert(onConflict = REPLACE)
    fun saveUser(userEntity : UserEntity): Completable
    @Query(value = "DELETE FROM UserEntity")
    fun deleteUser(): Completable
    @Query(value = "SELECT token FROM UserEntity")
    fun getToken(): Single<String>
    @Query(value = "UPDATE UserEntity SET token = :token WHERE id = 0")
    fun updateToken(token: String): Completable
}