package com.gituser.paging

import android.app.Application
import com.gituser.paging.di.appModule
import com.gituser.paging.di.repoModule
import com.gituser.paging.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class GitUserApp : Application(){

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@GitUserApp)
            modules(listOf(appModule, repoModule, viewModelModule))
        }
    }
}