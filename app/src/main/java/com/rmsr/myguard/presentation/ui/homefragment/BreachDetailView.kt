package com.rmsr.myguard.presentation.ui.homefragment

import android.text.format.DateFormat
import androidx.annotation.Keep
import com.rmsr.myguard.domain.entity.Breach
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

@Keep
@Deprecated("Use BreachItemUiState instead.", ReplaceWith("BreachItemUiState()"))
data class BreachDetailView(
    val id: Long = 0,
    /**
     * The Breach Identifier, its unique and used as PrimaryKey.
     */
    var name: String,

    var createdTime: Long,

    var logoPath: String,

    var title: String,

    var description: String,

    var domain: String,

    /**
     * e.g "2020-03-22"
     */
    var date: String,

    var pwnCount: Int,

    //what has pwned. ex. "USERNAME" "PASSWORD"
    var dataClassesList: List<String>,

    //Used to expand recyclerView.
    var isInfoExpanded: Boolean = false
) {
    /**
     * Used only when Header is needed to view in RecyclerView,
     * so the Breaches List should insert this constructor at first index.
     */
    constructor(name: String = "NoName") : this(
        name = name,
        logoPath = "NO Logo",
        createdTime = System.currentTimeMillis(),
        title = "NO Title",
        description = "NO Description",
        domain = "NO Domain",
        date = "NO Date",
        dataClassesList = emptyList<String>(),

        //This Random number generator used in case recycler adapter use DiffUtil,
        //so Header Will changed every time.
        pwnCount = Random.nextInt()
    )

    /**
     * return the discoveredDate all in one string,
     * that used only for the textView.
     *
     * @return e.g "oct. 2020"
     */
    val breachDate: String
        get() = date.takeIf { it.contains("-") }?.apply {
            try {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(this)
                return DateFormat.format("MMM. yyyy", date).toString()
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        } ?: date


    /**
     * return the leaked data all in one string, **
     * that used only for the textView.
     * @return
     */
    val dataClasses: String
        get() {
            val data = StringBuilder()
            for (item in dataClassesList) {
                data.append(item).append(", ")
            }
            if (data.length > 1)
                data.delete(data.length - 2, data.length - 1)
            //data.append(".")
            return data.toString()
        }
}

fun Breach.toOldBreach(): BreachDetailView = BreachDetailView(
    id = this.id,
    name = this.title,
    createdTime = this.metadata.createdTime,
    logoPath = this.metadata.logoUrl,
    title = this.title,
    description = this.leakInfo.description,
    domain = this.metadata.domain,
    date = this.leakInfo.discoveredDate.toString(),
    pwnCount = this.leakInfo.pwnCount,
    dataClassesList = this.leakInfo.compromisedData,
)