package com.gituser.paging.di

import com.gituser.paging.data.repository.GitUserRepository
import org.koin.dsl.module

val repoModule = module {
    single {
        GitUserRepository(get())
    }
}