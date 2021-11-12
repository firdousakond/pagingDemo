package com.gituser.paging.data.model

data class GitUserResponse(
    val incomplete_results: Boolean,
    val items: List<UserData>,
    val total_count: Int
)