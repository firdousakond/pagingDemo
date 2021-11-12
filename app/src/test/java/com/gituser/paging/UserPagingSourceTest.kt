package com.gituser.paging

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.PagingSource
import com.gituser.paging.data.model.GitUserResponse
import com.gituser.paging.data.model.UserData
import com.gituser.paging.data.paging.UserPagingSource
import com.gituser.paging.data.repository.GitUserRepository
import com.gituser.paging.di.appModule
import com.gituser.paging.di.repoModule
import com.gituser.paging.di.viewModelModule
import com.gituser.paging.view.users.viewmodel.GitUserViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.ArgumentMatchers.*
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.lang.NullPointerException
import java.lang.RuntimeException

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserPagingSourceTest : KoinTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Mock lateinit var userRepository: GitUserRepository
    private val userViewModel: GitUserViewModel by inject()
    private lateinit var userPagingSource: UserPagingSource

    @Before
    fun setUp() {
        startKoin {
            androidContext(Mockito.mock(Context::class.java))
            modules(listOf(appModule, viewModelModule, repoModule))
        }
        userPagingSource = UserPagingSource(userRepository, "dan", userViewModel)
    }

    @Test
    fun `user paging source - http error response test`() {
        runBlockingTest {
            val error = RuntimeException("404", Throwable())
            given(userRepository.getUsers(anyString(), anyInt(), anyInt())).willThrow(error)
            val expectedResult = PagingSource.LoadResult.Error<Int, UserData>(error)
            assertEquals(
                expectedResult,
                userPagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = 1,
                        loadSize = 3,
                        placeholdersEnabled = false
                    )
                )
            )
        }
    }

    @Test
    fun `user paging source load - failure - received null`() = runBlockingTest {
        given(userRepository.getUsers(anyString(), anyInt(), anyInt())).willReturn(null)
        val expectedResult = PagingSource.LoadResult.Error<Int, UserData>(NullPointerException())
        assertEquals(
            expectedResult.toString(),
            userPagingSource.load(
                PagingSource.LoadParams.Refresh(key = 0, loadSize = 1, placeholdersEnabled = false)
            ).toString()
        )
    }


    @Test
    fun `user paging source refresh - success`() = runBlockingTest {
        given(userRepository.getUsers(anyString(), anyInt(), anyInt())).willReturn(
            userResponse
        )

        val expectedResult = PagingSource.LoadResult.Page(
            data = userResponse.items,
            prevKey = null,
            nextKey = 2
        )
        assertEquals(
            expectedResult, userPagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = 1,
                    loadSize = 3,
                    placeholdersEnabled = false
                )
            )
        )
    }


    @Test
    fun `user paging source append - success`() = runBlockingTest {
        given(userRepository.getUsers(anyString(), anyInt(), anyInt())).willReturn(
            nextUserResponse
        )
        val expectedResult = PagingSource.LoadResult.Page(
            data = nextUserResponse.items,
            prevKey = 2,
            nextKey = 3
        )
        assertEquals(
            expectedResult, userPagingSource.load(
                PagingSource.LoadParams.Append(
                    key = 2,
                    loadSize = 3,
                    placeholdersEnabled = false
                )
            )
        )
    }

    @Test
    fun `user paging source prepend - success`() = runBlockingTest {
        given(userRepository.getUsers(anyString(), anyInt(), anyInt())).willReturn(
            userResponse
        )
        val expectedResult = PagingSource.LoadResult.Page(
            data = userResponse.items,
            prevKey = null,
            nextKey = 2
        )
        assertEquals(
            expectedResult, userPagingSource.load(
                PagingSource.LoadParams.Prepend(
                    key = 1,
                    loadSize = 3,
                    placeholdersEnabled = false
                )
            )
        )
    }

    companion object {

        val userResponse = GitUserResponse(
            items = listOf(
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
            ),
            total_count = 3,
            incomplete_results = true
        )

        val nextUserResponse =
            GitUserResponse(
                items = listOf(
                    UserData(
                        id = 4,
                        login = "Thor",
                        url = "https://www.marvel.com",
                        avatar_url = "https://www.marvel.com/thor",
                        type = "User",
                        isFavourite = 0
                    ),
                    UserData(
                        id = 5,
                        login = "Iron Man",
                        url = "https://www.marvel.com",
                        avatar_url = "ttps://www.marvel.com/IM",
                        type = "User",
                        isFavourite = 0
                    ),
                    UserData(
                        id = 6,
                        login = "Hulk",
                        url = "https://www.marvel.com",
                        avatar_url = "https://www.marvel.com/HULK",
                        type = "User",
                        isFavourite = 0
                    )
                ),
                total_count = 3,
                incomplete_results = true
            )
    }

    @After
    fun tearDown() {
        stopKoin()
    }


}