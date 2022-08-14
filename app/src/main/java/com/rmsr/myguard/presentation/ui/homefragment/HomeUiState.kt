package com.rmsr.myguard.presentation.ui.homefragment

import android.os.Parcelable
import android.view.inputmethod.EditorInfo
import com.rmsr.myguard.R
import com.rmsr.myguard.domain.entity.QueryType
import com.rmsr.myguard.presentation.util.BreachSortType
import com.rmsr.myguard.presentation.util.UiText
import com.rmsr.myguard.presentation.util.UserCommunicate
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

data class HomeUiState(
    val isLoading: Boolean,
    val resultReady: Boolean,
    val isPwned: Boolean,
    val breachesItems: List<BreachItemUiState>,
    val errorMessages: List<UserCommunicate>,
) {
    val isPwnedd
        get() = breachesItems.isNotEmpty()
}

data class BreachItemUiState(
    val id: Long,
    val title: String,
    val logoUrl: String,
    val discoveredDate: String,
    val compromisedData: String,
    val description: String,
)

@Parcelize
data class HomeUiResourcesState(
    val query: String,
    val queryType: QueryType,
    val breachSort: BreachSortType,
) : Parcelable {
    @IgnoredOnParcel
    private val queryTypeDependentRes = when (queryType) {
        QueryType.EMAIL -> mapOf(
            "catRes" to R.id.email_material_group_item,
            "hintUiText" to UiText.StringResource(R.string.home_frag_email_hint),
            "infoUiText" to UiText.StringResource(R.string.home_frag_email_category_info),
            "inputType" to (EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS),
        )
        QueryType.Phone -> mapOf(
            "catRes" to R.id.phone_material_group_item,
            "hintUiText" to UiText.StringResource(R.string.home_frag_phone_number_hint),
            "infoUiText" to UiText.StringResource(R.string.home_frag_phone_category_info),
            "inputType" to EditorInfo.TYPE_CLASS_PHONE,
        )
        else -> mapOf(
            "catRes" to R.id.domain_material_group_item,
            "hintUiText" to UiText.StringResource(R.string.home_frag_domain_hint),
            "infoUiText" to UiText.StringResource(R.string.home_frag_domain_category_info),
            "inputType" to (EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_FILTER),
        )
    }

    @IgnoredOnParcel
    val categoryRes
        get() = queryTypeDependentRes["catRes"] as Int

    @IgnoredOnParcel
    val infoRes
        get() = queryTypeDependentRes["infoUiText"] as UiText

    @IgnoredOnParcel
    val hintRes
        get() = queryTypeDependentRes["hintUiText"] as UiText

    @IgnoredOnParcel
    val sortRes = when (breachSort) {
        BreachSortType.DATE_DESCENDING -> R.id.date_sort_material_group_item
        BreachSortType.TITLE_ASCENDING -> R.id.name_sort_material_group_item
        BreachSortType.PWN_COUNT_DESCENDING -> R.id.pwned_count_sort_material_group_item
        else -> throw NotImplementedError()
    }

    @IgnoredOnParcel
    val keyboardInputType
        get() = queryTypeDependentRes["inputType"] as Int

    //show clearText icon when query is not empty. //show Info icon when query is empty.
    @IgnoredOnParcel
    val endIcon
        get() = if (query.isEmpty()) R.drawable.ic_info else com.google.android.material.R.drawable.mtrl_ic_cancel
}