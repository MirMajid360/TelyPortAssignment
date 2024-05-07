package com.majid.androidassignment.vewmdels

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.majid.androidassignment.R
import com.majid.androidassignment.app.App
import com.majid.androidassignment.models.HackerPostResponseModel
import com.majid.androidassignment.repository.Repository
import com.majid.androidassignment.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
) : ViewModel() {

    private val _postData = MutableLiveData<HackerPostResponseModel.Post>()
    val postData: LiveData<HackerPostResponseModel.Post> get() = _postData

    val isCommentFragmentShown = MutableLiveData<Boolean>()

    val locationList : MutableLiveData<ArrayList<String>> by lazy { MutableLiveData<ArrayList<String>>() }


    /**
     *  Managing  Fragments Ang Other UI Operations here Using Live Data
     * **/
    private var openCommentsFragment: MutableLiveData<Event<Array<Any>>> =
        MutableLiveData<Event<Array<Any>>>()
    private var openLocationsFragment: MutableLiveData<Event<Array<Any>>> =
        MutableLiveData<Event<Array<Any>>>()
    private var openNotificationsFragment: MutableLiveData<Event<Array<Any>>> =
        MutableLiveData<Event<Array<Any>>>()


    fun getOpenCommentsFragment(): MutableLiveData<Event<Array<Any>>> {
        return openCommentsFragment
    }

    fun setOpenCommentsFragment(objects: Array<Any>) {
        openCommentsFragment.value = Event(objects)
    }

    fun getOpenLocationsFragment(): MutableLiveData<Event<Array<Any>>> {
        return openLocationsFragment
    }

    fun setOpenLocationsFragment(objects: Array<Any>) {
        openLocationsFragment.value = Event(objects)
    }

    fun getOpenNotificationsFragment(): MutableLiveData<Event<Array<Any>>> {
        return openNotificationsFragment
    }

    fun setOpenNotificationsFragment(objects: Array<Any>) {
        openNotificationsFragment.value = Event(objects)
    }


    /**
     * Snackbar Setup
     */

    private var showSnackBar: MutableLiveData<Event<Array<Any>>> =
        MutableLiveData<Event<Array<Any>>>()

    fun getSnackbar(): MutableLiveData<Event<Array<Any>>> {
        return showSnackBar
    }

    fun setSnackbar(objects: Array<Any>) {
        showSnackBar.postValue(Event(objects))
    }














    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }


    fun getPost(): LiveData<HackerPostResponseModel.Post> {


        if (!isNetworkConnected()) {
            setSnackbar(arrayOf(App.context.getString(R.string.no_internet_message), true))

        } else {

            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

                try {

                    val response = repository.getPost()


                    if (response.isSuccessful) {

                        response.body()?.let {
                            _postData.postValue(it)
                        }
                    } else {

                        val errorBody = response.errorBody()?.string()


                        val message = JSONObject(errorBody!!).getString("message")


                    }
                } catch (e: java.lang.Exception) {

                }

            }

        }

        return postData

    }

    fun getComment(id :Int): LiveData<HackerPostResponseModel.Post> {

        val commentData = MutableLiveData<HackerPostResponseModel.Post>()

        if (!isNetworkConnected()) {
            setSnackbar(arrayOf(App.context.getString(R.string.no_internet_message), true))

        } else {

            viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {

                try {

                    val response = repository.getComments(id)


                    if (response.isSuccessful) {

                        response.body()?.let {
                            commentData.postValue(it)
                        }
                    } else {

                        val errorBody = response.errorBody()?.string()


                        val message = JSONObject(errorBody!!).getString("message")


                    }
                } catch (e: java.lang.Exception) {

                }

            }

        }

        return commentData

    }


    /**
    Check Network connectivity
     */
    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            App.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
            NetworkCapabilities.TRANSPORT_CELLULAR
        ) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || capabilities.hasTransport(
            NetworkCapabilities.TRANSPORT_BLUETOOTH
        ))
    }
}