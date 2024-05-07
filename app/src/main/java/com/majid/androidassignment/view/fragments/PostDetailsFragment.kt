package com.majid.androidassignment.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.majid.androidassignment.R
import com.majid.androidassignment.databinding.FragmentPostDetailsBinding
import com.majid.androidassignment.utils.Utils
import com.majid.androidassignment.utils.capitalizeFirstLetter
import com.majid.androidassignment.vewmdels.MainViewModel
import com.majid.androidassignment.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class PostDetailsFragment : Fragment() {
    private lateinit var binding: FragmentPostDetailsBinding

    @Inject
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()
        setObserver()
        setListeners()

    }

    private fun setObserver() {
        mainViewModel.isCommentFragmentShown.observe(viewLifecycleOwner, Observer {
            if (it){
                binding.btnViewComments.text = resources.getString(R.string.hide_comment)

            }else{
                binding.btnViewComments.text = resources.getString(R.string.view_comment)

            }
        })
    }

    private fun setListeners() {
        binding.btnViewComments.setOnClickListener {
            if (binding.btnViewComments.text.equals(resources.getString(R.string.view_comment))){
                binding.btnViewComments.text = resources.getString(R.string.hide_comment)
                mainViewModel.setOpenCommentsFragment(arrayOf(1,2))
            }else{
                binding.btnViewComments.text = resources.getString(R.string.view_comment)
                activity?.onBackPressedDispatcher?.onBackPressed()
            }

        }
    }

    private fun getData() {
        mainViewModel.getPost().observe(viewLifecycleOwner, Observer { data ->

            binding.apply {

                tvTitle.text = data.title?.capitalizeFirstLetter()
                tvText.text = data.text
                data.time?.let {
                    Utils.formatDateToString(it, "dd/MM/yyyy").let { finalTime ->
                        tvTime.text = finalTime.toString()

                    }
                }

                tvAuthor.text = data.by?.capitalizeFirstLetter()
            }
        })
    }



    companion object {

        @JvmStatic
        fun newInstance() =
            PostDetailsFragment().apply {

            }
    }
}