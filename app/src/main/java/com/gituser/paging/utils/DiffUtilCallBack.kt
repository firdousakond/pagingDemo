package com.gituser.paging.utils

import androidx.recyclerview.widget.DiffUtil
import com.gituser.paging.data.model.UserData

class DiffUtilCallBack : DiffUtil.ItemCallback<UserData>() {

    override fun areItemsTheSame(oldItem: UserData, newItem: UserData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserData, newItem: UserData): Boolean {
        return oldItem == newItem
    }

}