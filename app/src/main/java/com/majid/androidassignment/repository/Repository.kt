package com.majid.androidassignment.repository

import com.majid.androidassignment.models.HackerPostResponseModel
import com.majid.androidassignment.network.ApiService
import retrofit2.Response
import javax.inject.Inject

class Repository@Inject constructor(
    private val apiService: ApiService,
)  {



    suspend fun getPost(): Response<HackerPostResponseModel.Post> {

        return apiService.getPost()
    }

    suspend fun getComments(id :Int): Response<HackerPostResponseModel.Post> {

        return apiService.getComments(id)
    }

}