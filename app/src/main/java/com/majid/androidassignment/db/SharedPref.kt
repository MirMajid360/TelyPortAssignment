package com.majid.androidassignment.db

import android.content.SharedPreferences

import com.google.gson.Gson
import javax.inject.Inject


class SharedPref @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {


    fun setData(key: String, data: String) {
        try {


            sharedPreferences.edit().putString(key, data).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setData(key: String, data: Boolean) {
        try {


            sharedPreferences.edit().putBoolean(key, data).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setData(key: String, data: Int) {
        try {


            sharedPreferences.edit().putInt(key, data).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setData(key: String, data: Float) {
        try {


            sharedPreferences.edit().putFloat(key, data).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun <T> saveData(key: String, value: T) {
        try {


            val editor = sharedPreferences.edit()
            val json = Gson().toJson(value)
            editor.putString(key, json)
            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun <T> getObject(key: String, objectType: Class<T>?): T? {

        val json = sharedPreferences.getString(key, null)
        val gson = Gson()
        return try {
            gson.fromJson(json, objectType)

        } catch (e: java.lang.Exception) {
            throw IllegalArgumentException(
                "Object stored with key " + key + " is instanceof other class"
            )
        }

    }


    fun getString(key: String): String {

        return sharedPreferences.getString(key, "") ?: ""
    }


    fun getBoolean(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }


    fun getInteger(key: String): Int {
        return sharedPreferences.getInt(key, 0)
    }

    fun getFloat(key: String): Float {
        return sharedPreferences.getFloat(key, 0f)
    }

    fun clearData(key: String) {
        try {


            val editor = sharedPreferences.edit()
            editor.remove(key)
            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }




}