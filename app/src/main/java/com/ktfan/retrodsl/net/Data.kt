package com.ktfan.retrodsl.net

data class Item(val name: String)

data class Thing(
    val format: String? = null,
    val base64RawData: String = ""
)

data class UploadResponse(
    val id: Long,
    val format: String?,
    val url: String
)

data class ApiErrorResponse(
    val message: String? = null,
    val status: String? = null,
    val type: String? = null,
    val code: Int? = null,
    val errors: Any? = null
)
