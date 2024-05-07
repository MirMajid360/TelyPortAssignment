package com.majid.androidassignment.di

import com.majid.androidassignment.repository.Repository
import com.majid.androidassignment.vewmdels.MainViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {

    @Provides
    @Singleton
    fun provideMainViewModel(repository: Repository): MainViewModel {
        return MainViewModel(repository,)
    }


   }