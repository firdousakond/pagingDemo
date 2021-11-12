package com.gituser.paging.di

import android.content.Context
import androidx.room.Room
import com.gituser.paging.BuildConfig
import com.gituser.paging.data.api.ApiHelper
import com.gituser.paging.data.api.ApiHelperImpl
import com.gituser.paging.data.api.ApiService
import com.gituser.paging.data.room.AppDatabase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val appModule = module {
    single { provideOkHttpClient() }
    single { provideRetrofit(get()) }
    single { provideApiService(get()) }
    single { provideRoomInstance(androidContext()) }

    single<ApiHelper> {
        return@single ApiHelperImpl(get())
    }
}

private fun provideRoomInstance(context: Context): AppDatabase{
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java, "user-db"
    ).build()
}

private fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
} else OkHttpClient
    .Builder()
    .build()

private fun provideRetrofit(
    okHttpClient: OkHttpClient
): Retrofit =
    Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .build()

private fun provideApiService(retrofit: Retrofit): ApiService =
    retrofit.create(ApiService::class.java)
