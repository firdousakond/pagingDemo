package com.gituser.paging.data.api

import com.gituser.paging.data.model.GitUserResponse

interface ApiHelper {

    suspend fun getGitUsers(
        searchKey: String,
        pageSize: Int,
        page: Int
    ): GitUserResponse
}