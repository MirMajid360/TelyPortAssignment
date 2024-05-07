package com.majid.androidassignment.view.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.majid.androidassignment.databinding.ItemCommentBinding
import com.majid.androidassignment.databinding.ItemLocationBinding
import com.majid.androidassignment.models.HackerPostResponseModel
import com.majid.androidassignment.utils.IListeners


class LocationsAdapter(
    var context: Context,
    var list: ArrayList<String>,
    var listener: IListeners.IClickListeners,
) : RecyclerView.Adapter<LocationsAdapter.RecipeStepsViewHolder>() {


    inner class RecipeStepsViewHolder(private val binding: ItemLocationBinding) :
        ViewHolder(binding.root) {
        fun bind(model: String, position: Int) {

            try {
                binding.apply {
                    tvTitle.text = "${position+ 1}.   Location"
                     tvLocation.text = model
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeStepsViewHolder {
        val itemBinding =
            ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return RecipeStepsViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return list.size
    }



    override fun onBindViewHolder(holder: RecipeStepsViewHolder, position: Int) {

        try {


            holder.bind(list[position], position)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newList: ArrayList<String>){
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()



    }
}