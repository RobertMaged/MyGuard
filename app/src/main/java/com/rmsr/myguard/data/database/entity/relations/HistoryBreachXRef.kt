package com.rmsr.myguard.data.database.entity.relations

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.rmsr.myguard.data.database.entity.BreachEntity
import com.rmsr.myguard.data.database.entity.SearchHistoryEntity

@Entity(
    tableName = HistoryBreachXRef.SCHEMA.TABLE_NAME,
    primaryKeys = [HistoryBreachXRef.SCHEMA.HISTORY_ID, HistoryBreachXRef.SCHEMA.BREACH_ID],
    foreignKeys = [
        ForeignKey(
            entity = BreachEntity::class,
            parentColumns = [BreachEntity.SCHEMA.ID],
            childColumns = [HistoryBreachXRef.SCHEMA.BREACH_ID],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SearchHistoryEntity::class,
            parentColumns = [SearchHistoryEntity.SCHEMA.QUERY_ID],
            childColumns = [HistoryBreachXRef.SCHEMA.HISTORY_ID],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HistoryBreachXRef(
    @ColumnInfo(name = SCHEMA.HISTORY_ID)
    val historyId: Long,
    @ColumnInfo(name = SCHEMA.BREACH_ID)
    val breachId: Long
) {
    object SCHEMA {
        const val TABLE_NAME = "history_breach_xref"
        private const val PREFIX = "his_x_bre__"

        const val HISTORY_ID = "${PREFIX}query_id"
        const val BREACH_ID = "${PREFIX}breach_id"
    }
}
