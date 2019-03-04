package com.ktfan.retrodsl.net

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

abstract class BaseApi<S>(s: Class<S>) {
    open val BASE_URL: String = "https://server.com/"
    open fun getOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
            .apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

        val trustAllCerts = arrayOf(
            object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )
        val sslContext = SSLContext.getInstance("TLS")
            .apply { init(null, trustAllCerts, java.security.SecureRandom()) }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .sslSocketFactory(
                sslContext.socketFactory,
                trustAllCerts[0]
            )
            .hostnameVerifier { _, _ -> true }
            .build()
    }

    val service by lazy { getRetrofit(BASE_URL, getOkHttpClient()).create(s) }

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
}
