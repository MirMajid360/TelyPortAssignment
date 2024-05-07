package com.majid.androidassignment.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.majid.androidassignment.databinding.ItemCommentBinding
import com.majid.androidassignment.models.HackerPostResponseModel
import com.majid.androidassignment.utils.IListeners
import com.majid.androidassignment.utils.Utils
import com.majid.androidassignment.utils.capitalizeFirstLetter


class CommentsAdapter(
    var context: Context,
    var list: ArrayList<HackerPostResponseModel.Post>,
    var listener: IListeners.IClickListeners,
) : RecyclerView.Adapter<CommentsAdapter.RecipeStepsViewHolder>() {


    inner class RecipeStepsViewHolder(private val binding: ItemCommentBinding) :
        ViewHolder(binding.root) {
        fun bind(model: HackerPostResponseModel.Post, position: Int) {

            try {

                binding.apply {
                    tvText.text =model.text
                    tvAuthor.text = "by ${model.by?.capitalizeFirstLetter()}"
                    tvTime.text = model.time?.let { Utils.formatDateToString(it,"mm:ss/dd/MM/yyyy") }
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeStepsViewHolder {
        val itemBinding =
            ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)

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

    fun addItem(newItem: HackerPostResponseModel.Post){
        list.add(newItem)
        notifyItemInserted(list.size)

    }
}