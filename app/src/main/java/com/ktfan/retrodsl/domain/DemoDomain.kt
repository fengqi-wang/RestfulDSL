package com.ktfan.retrodsl.domain

import com.ktfan.retrodsl.model.AsyncResult
import com.ktfan.retrodsl.model.DemoItem
import com.ktfan.retrodsl.model.callAsync

suspend fun demoAction(): AsyncResult<List<DemoItem>> {
    return callAsync { DemoApi.service.fetchItemsAsync() }
}