package com.rmsr.myguard.data.database.entity

import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rmsr.myguard.domain.entity.QueryType
import java.util.*

@Entity(
    tableName = QueryEntity.SCHEMA.TABLE_NAME,
    indices = [Index(value = [QueryEntity.SCHEMA.UUID], unique = true)]
)
data class QueryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = SCHEMA.ID)
    val id: Long = 0,
    @ColumnInfo(name = SCHEMA.CONTENT, collate = ColumnInfo.NOCASE)
    val content: String,
    @ColumnInfo(name = SCHEMA.TYPE)
    val type: QueryType,
    @ColumnInfo(name = SCHEMA.UUID)
    val uuid: UUID = UUID.nameUUIDFromBytes(
        "$content$type".toLowerCase(Locale.current)
            .toByteArray()
    ),
    @ColumnInfo(name = SCHEMA.HINT, defaultValue = "NULL")
    val hint: String? = null,
) {

    object SCHEMA {
        const val TABLE_NAME = "queries"
        private const val PREFIX = "que_"

        const val ID = "_queryId"
        const val CONTENT = "${PREFIX}content"
        const val TYPE = "${PREFIX}type"
        const val UUID = "${PREFIX}uuid"
        const val HINT = "${PREFIX}hint"
    }
}
