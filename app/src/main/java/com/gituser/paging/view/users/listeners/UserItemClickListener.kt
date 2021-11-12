package com.gituser.paging.view.users.listeners

import com.gituser.paging.data.model.UserData

interface UserItemClickListener {
    fun onGitUserItemClick(userData: UserData)
    fun onFavouriteClick(userData: UserData)
}
