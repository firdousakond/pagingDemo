package com.gituser.paging

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gituser.paging.data.model.UserData
import com.gituser.paging.data.room.AppDatabase
import com.gituser.paging.data.room.UserDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RoomDbQueriesTest {

    private lateinit var userDao: UserDao
    private lateinit var db: AppDatabase

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineScopeTestRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).allowMainThreadQueries().build()
        userDao = db.userDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertUsersAndTestIfSuccess() {
        runBlockingTest {
            userDao.insertAll(testUsers)
            val savedUsers = userDao.getAllUsers()
            assertTrue(savedUsers.contains(testUsers[1]))
        }
    }

    @Test
    @Throws(Exception::class)
    fun insertUsersAndUpdateTest() {
        runBlockingTest {
            userDao.insertAll(testUsers)
            testUsers[0].isFavourite = 1
            userDao.updateFavourite(testUsers[0])
            val savedUsers = userDao.getAllUsers()
            assertTrue(savedUsers[0].isFavourite == 1)
        }
    }

    @Test
    @Throws(Exception::class)
    fun fetchFavouriteUsersTest() {
        runBlockingTest {
            testUsers[1].isFavourite = 1
            userDao.insertAll(testUsers)
            val favUsers = userDao.getFavouriteUsers()
            assertTrue(favUsers.isNotEmpty())
        }
    }

    companion object {

        val testUsers = listOf(
            UserData(
                id = 1,
                login = "Captain America",
                url = "https://www.marvel.com",
                avatar_url = "ttps://www.marvel.com/CA",
                type = "User",
                isFavourite = 0
            ),
            UserData(
                id = 2,
                login = "Black Widow",
                url = "https://www.marvel.com",
                avatar_url = "https://www.marvel.com/BW",
                type = "User",
                isFavourite = 0
            ),
            UserData(
                id = 3,
                login = "Hawk Eye",
                url = "https://www.marvel.com",
                avatar_url = "ttps://www.marvel.com/HE",
                type = "User",
                isFavourite = 0
            )
        )
    }

}
