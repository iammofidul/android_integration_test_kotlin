package com.flingo.helloworld.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope

import com.flingo.helloworld.data.Post
import com.flingo.helloworld.data.Repository
import com.flingo.helloworld.util.NetWorkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository, application: Application): BaseViewModel(application) {
    private val _responsePosts: MutableLiveData<NetWorkResult<List<Post>>> = MutableLiveData()
    val responsePosts: LiveData<NetWorkResult<List<Post>>> = _responsePosts

    fun getPostsList() = viewModelScope.launch {
        repository.getPostList(context).collect { values ->
            _responsePosts.value = values
        }
    }

}