package com.rmsr.myguard.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index


@Entity(
    tableName = SearchHistoryEntity.SCHEMA.TABLE_NAME,
//    indices = [Index(value = ["query_id"], unique = true)],
    primaryKeys = [SearchHistoryEntity.SCHEMA.QUERY_ID],
    indices = [Index(value = [SearchHistoryEntity.SCHEMA.QUERY_ID])],
    foreignKeys = [ForeignKey(
        entity = QueryEntity::class,
        parentColumns = [QueryEntity.SCHEMA.ID],
        childColumns = [SearchHistoryEntity.SCHEMA.QUERY_ID],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE  //fixme see how updating queries handle its sql recurrences.
    )]
)
data class SearchHistoryEntity(

    @ColumnInfo(name = SCHEMA.QUERY_ID)
    val queryId: Long,
    @ColumnInfo(name = SCHEMA.CREATED_TIME)
    val createdTime: Long,
    @ColumnInfo(name = SCHEMA.LAST_ACCESS_TIME)
    val lastAccessTime: Long,
    @ColumnInfo(name = SCHEMA.ACCESS_COUNT)
    val accessCount: Int,
) {
    object SCHEMA {
        const val TABLE_NAME = "search_history"
        private const val PREFIX = "his_"

        const val QUERY_ID = "${PREFIX}query_id"
        const val CREATED_TIME = "${PREFIX}created"
        const val LAST_ACCESS_TIME = "${PREFIX}accessed"
        const val ACCESS_COUNT = "${PREFIX}accessCount"
    }
}
