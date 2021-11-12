package com.gituser.paging.data.repository

import com.gituser.paging.data.api.ApiHelper

class GitUserRepository(private val apiHelper: ApiHelper) {

    suspend fun getUsers(
        searchKey: String,
        pageSize: Int,
        offset: Int
    ) = apiHelper.getGitUsers( searchKey, pageSize, offset)

}