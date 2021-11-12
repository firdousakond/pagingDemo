package com.gituser.paging.data.api

import com.gituser.paging.data.model.GitUserResponse

class ApiHelperImpl(private val apiService: ApiService) : ApiHelper {

    override suspend fun getGitUsers(
        searchKey: String,
        pageSize: Int,
        page: Int
    ): GitUserResponse = apiService.getGitUsers(searchKey,pageSize,page)

}