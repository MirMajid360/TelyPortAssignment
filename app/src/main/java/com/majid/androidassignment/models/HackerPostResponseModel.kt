package com.majid.androidassignment.models

import com.google.gson.annotations.SerializedName

class HackerPostResponseModel {

    data class Post(

        @SerializedName("by") var by: String? = null,
        @SerializedName("descendants") var descendants: Int? = null,
        @SerializedName("id") var id: Int? = null,
        @SerializedName("kids") var kids: ArrayList<Int> = arrayListOf(),
        @SerializedName("score") var score: Int? = null,
        @SerializedName("time") var time: Long? = null,
        @SerializedName("title") var title: String? = null,
        @SerializedName("type") var type: String? = null,
        @SerializedName("url") var url: String? = null,
        @SerializedName("parent" ) var parent : Int?           = null,
        @SerializedName("text"   ) var text   : String?        = null, )


}