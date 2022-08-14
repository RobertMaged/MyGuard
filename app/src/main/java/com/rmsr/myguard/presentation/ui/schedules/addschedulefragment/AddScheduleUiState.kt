package com.rmsr.myguard.presentation.ui.schedules.addschedulefragment

import android.view.inputmethod.EditorInfo
import com.rmsr.myguard.R
import com.rmsr.myguard.domain.entity.SearchQuery
import com.rmsr.myguard.presentation.util.UiText
import com.rmsr.myguard.presentation.util.UserCommunicate

data class AddScheduleUiState(
    val searchQuery: SearchQuery,
    val isValidQuery: Boolean,
    private val canPickerHandled: Boolean,
    val contactPickerRequired: Boolean,
    val scheduleSaved: Boolean,
    val errorMessages: List<UserCommunicate>,
) {
    private val searchQueryDependentRes = when (searchQuery) {
        is SearchQuery.Email -> Companion.queryEmailTypeMap
        is SearchQuery.Phone -> Companion.queryPhoneTypeMap
        else -> throw NotImplementedError("AddSchedules didn't implement other categories in ui")
    }

    val categoryRes
        get() = searchQueryDependentRes["catRes"] as Int

    val hintRes
        get() = searchQueryDependentRes["hintUiText"] as UiText

    val keyboardInputType
        get() = searchQueryDependentRes["inputType"] as Int

    val imeOptions
        get() = if (isValidQuery) EditorInfo.IME_ACTION_GO else EditorInfo.IME_ACTION_NONE

    /**
     * show clearText icon when query is not empty or
     * open contacts icon when query is empty and in phone mode.
     */
    val endIcon: Int?
        get() = when {
            canPickerHandled && searchQuery.query.isEmpty() && searchQuery is SearchQuery.Phone ->
                R.drawable.ic_open_contacts
            searchQuery.query.isEmpty() -> null
            else -> com.google.android.material.R.drawable.mtrl_ic_cancel
        }

    companion object {
        private val queryEmailTypeMap = mapOf(
            "catRes" to R.id.email_schedule_add_group_item,
            "hintUiText" to UiText.StringResource(R.string.home_frag_email_hint),
            "inputType" to (EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS),
        )

        private val queryPhoneTypeMap = mapOf(
            "catRes" to R.id.phone_schedule_add_group_item,
            "hintUiText" to UiText.StringResource(R.string.home_frag_phone_number_hint),
            "inputType" to EditorInfo.TYPE_CLASS_PHONE,
        )
    }

}