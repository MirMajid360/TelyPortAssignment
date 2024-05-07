package com.majid.androidassignment.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.majid.androidassignment.db.DBDEFINITIONS.KEY_PREF_NAME
import com.majid.androidassignment.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SharedPreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(KEY_PREF_NAME, Context.MODE_PRIVATE)
    }
}
