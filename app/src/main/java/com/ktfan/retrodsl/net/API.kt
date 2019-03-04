package com.ktfan.retrodsl.net

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

object API {
    private const val BASE_URL = "http://api.test.com"
    const val ACCESS_TOKEN = "X-Auth-Token"
    const val SET_COOKIE = "Set-Cookie"

    interface RestService {
        @GET("/items")
        fun fetchItems(@Header(ACCESS_TOKEN) accessToken: String): Deferred<Response<Item>>

        @HEAD("/items/")
        fun isItemExist(
            @Header(ACCESS_TOKEN) accessToken: String, @Query(
                value = "number",
                encoded = true
            ) number: String
        ): Deferred<Response<Void>>

        @DELETE("/items/{itemId}")
        fun deleteCard(@Header(ACCESS_TOKEN) accessToken: String, @Path("itemId") itemId: Long): Deferred<Response<Void>>

        @POST("/images")
        fun uploadThing(@Header(ACCESS_TOKEN) accessToken: String, @Body thing: Thing): Deferred<Response<UploadResponse>>
    }

    val service by lazy { getRetrofit(BASE_URL, okHttpClient).create(RestService::class.java) }

    private fun getRetrofit(baseUrl: String, okHttpClient: OkHttpClient): Retrofit {
        val gson = GsonBuilder()
            //.setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
            .create()

        val builder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpClient)

        return builder.build()
    }

    private val okHttpClient: OkHttpClient
        get() {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            return OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()
        }
}

class RestyHolder<T>(private val lifecycleOwner: LifecycleOwner) {
    private val resultData: MutableLiveData<T> = MutableLiveData()
    private val errorData: MutableLiveData<ApiErrorResponse> = MutableLiveData()
    private val exceptionData: MutableLiveData<Exception> = MutableLiveData()

    lateinit var request: API.RestService.() -> Deferred<Response<T>>
    var onSuccess: (T?) -> Unit = {}
    var onError: (ApiErrorResponse) -> Unit = {}
    var onException: (Exception) -> Unit = {}

    fun act(cxt: CoroutineContext) {
        resultData.observe(lifecycleOwner, Observer(onSuccess))
        errorData.observe(lifecycleOwner, Observer(onError))
        exceptionData.observe(lifecycleOwner, Observer(onException))

        CoroutineScope(cxt).launch {
            try {
                val response = API.service.request().await()

                if (response.isSuccessful)
                    resultData.postValue(response.body())
                else {
                    Gson().fromJson(response.errorBody()?.string(), ApiErrorResponse::class.java)
                        ?.let {
                            errorData.postValue(it)
                        }
                        ?: run {
                            Log.e("TEST", "Error!!")
                        }
                }
            } catch (e: Exception) {
                Log.e("TEST", e.message)
                exceptionData.postValue(e)
            }
        }
    }
}

fun <T> restyCall(
    lifecycleOwner: LifecycleOwner,
    cxt: CoroutineContext = Dispatchers.IO,
    block: RestyHolder<T>.() -> Unit
) {
    RestyHolder<T>(lifecycleOwner).apply(block).act(cxt)
}