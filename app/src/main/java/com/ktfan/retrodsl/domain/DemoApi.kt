package com.ktfan.retrodsl.domain

import com.ktfan.retrodsl.model.DemoItem
import com.ktfan.retrodsl.net.BaseApi
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path

interface DemoService {
    companion object {
        private const val DEMO_URL = "/{appId}/profile"
    }

    @POST(DEMO_URL)
    fun fetchItemsAsync(
        @Path("appId") appId: String = ""
    ): Deferred<Response<List<DemoItem>>>
}

object DemoApi : BaseApi<DemoService>(DemoService::class.java)
