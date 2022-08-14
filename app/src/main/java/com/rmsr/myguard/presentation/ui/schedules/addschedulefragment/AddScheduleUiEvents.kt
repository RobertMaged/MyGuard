package com.rmsr.myguard.presentation.ui.schedules.addschedulefragment

import android.content.ContentResolver
import android.net.Uri
import com.rmsr.myguard.domain.entity.QueryType

sealed class AddScheduleUiEvents {

    data class SetQuery(val query: String) : AddScheduleUiEvents()

    data class SetHint(val hint: String) : AddScheduleUiEvents()

    data class SetCategory(val type: QueryType) : AddScheduleUiEvents()

    data class PickContact(val resolver: ContentResolver, val uri: Uri?) : AddScheduleUiEvents()

    object EndIconClick : AddScheduleUiEvents()

    object SaveSchedule : AddScheduleUiEvents()
}