package com.flingo.helloworld.data

import com.google.gson.JsonObject
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val apiService: ApiService) {
    suspend fun getPosts() = apiService.getPosts()
}