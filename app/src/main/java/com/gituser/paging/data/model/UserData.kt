package com.gituser.paging.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
@Entity
@Parcelize
data class UserData(

    @PrimaryKey
    val id: Int,
    val avatar_url: String,
    val login: String,
    val url: String,
    val type: String,
    var isFavourite : Int = 0
): Parcelable