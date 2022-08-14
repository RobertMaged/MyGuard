package com.rmsr.myguard.data.remote.pojo_response

import com.google.gson.annotations.SerializedName


data class BreachResponse @JvmOverloads constructor(
    /**
     * The Breach Identifier, its unique and used as PrimaryKey by Api provider.
     */
    @SerializedName("Name") val name: String = "NoName",

    @SerializedName("LogoPath") val logoPath: String = "NO Logo",

    @SerializedName("Title") val title: String = "NO Title",

    @SerializedName("Description") val description: String = "NO Description",

    @SerializedName("Domain") val domain: String = "NO Domain",

    /**
     * e.g "2020-03-22"
     */
    @SerializedName("BreachDate") val breachDateDMY: String = "NO Date",

    @SerializedName("PwnCount") val pwnCount: Int = 0,

    @SerializedName("DataClasses") val dataClassesList: List<String> = emptyList(),

    @SerializedName("IsSpamList") val isSpamList: Boolean = false,

    @SerializedName("IsSensitive") val isSensitive: Boolean = false,

    @SerializedName("IsRetired") val isRetired: Boolean = false,

    @SerializedName("ModifiedDate") val modifiedDate: String = "NO Date",

    @SerializedName("IsFabricated") val isFabricated: Boolean = false,

    @SerializedName("AddedDate") val addedDate: String = "NO Date",

    @SerializedName("IsVerified") val isVerified: Boolean = false,

    )


