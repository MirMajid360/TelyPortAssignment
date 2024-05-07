package com.majid.androidassignment.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.majid.androidassignment.R
import com.majid.androidassignment.databinding.FragmentLocationDetailsBinding
import com.majid.androidassignment.databinding.FragmentPostDetailsBinding
import com.majid.androidassignment.db.DBDEFINITIONS
import com.majid.androidassignment.db.SharedPref
import com.majid.androidassignment.utils.IListeners
import com.majid.androidassignment.vewmdels.MainViewModel
import com.majid.androidassignment.view.adapters.CommentsAdapter
import com.majid.androidassignment.view.adapters.LocationsAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LocationDetailsFragment : Fragment() {
    private  lateinit var binding: FragmentLocationDetailsBinding
    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var sharedPref :SharedPref
    private  lateinit var locationsAdapter: LocationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLocationDetailsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCommentsAdapter(arrayListOf())


        mainViewModel.locationList.observe(viewLifecycleOwner, Observer {
            if (::locationsAdapter.isInitialized){
                locationsAdapter.updateList(it)
            }else{
                initCommentsAdapter(it)
            }

        })

    }

    private fun initCommentsAdapter(locationList : ArrayList<String>) {

        try {


            locationsAdapter =
                LocationsAdapter(
                    requireContext(),
                    locationList,
                    object : IListeners.IClickListeners {
                        override fun onItemClicked(model: Any, pos: Int) {

                        }

                    })
            binding.recyclerView.layoutManager = LinearLayoutManager(activity)
            binding.recyclerView.adapter = locationsAdapter

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    companion object {

        @JvmStatic
        fun newInstance() =
            LocationDetailsFragment().apply {

            }
    }
}