package com.majid.androidassignment.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.majid.androidassignment.R
import com.majid.androidassignment.databinding.FragmentNotficationsBinding
import com.majid.androidassignment.db.DBDEFINITIONS
import com.majid.androidassignment.db.SharedPref
import com.majid.androidassignment.vewmdels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsFragment : Fragment() {
    private lateinit var binding: FragmentNotficationsBinding

    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var sharedPref : SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentNotficationsBinding.inflate(layoutInflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref.getInteger(DBDEFINITIONS.Notification_Count).let {

            binding.tvCount.text = it.toString()
        }

    }

    companion object {

        @JvmStatic
        fun newInstance() =
            NotificationsFragment().apply {

            }
    }
}