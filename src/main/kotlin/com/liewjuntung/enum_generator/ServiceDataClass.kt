package com.liewjuntung.enum_generator

import com.google.gson.annotations.SerializedName

data class ServiceDataClass(
    @SerializedName("name") val name: String,
    @SerializedName("methods") val methodList: ArrayList<String>
)
