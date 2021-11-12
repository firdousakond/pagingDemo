package com.gituser.paging.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.gituser.paging.data.model.UserData
import com.gituser.paging.data.repository.GitUserRepository
import com.gituser.paging.utils.PAGE_SIZE
import com.gituser.paging.view.users.viewmodel.GitUserViewModel
import retrofit2.HttpException
import java.lang.Exception

class UserPagingSource(
    private val gitUserRepository: GitUserRepository,
    private val searchKey: String,
    private val viewModel: GitUserViewModel
) : PagingSource<Int, UserData>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserData> {
        val page = params.key ?: PAGE_INDEX
        return try {
            val response = gitUserRepository.getUsers(
                searchKey = searchKey,
                offset = page,
                pageSize = PAGE_SIZE
            )

            val users = response.items
            insertUsersIntoDb(users)
            val prevKey = if (page == PAGE_INDEX) null else page
            val nextKey =
                if (response.items.isNullOrEmpty()) {
                    null
                } else {
                    page.plus(PAGE_INDEX)
                }

            Log.i("$TAG: ", "PrevKey - $prevKey NextKey - $nextKey")

            LoadResult.Page(
                data = users,
                prevKey = prevKey,
                nextKey = nextKey
            )

        } catch (e: Exception) {
            Log.e("$TAG:", "$e")
            if (e is HttpException) {
                viewModel.setHttpException(e)
            }
            return LoadResult.Error(e)
        }
    }

    private suspend fun insertUsersIntoDb(users: List<UserData>) {
        val result = viewModel.insertAllUser(users)
        if (result.isNotEmpty()) {
            Log.i("$TAG:", "Users saved successfully")
        } else {
            Log.i("$TAG:", "Failed to save Users")
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UserData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    companion object {
        private const val PAGE_INDEX = 1
        private val TAG = UserPagingSource::class.java.simpleName
    }

}