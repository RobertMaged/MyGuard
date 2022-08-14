package com.rmsr.myguard.data.remote.pojo_response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Suppress("Not implemented yet.")
@Keep
data class PasteResponse @JvmOverloads constructor(
    @SerializedName("Title")
    val title: String = "missing title",

    @SerializedName("Id")
    val id: String = "missing id",

    @SerializedName("EmailCount")
    val emailCount: Int = 0,

    @SerializedName("Source")
    val source: String = "missing source",

    @SerializedName("Date")
    val date: String = "missing discoveredDate",
)