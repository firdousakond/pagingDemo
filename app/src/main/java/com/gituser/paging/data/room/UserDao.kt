package com.gituser.paging.data.room

import androidx.room.*
import com.gituser.paging.data.model.UserData

@Dao
interface UserDao {

        @Query("select * from UserData")
        suspend fun getAllUsers(): List<UserData>

        @Query("select * from UserData where isFavourite = 1")
        suspend fun getFavouriteUsers(): List<UserData>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertAll(users: List<UserData>): LongArray

        @Update
        suspend fun updateFavourite(user: UserData)

        @Query("DELETE FROM userdata")
        fun deleteAll()

}