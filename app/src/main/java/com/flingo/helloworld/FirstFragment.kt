package com.flingo.helloworld

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flingo.helloworld.data.ApiResultHandler
import com.flingo.helloworld.data.Post
import com.flingo.helloworld.databinding.FragmentFirstBinding
import com.flingo.helloworld.vm.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPosts()
        observePostData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun getPosts() {
        mainViewModel.getPostsList()
    }
    private fun observePostData() {
        try {
            mainViewModel.responseposts.observe(this) { response ->
                val apiResultHandler = context?.let {
                    ApiResultHandler<List<Post>>(it,
                        onLoading = {
                            binding.progressBar.visibility = View.VISIBLE
                        },
                        onSuccess = { data ->
                            println(data)
                            binding.progressBar.visibility = View.GONE

                            var dataset = arrayOf("")

                            data?.let {posts ->
                                dataset = Array(posts.size) { i -> posts[i].title.toString() }

                            }
                            val customAdapter = ContactRv(dataset, object : OnItemClickListener {
                                override fun onItemClick(position: Int) {
                                    findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
                                }
                            })

                            val recyclerView: RecyclerView = binding.recyclerView
                            recyclerView.layoutManager = LinearLayoutManager(context)
                            recyclerView.adapter = customAdapter
                        },
                        onFailure = {
                            binding.progressBar.visibility = View.GONE
                        })
                }
                apiResultHandler?.handleApiResult(response)
            }
        } catch (e: Exception) {
            e.stackTrace
        }
    }
}