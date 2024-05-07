package com.majid.androidassignment.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.majid.androidassignment.databinding.FragmentCommentsBinding
import com.majid.androidassignment.utils.IListeners
import com.majid.androidassignment.utils.hideVisibility
import com.majid.androidassignment.utils.showVisibility
import com.majid.androidassignment.vewmdels.MainViewModel
import com.majid.androidassignment.view.MainActivity
import com.majid.androidassignment.view.adapters.CommentsAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CommentsFragment : Fragment() {

    private lateinit var binding: FragmentCommentsBinding

    @Inject
    lateinit var mainViewModel: MainViewModel

    private lateinit var commentsAdapter: CommentsAdapter
    private val commentList: ArrayList<Int> by lazy { ArrayList<Int>() }

    var currentPosition = 0
    lateinit var commentsLayoutManager: LinearLayoutManager
    private var isLoadingMore = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommentsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCommentsAdapter()
        setObserver()
    }



    private fun setObserver() {
        mainViewModel.postData.observe(viewLifecycleOwner, Observer {
            commentList.clear()
            commentList.addAll(it.kids)

            loadComments()
        })
    }

    private fun loadComments() {
        for (i in currentPosition until (currentPosition + 4).coerceAtMost(commentList.size)) {
            val item = commentList[i]
            mainViewModel.getComment(item).observe(viewLifecycleOwner, Observer {
                commentsAdapter.addItem(it)
                isLoadingMore = false
                binding.loadMoreProgressbar.hideVisibility()
            })
        }
        currentPosition += 4
        if (currentPosition >= commentList.size) {
            mainViewModel.setSnackbar(arrayOf("No more comments available", false))
            binding.loadMoreProgressbar.hideVisibility()
        }
    }

    private fun initCommentsAdapter() {
        commentsAdapter = CommentsAdapter(
            requireContext(),
            arrayListOf(),
            object : IListeners.IClickListeners {
                override fun onItemClicked(model: Any, pos: Int) {
                    // Handle item click if needed
                }
            }
        )
        commentsLayoutManager = LinearLayoutManager(activity)
        binding.recyclerView.layoutManager = commentsLayoutManager
        binding.recyclerView.adapter = commentsAdapter
        initRecyclerViewScrollListener()
    }

    private fun initRecyclerViewScrollListener() {
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition = commentsLayoutManager.findLastCompletelyVisibleItemPosition()
                val totalItemCount = commentsLayoutManager.itemCount

                // Check if the user is scrolling down and has reached the last item
                if (dy > 0 && lastVisibleItemPosition == totalItemCount - 1 && !isLoadingMore) {
                    loadMoreData()
                }
            }
        })
    }

    private fun loadMoreData() {
        binding.loadMoreProgressbar.showVisibility()
        isLoadingMore = true
        loadComments()
    }

    companion object {
        @JvmStatic
        fun newInstance() = CommentsFragment()
    }
}
