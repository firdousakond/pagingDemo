package com.gituser.paging.view.users.viewmodel

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gituser.paging.data.model.UserData
import com.gituser.paging.data.paging.UserPagingSource
import com.gituser.paging.data.repository.GitUserRepository
import com.gituser.paging.data.room.AppDatabase
import com.gituser.paging.utils.Event
import com.gituser.paging.utils.PAGE_SIZE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

class GitUserViewModel(
    private val gitUserRepository: GitUserRepository,
    private val appDatabase: AppDatabase
) : ViewModel() {

    private val _favUsers: MutableLiveData<Event<List<UserData>>>
            by lazy { MutableLiveData<Event<List<UserData>>>() }
    val favUsers: LiveData<Event<List<UserData>>>
        get() = _favUsers

    private val _errorMessage: MutableLiveData<Event<String>>
            by lazy { MutableLiveData<Event<String>>() }
    val errorMessage: LiveData<Event<String>>
        get() = _errorMessage

    fun getGitUsers(searchKey: String): Flow<PagingData<UserData>> {
        return Pager(
            PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = true)
        ) {
            UserPagingSource(gitUserRepository, searchKey, this)
        }.flow.cachedIn(viewModelScope)
    }

    suspend fun insertAllUser(users: List<UserData>): LongArray {
        var result: LongArray = longArrayOf()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dao = appDatabase.userDao()
                val favouriteUsers = dao.getFavouriteUsers()
                favouriteUsers.forEach { favUser ->
                    users.forEach userLoop@{ user ->
                        if (favUser.id == user.id) {
                            user.isFavourite = 1
                            return@userLoop
                        }
                    }
                }
                result = dao.insertAll(users)
            } catch (ex: SQLiteException) {
                Log.e("$TAG: ", "Error while inserting users $ex")
            }
        }
        return result
    }

    fun updateUserFavourite(user: UserData) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dao = appDatabase.userDao()
                dao.updateFavourite(user)
            } catch (ex: SQLiteException) {
                Log.e("$TAG: ", "Error While updating favourite user: $ex")
            }
        }
    }

    fun getFavouriteUsers() {
        var users: List<UserData>?
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dao = appDatabase.userDao()
                users = dao.getFavouriteUsers()
                _favUsers.postValue(Event(users.orEmpty()))
            } catch (ex: SQLiteException) {
                Log.e("$TAG: ", "Error while fetching favourite users $ex")
            }
        }
    }

    // For test purpose
    fun getAllUsers(): List<UserData> {
        var users: List<UserData> = ArrayList()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val dao = appDatabase.userDao()
                users = dao.getAllUsers()
            } catch (ex: SQLiteException) {
                Log.e("$TAG: ", "Error while fetching users $ex")
            }
        }
        return users
    }

    fun setHttpException(exception: HttpException) {
        val jsonObj = JSONObject(exception.response()?.errorBody()?.charStream()?.readText() ?: "")
        _errorMessage.postValue(Event(jsonObj.getString("message") ?: "Something went wrong"))
    }

    companion object {
        private val TAG = GitUserViewModel::class.java.simpleName
    }

}