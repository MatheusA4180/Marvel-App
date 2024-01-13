package com.example.core.data.network.response

import com.google.gson.annotations.SerializedName

data class DataContainerResponse(
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("results")
    val results: CharacterResponse
)
