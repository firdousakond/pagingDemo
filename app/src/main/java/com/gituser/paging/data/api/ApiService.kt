package com.gituser.paging.data.api

import com.gituser.paging.data.model.GitUserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("search/users")
    suspend fun getGitUsers(
        @Query("q") searchKey: String,
        @Query("per_page") pageSize: Int,
        @Query("page") page: Int
    ):  GitUserResponse

}