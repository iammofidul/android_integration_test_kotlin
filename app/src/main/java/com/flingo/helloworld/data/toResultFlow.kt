package com.flingo.helloworld.data


import android.content.Context
import com.flingo.helloworld.util.Constants.Companion.API_FAILED_CODE
import com.flingo.helloworld.util.Constants.Companion.API_INTERNET_CODE
import com.flingo.helloworld.util.Constants.Companion.API_INTERNET_MESSAGE
import com.flingo.helloworld.util.NetWorkResult
import com.flingo.helloworld.util.Utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties


inline fun <reified T> toResultFlow(context: Context, crossinline call: suspend () -> Response<T>?): Flow<NetWorkResult<T>> {
    return flow {
        val isInternetConnected = Utils.hasInternetConnection(context)
        if (isInternetConnected) {
            emit(NetWorkResult.Loading( true))
            val c = call()
            c?.let { response ->
                try {
                    if (c.isSuccessful && c.body()!=null) {
                        c.body()?.let {
                            emit(NetWorkResult.Success(it))
                        }
                    } else {
                        emit(NetWorkResult.Error(null, response.message()))
                    }
                } catch (e: Exception) {
                    emit(NetWorkResult.Error(null, e.toString()))
                }
            }
        } else {
            emit(NetWorkResult.Error(null, API_INTERNET_MESSAGE))
        }
    }.flowOn(Dispatchers.IO)
}

