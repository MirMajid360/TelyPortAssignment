package com.majid.androidassignment.network

import com.majid.androidassignment.models.HackerPostResponseModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @GET("/v0/item/8863.json")
    suspend fun getPost(): Response<HackerPostResponseModel.Post>


    @GET("/v0/item/{id}.json")
    suspend fun getComments( @Path("id") id: Int): Response<HackerPostResponseModel.Post>

}