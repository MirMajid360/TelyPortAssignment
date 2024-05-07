package com.majid.androidassignment.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import com.majid.androidassignment.R
import com.majid.androidassignment.app.App
import com.majid.androidassignment.network.ApiService
import com.majid.androidassignment.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.CacheControl
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.Base64
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {
    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.Base_Url)
            .client(provideOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .sslSocketFactory(
                createUnsafeSslContext().socketFactory,
                createUnsafeX509TrustManager()
            )
            .hostnameVerifier { _, _ -> true }
            .addInterceptor(provideHttpLoggingInterceptor())
            .addNetworkInterceptor(provideCacheInterceptor())
            .connectTimeout(Constants.apiTimeoutDuration, TimeUnit.SECONDS)
            .readTimeout(Constants.apiTimeoutDuration, TimeUnit.SECONDS)
            .writeTimeout(Constants.apiTimeoutDuration, TimeUnit.SECONDS)
            .connectionPool(ConnectionPool(1000, 60, TimeUnit.SECONDS))
            .retryOnConnectionFailure(true)
            .dispatcher(getDispatcher())
            .build()
    }

    private fun createUnsafeSslContext(): SSLContext {
        val trustManager = createUnsafeX509TrustManager()
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
        return sslContext
    }

    private fun createUnsafeX509TrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}

            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    }

    private fun getDispatcher(): Dispatcher {
        return Dispatcher(Executors.newFixedThreadPool(1))
    }

    private fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    private fun provideCacheInterceptor(): Interceptor {
        return object : Interceptor {
            @RequiresApi(Build.VERSION_CODES.O)
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {

                val request: Request = chain.request().newBuilder()
                    .addHeader("User-Agent", App.context.getString(R.string.app_name))
                    .addHeader("Accept", "application/json")
                    .build()

                val response: Response = chain.proceed(request)

                val cacheControl: CacheControl = CacheControl.Builder()
                    .maxAge(1, TimeUnit.MINUTES)
                    .build()

                return response.newBuilder()
                    .header(Constants.CACHE_CONTROL, cacheControl.toString()).build()
            }
        }
    }

    private fun checkIfHasNetwork(mContext: Context): Boolean {
        val connectivityManager =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }



    @Singleton
    @Provides
    fun providesAPIService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }





}