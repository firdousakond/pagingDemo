package com.gituser.paging.utils

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.gituser.paging.R


object NetworkUtil {

    private val TAG = NetworkUtil::class.java.simpleName

    fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return if (networkInfo != null && networkInfo.isConnected) {
            true
        } else {
            Log.e("$TAG: ", context.getString(R.string.msg_network_unavailable))
            context.toastMessage(context.getString(R.string.msg_network_unavailable))
            false
        }
    }

}