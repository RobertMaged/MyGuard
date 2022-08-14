package com.rmsr.myguard.presentation.ui.schedules.mainfragment.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Deprecated("")
@Composable
fun IntervalItem(
    modifier: Modifier = Modifier,
    interval: String,
    isActivated: Boolean,
    isRecommendedInterval: Boolean = false,
    onSelect: (String) -> Unit
) {
//    val backGround by animateColorAsState(targetValue = if (isActivat)
    val borderAnim by animateDpAsState(targetValue = if (isActivated) 2.dp else 0.dp)
    val colorAnim by animateColorAsState(targetValue = if (isActivated) MaterialTheme.colors.primary else MaterialTheme.typography.body2.color)

    Card(
        modifier = modifier
            .fillMaxHeight()
            .wrapContentWidth()
            .padding(4.dp)
            .border(
                width = borderAnim,
                shape = RoundedCornerShape(corner = CornerSize(20.dp)),
                color = MaterialTheme.colors.primary
            )
            .background(Color.Gray)
            .clickable { onSelect(interval) },
        shape = RoundedCornerShape(corner = CornerSize(20.dp)),
        elevation = 4.dp
    ) {

        Box(contentAlignment = Alignment.Center) {

            val text = if (isRecommendedInterval) "Default($interval)" else interval

            Text(text = text, color = colorAnim)

        }

    }
}

@Composable
fun IntervalSection(modifier: Modifier = Modifier, intervals: List<String>, selected: String) {
    Row(
        modifier = modifier.padding(horizontal = 3.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(text = "Interval")
        Text(text = selected)
    }
}


@Preview()
@Composable
private fun Preview() {
    MyGuard {

    }
}

