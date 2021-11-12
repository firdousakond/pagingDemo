package com.gituser.paging.di

import com.gituser.paging.view.users.viewmodel.GitUserViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        GitUserViewModel(get(), get())
    }
}