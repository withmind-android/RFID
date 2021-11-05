package com.rfid.data.local.token

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface TokenDao {
    @Query(value = "SELECT * FROM token")
    fun getToken(): Single<TokenEntity>

    @Insert(onConflict = REPLACE)
    fun saveToken(tokenEntity : TokenEntity): Completable

    @Query(value = "DELETE FROM token")
    fun clearCache(): Completable

    @Query(value = "SELECT accessToken FROM token")
    fun getAccessToken(): Single<String>
}