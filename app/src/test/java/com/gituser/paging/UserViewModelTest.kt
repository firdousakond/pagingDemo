package com.gituser.paging

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.PagingData
import com.gituser.paging.data.model.UserData
import com.gituser.paging.data.repository.GitUserRepository
import com.gituser.paging.data.room.AppDatabase
import com.gituser.paging.di.appModule
import com.gituser.paging.di.repoModule
import com.gituser.paging.di.viewModelModule
import com.gituser.paging.utils.Event
import com.gituser.paging.view.users.viewmodel.GitUserViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.HttpException

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UserViewModelTest : KoinTest {

    private val userRepository: GitUserRepository by inject()
    private val appDatabase: AppDatabase by inject()

    @Mock
    private lateinit var observer: Observer<Event<String>>

    @Captor
    private lateinit var argumentCaptor: ArgumentCaptor<Event<String>>

    @Mock
    lateinit var httpException: HttpException

    private lateinit var userViewModel: GitUserViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    @Before
    fun setup() {

        startKoin {
            androidContext(mock(Context::class.java))
            modules(listOf(appModule, viewModelModule, repoModule))
        }
        userViewModel = GitUserViewModel(userRepository, appDatabase)
    }

    @Test
    fun `check user flow not null`() {
        val flow: Flow<PagingData<UserData>> = userViewModel.getGitUsers("test")
        assertNotNull(flow)
    }

    @Test
    fun `set http exception test`() {
        runBlockingTest {

            userViewModel.errorMessage.observeForever(observer)
            userViewModel.setHttpException(httpException)
            verify(observer, times(1)).onChanged(argumentCaptor.capture())
            val value = argumentCaptor.value
            assertEquals(value.getContentIfNotHandled(),"Something went wrong")
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }


}