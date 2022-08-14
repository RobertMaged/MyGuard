package com.rmsr.myguard.data.database.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Keep
@Entity(
    tableName = BreachEntity.SCHEMA.TABLE_NAME,
    indices = [Index(value = [BreachEntity.SCHEMA.LEAK_NAME], unique = true)]
)
data class BreachEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = SCHEMA.ID)
    val id: Long = 0,
    /**
     * The Breach Identifier, its unique and retrieved from the API.
     */
    @ColumnInfo(name = SCHEMA.LEAK_NAME, collate = ColumnInfo.NOCASE)
    val leakName: String,

    @ColumnInfo(name = SCHEMA.CREATED_TIME)
    val createdTime: Long,

    @ColumnInfo(name = SCHEMA.LOGO_PATH)
    val logoPath: String,

    @ColumnInfo(name = SCHEMA.TITLE, collate = ColumnInfo.NOCASE)
    val title: String,

    @ColumnInfo(name = SCHEMA.DESCRIPTION)
    val description: String,

    @ColumnInfo(name = SCHEMA.DOMAIN, collate = ColumnInfo.NOCASE)
    val domain: String,

    /**
     * Date format is YYYY-MM-DD.
     */
    @ColumnInfo(name = SCHEMA.DATE)
    val date: String,

    @ColumnInfo(name = SCHEMA.PWN_COUNT)
    val pwnCount: Int,

    //what has pwned. ex. "USERNAME" "PASSWORD"
    @ColumnInfo(name = SCHEMA.COMPROMISED_DATA)
    val compromisedData: List<String>,

    ) {

//    @Deprecated("Use BreachEntityMapper.map() instead", replaceWith = ReplaceWith("BreachEntityMapper()", "com.rmsr.myguard.data.mapper"))
//    fun toBreach(): Breach {
//        return Breach(
//            id = this.id ?: "NoName",
//            cachedTimeStamp = this.cachedTimeStamp ?: 0,
//            logoPath = this.logoPath ?: "NoLogo",
//            title = this.title ?: "NoTile",
//            description = this.description ?: "NoDescription",
//            domain = this.domain ?: "NoDomain",
//            breachDateDMY = this.breachDateDMY ?: "NoDate",
//            pwnCount = this.pwnCount ?: 0,
//            dataClassesList = this.dataClassesList ?: emptyList<String>()
//        )
//    }

    object SCHEMA {
        const val TABLE_NAME = "breaches"
        private const val PREFIX = "bre_"

        const val ID = "_breachId"
        const val TITLE = "${PREFIX}title"
        const val LEAK_NAME = "${PREFIX}name"
        const val DOMAIN = "${PREFIX}domain"
        const val CREATED_TIME = "${PREFIX}created"
        const val LOGO_PATH = "${PREFIX}logoPath"
        const val DESCRIPTION = "${PREFIX}description"
        const val DATE = "${PREFIX}discoveredDate"
        const val PWN_COUNT = "${PREFIX}pwnCount"
        const val COMPROMISED_DATA = "${PREFIX}compromisedData"
    }
}
