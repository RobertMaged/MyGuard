package com.rmsr.myguard.presentation.ui.schedules.detailfragment.components

import android.text.method.LinkMovementMethod
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.rmsr.myguard.R
import com.rmsr.myguard.databinding.BreachListItemBinding
import com.rmsr.myguard.di.GlideApp
import com.rmsr.myguard.domain.entity.Breach
import com.rmsr.myguard.presentation.ui.schedules.detailfragment.ScheduleDetailViewModel
import com.rmsr.myguard.presentation.util.getHTMLFormatText
import java.time.format.DateTimeFormatter


@Composable
fun ScheduleDetailScreen(
    breaches: List<Breach> = emptyList(),
    detailViewModel: ScheduleDetailViewModel
) {
    if (detailViewModel.leaks.isEmpty())
        NoLeaks()
    else
        BreachLazyList(breaches = detailViewModel.leaks)
}

@Composable
fun NoLeaks(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Image(
                    painter = painterResource(id = R.drawable.no_leaks),
                    modifier = Modifier.fillMaxWidth(0.3f),
                    contentDescription = null,
                    alpha = 0.5f
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No Leaks.",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun BreachLazyList(breaches: List<Breach>) {

    val breachesExpandState = remember { mutableStateMapOf<Breach, Boolean>() }
    var prevBreach by remember { mutableStateOf<Breach?>(null) }

    breaches.associateWithTo(breachesExpandState) { false }

    LazyColumn(modifier = Modifier.fillMaxSize()) {

        items(breaches, key = Breach::id) { breach ->
            BreachItem(
                currBreach = breach,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                isExpanded = breachesExpandState[breach] ?: false,
                onClick = { clickedBreach ->
                    breachesExpandState[clickedBreach] =
                        breachesExpandState.getValue(clickedBreach).not()

                    prevBreach?.takeIf { prevBreach != clickedBreach }?.let {
                        breachesExpandState[it] = false
                    }
                    prevBreach = clickedBreach
                }
            )
        }
    }
}

@Composable
fun BreachItem(
    currBreach: Breach,
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    onClick: (Breach) -> Unit = {}
) {
    val context = LocalContext.current

    AndroidViewBinding(
        factory = BreachListItemBinding::inflate,
        modifier = modifier.clickable { onClick(currBreach) }) {

        GlideApp.with(listItemBreachLogo)
            .load(currBreach.metadata.logoUrl)
            .into(listItemBreachLogo)

        listItemBreachName.text =
            String.format(context.getString(R.string.domain_name), currBreach.title)
        listItemBreachData.text =
            String.format(context.getString(R.string.breach_date), currBreach.breachDate)
        listItemCompromisedData.text =
            String.format(
                context.getString(R.string.compromised_data),
                currBreach.leakInfo.compromisedData.toString().trim('[', ']')
            )

        listItemBreachDescription.text =
            getHTMLFormatText(
                currBreach.leakInfo.description
//                String.format(
//                    context.getString(R.string.description),
//                    currBreach.leakInfo.description
//                )
            )
        listItemBreachDescription.movementMethod = LinkMovementMethod.getInstance()

        if (isExpanded) {
            expandedInfo.visibility = View.VISIBLE
            expandIcon.animate().setDuration(200).rotation(180f)
        } else {
            expandIcon.animate().setDuration(200).rotation(0f)
            expandedInfo.visibility = View.GONE
        }
    }
}

private val Breach.breachDate: String
    get() = leakInfo.discoveredDate.format(DateTimeFormatter.ofPattern("MMM. yyyy"))