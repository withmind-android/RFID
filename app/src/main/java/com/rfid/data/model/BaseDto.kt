package com.rfid.data.model

import com.google.gson.annotations.SerializedName

data class BaseDto(
    @SerializedName("response_code")
    val responseCode: Int,
    @SerializedName("response_data")
    val responseData: ResponseData?
)

data class ResponseData(
    @SerializedName("error_code")
    val errorCode: Int,
    @SerializedName("error_message")
    val errorMessage: String,
)