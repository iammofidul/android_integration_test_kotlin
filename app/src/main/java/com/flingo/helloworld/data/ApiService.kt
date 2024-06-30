package com.flingo.helloworld.data
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

   /* @POST("/mdkndhb")
    suspend fun getProducts(@Body jsonObject: JsonObject): Response<ApiResponseData>*/

    @GET("posts")
    suspend fun getPosts(): Response<List<Post>>
}