package com.dev.ldhnam.kotlinsample.di.module

import android.content.Context
import com.dev.ldhnam.kotlinsample.AndroidApplication
import com.dev.ldhnam.kotlinsample.BuildConfig
import com.dev.ldhnam.kotlinsample.api.RestService
import com.dev.ldhnam.kotlinsample.util.Constant
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule(private val application: AndroidApplication) {

    @Provides
    @Singleton
    fun provideContext(): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideCache(): Cache {
        val cacheSize: Long = 10 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize)
    }

    @Provides
    @Singleton
    fun provideHttpLogging(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG)
            HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache, httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .followSslRedirects(true)
                .cache(cache)
                .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    @Provides
    @Singleton
    fun provideRestService(retrofit: Retrofit): RestService = retrofit.create(RestService::class.java)
}
