package com.rmsr.myguard.presentation.ui.schedules.mainfragment.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rmsr.myguard.R
import com.rmsr.myguard.domain.entity.Schedule
import com.rmsr.myguard.domain.entity.SearchQuery
import com.rmsr.myguard.presentation.ui.schedules.mainfragment.SchedulesItemUiState
import com.rmsr.myguard.presentation.ui.schedules.mainfragment.SchedulesViewModel


@ExperimentalMaterialApi
@Composable
fun ScheduleListItem(
    modifier: Modifier = Modifier,
    itemUiState: SchedulesItemUiState,
    expandActions: List<ExpandAction>,
    isExpanded: Boolean,
    onExpandClick: (Schedule) -> Unit = {},
    onActionClick: (ExpandAction) -> Unit = {},
) {
    val schedule = itemUiState.schedule


    val transition = updateTransition(targetState = isExpanded, label = "transition")

    val elevationDp by transition.animateDp(label = "elevationDp") { expanded ->
        if (expanded) 4.dp else Dp.Hairline
    }
    val colorAnim by transition.animateColor(label = "colorAnim") { expanded ->
        colorResource(
            id = if (expanded) R.color.color_on_background_custom
            else R.color.color_on_primary_custom
        )
    }
    val paddingDp by transition.animateDp(label = "paddingDp") { expanded ->
        if (expanded) 8.dp else Dp.Hairline
    }
    //val elevationDp by animateDpAsState(targetValue = if (expandState) 4.dp else Dp.Hairline)
//    val elevationDp = if (expandState) 4.dp else Dp.Hairline
//    val paddingDp by animateDpAsState(targetValue = if (expandState) 8.dp else Dp.Hairline)

//    val colorAnim by animateColorAsState(
//        targetValue =
//        colorResource(
//            id = if (expandState) R.color.color_on_background_custom else
//                R.color.color_on_primary_custom
//        )
//    )
//    val colorAnim = colorResource(
//            id = if (expandState) R.color.color_on_background_custom else
//                R.color.color_on_primary_custom
//        )


    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = paddingDp)
        // .clickable { expandState = expandState.not() }
        ,
        shape = RoundedCornerShape(20.dp),
        elevation = elevationDp,
        color = colorAnim,
        onClick = { onExpandClick(schedule) }//{ expandState = expandState.not() }
//        border = BorderStroke(1.dp, Color.Gray),
    ) {

        val dividerFloat by transition.animateFloat(label = "dividerFloat") { expanded ->
            if (expanded) 1f else 0.84f
        }
//        animateFloatAsState(targetValue = if (expandState) 1f else 0.84f )

        Column(
            modifier = Modifier
                .fillMaxSize()
//            .padding(top = 8.dp)
                .animateContentSize(),
            horizontalAlignment = Alignment.End
        ) {


            ScheduleItemMainPart(
//            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp, end = 8.dp),
                modifier = Modifier.padding(all = 10.dp),
                itemUiState = itemUiState
            )
//        {
//            expandState.value = !expandState.value
//        }

            Divider(modifier = Modifier.fillMaxWidth(dividerFloat))
            if (isExpanded) {
                ScheduleItemExpandedPart(
                    actions = expandActions,
                    onActionClick = onActionClick
                )
            }
        }

    }
}

val Color.Companion.random: Color
    get() = arrayOf(Green, Yellow, Blue, Green, Red).random()

@Composable
fun ScheduleItemMainPart(
    modifier: Modifier = Modifier,
    itemUiState: SchedulesItemUiState,
    // onClick: () -> Unit
) {
    val schedule = itemUiState.schedule

    Row(
        modifier = modifier
            .fillMaxWidth()
//            .wrapContentHeight()
//            .clickable { onClick() }
//            .padding(dimensionResource(id = R.dimen._6sdp))
        // .clip(RoundedCornerShape(dimensionResource(id = R.dimen._4sdp)))
        ,
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {

        Surface(
            modifier = Modifier.size(dimensionResource(id = R.dimen._40sdp)),
            shape = CircleShape,
            color = Color.Gray.copy(0.4f)
        ) {
            val (painter, dec) = when (schedule.searchQuery) {
                is SearchQuery.Email -> Icons.Rounded.AlternateEmail to "Schedule Email"
                is SearchQuery.Phone -> Icons.Rounded.Phone to "Schedule Phone"
                else -> Icons.Rounded.Domain to "Schedule Domain"
                /* is SearchQuery.Email -> painterResource(R.drawable.ic_white_email) to "Schedule Email"
                 is SearchQuery.Phone -> painterResource(R.drawable.ic_white_phone) to "Schedule Phone"
                 else -> painterResource(R.drawable.ic_domain) to "Schedule Domain"
                */
            }
            Image(imageVector = painter, contentDescription = dec, alpha = .6f)
        }


        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen._8sdp)))

        Column(
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = schedule.searchQuery.hint ?: schedule.searchQuery.query,
                fontWeight = FontWeight.Medium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen._4sdp)))

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = itemUiState.lastLeaksFormatted,
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }


        Spacer(modifier = Modifier.width(6.dp))

        val time by derivedStateOf {
            itemUiState.lastLeakDate
        }
        MessageCounterAndLastOneTime(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.CenterVertically),
            lastMessageTime = time,
            messagesCount = itemUiState.newLeaksCount //schedule.unSeenBreaches
        )

    }
}

@Composable
fun MessageCounterAndLastOneTime(
    modifier: Modifier = Modifier,
    lastMessageTime: String,
    messagesCount: Int,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = lastMessageTime,
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.size(dimensionResource(R.dimen._2sdp)))

        if (messagesCount > 0)
        //fixme make text in box cause number.
            Badge(
                modifier = modifier.align(Alignment.CenterHorizontally),
                backgroundColor = Color.Red.copy(0.7f)
            ) {
                Text(
                    modifier = Modifier
                    //.size(dimensionResource(id = R.dimen._20sdp))
                    //.padding(dimensionResource(id = R.dimen._2sdp))
                    ,
                    text = "$messagesCount",
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    fontSize = MaterialTheme.typography.body2.fontSize
                )
            }
//            Text(
//                modifier = Modifier
//                    .size(dimensionResource(id = R.dimen._20sdp))
//                    .padding(dimensionResource(id = R.dimen._2sdp))
//                    .clip(CircleShape)
//                    .align(Alignment.CenterHorizontally)
//                    .background(Color.Red.copy(0.7f)),
//                text = "$messagesCount",
//                fontWeight = FontWeight.Light,
//                textAlign = TextAlign.Center,
//                fontSize = MaterialTheme.typography.body2.fontSize
//            )
        else
            Spacer(modifier = Modifier.size(dimensionResource(id = R.dimen._20sdp)))
    }
}

sealed class ExpandAction(
    val actionName: String,
    val actionIcon: ImageVector,
    val contentDescription: String?,
) {
    /*object MuteState {
        class Mute(
            actionName: String = "Mute",
            actionIcon: ImageVector = Icons.Rounded.VolumeMute,
            contentDescription: String? = null
        ) :
            ExpandAction(
                actionName = actionName,
                actionIcon = actionIcon,
                contentDescription = contentDescription
            )

        class UnMute(
            actionName: String = "UnMute",
            actionIcon: ImageVector = Icons.Rounded.VolumeUp, contentDescription: String? = null
        ) :
            ExpandAction(
                actionName = actionName,
                actionIcon = actionIcon,
                contentDescription = contentDescription
            )
    }*/

    class AllLeaks(
        actionName: String = "All Leaks",
        actionIcon: ImageVector = Icons.Rounded.History,
        contentDescription: String? = null,
    ) : ExpandAction(
        actionName = actionName,
        actionIcon = actionIcon,
        contentDescription = contentDescription
    )

    class Edit(
        actionName: String = "Edit",
        actionIcon: ImageVector = Icons.Rounded.EditNote,
        contentDescription: String? = null,
    ) : ExpandAction(
        actionName = actionName,
        actionIcon = actionIcon,
        contentDescription = contentDescription
    )

    class NewLeaks(
        actionName: String = "New Leaks",
        actionIcon: ImageVector = Icons.Rounded.NewReleases,
        contentDescription: String? = null,
    ) : ExpandAction(
        actionName = actionName,
        actionIcon = actionIcon,
        contentDescription = contentDescription
    )
}

@Composable
fun ScheduleItemExpandedPart(
    modifier: Modifier = Modifier,
    actions: List<ExpandAction>,
    onActionClick: (ExpandAction) -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        actions.take(4).forEach {
            TextWithIconCardItem(
                modifier = Modifier.weight(1f),
                expandAction = it,
                onClick = onActionClick
            )
        }
    }
}

@Composable
fun TextWithIconCardItem(
    modifier: Modifier = Modifier,
    expandAction: ExpandAction,
    onClick: (ExpandAction) -> Unit = {},
) {
    Column(
        modifier = modifier
            .clickable(role = Role.Tab) {
                onClick(expandAction)
            }
            .padding(
                vertical = dimensionResource(id = R.dimen._7sdp),
                horizontal = dimensionResource(id = R.dimen._10sdp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Icon(
            imageVector = expandAction.actionIcon,
            contentDescription = expandAction.contentDescription
        )
        Spacer(Modifier.height(dimensionResource(id = R.dimen._1sdp)))
        Text(text = expandAction.actionName)
    }
}

@ExperimentalMaterialApi
@Composable
fun ScheduleLazyList(
    modifier: Modifier = Modifier,
    schedulesItemsState: List<SchedulesItemUiState>,
    viewModel: SchedulesViewModel,
    onExpandNavigation: (ExpandAction, Schedule) -> Unit = { _, _ -> },
) {

    LazyColumn(modifier = modifier.padding(top = dimensionResource(id = R.dimen._2sdp))) {

        items(items = schedulesItemsState, key = { it.schedule.id }) { itemUiState ->

            SwipeItem(modifier = Modifier.padding(horizontal = 4.dp),
                onDismissed = { viewModel.deleteScheduleRetractable(itemUiState.schedule.wrappedId) }
            ) {

//            var muteState by remember { mutableStateOf(schedule.isMuted) }
                ScheduleListItem(
                    itemUiState = itemUiState,
                    expandActions = listOf(
                        //ExpandAction.Edit(),
                        ExpandAction.AllLeaks(),
                        ExpandAction.NewLeaks()
                    ),
                    isExpanded = itemUiState.isItemExpanded,
                    onActionClick = { expandAction ->
                        onExpandNavigation(
                            expandAction,
                            itemUiState.schedule
                        )
                    },
                    onExpandClick = { schedule -> viewModel.expandSchedule(schedule.wrappedId) },
                )
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun SwipeItem(
    modifier: Modifier = Modifier,
    onDismissedToStart: () -> Unit = {},
    onDismissedToEnd: () -> Unit = {},
    onDismissed: () -> Unit = {},
    dismissContent: @Composable () -> Unit,
) {

    val dismissState = rememberDismissState(confirmStateChange = {
        when (it) {
            DismissValue.DismissedToStart -> onDismissedToStart().also { onDismissed() }
            DismissValue.DismissedToEnd -> onDismissedToEnd().also { onDismissed() }
            else -> Unit
        }

        true
    })

    SwipeToDismiss(
        state = dismissState,
        modifier = modifier,
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
        dismissThresholds = { direction ->
//            FractionalThreshold(if (direction == DismissDirection.StartToEnd) 0.25f else 0.5f)
            FractionalThreshold(0.25f)
        },
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss

            val color by animateColorAsState(
                targetValue = when (dismissState.targetValue) {
                    DismissValue.Default -> Color.Gray //colorResource(R.color.color_on_primary_custom)
                    /*DismissValue.DismissedToEnd -> Color.Green
                    DismissValue.DismissedToStart -> Color.Red*/
                    else -> Color.Red.copy(alpha = .8f)
                }
            )

            val (icon, alignment) = when (direction) {
//                DismissDirection.StartToEnd -> Icons.Default.Done to Alignment.CenterStart
                DismissDirection.StartToEnd -> Icons.Default.Delete to Alignment.CenterStart
                DismissDirection.EndToStart -> Icons.Default.Delete to Alignment.CenterEnd
            }

            val scale by animateFloatAsState(
                targetValue =
                if (dismissState.targetValue == DismissValue.Default) 1f
                else 1.25f
            )

            //background draw
            Box(
                modifier = Modifier

                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.scale(scale)
                )
            }
        }
        //dismissContent
    ) {
        dismissContent()
    }


}

@ExperimentalMaterialApi
@Preview(backgroundColor = 12440286L, showBackground = true)
@Composable
private fun PreviewList() {
    MyGuard {
//        val check = _Schedule(
//            query = "testExample@example.com",
//            queryType = QueryType.EMAIL,
//            unSeenBreaches = 3
//        )
////        ScheduleListItem(scheduleCheck = check)
//        val checkList = List(19) {
//            _Schedule(
//                query = "testExample$it@example.com",
//                queryType = if (it % 2 == 0) QueryType.EMAIL else QueryType.Phone,
//                unSeenBreaches = Random.nextInt(0, 4)
//            )
//        }
        MessageCounterAndLastOneTime(lastMessageTime = "now", messagesCount = 12)
        //ScheduleLazyList(scheduleList = checkList)
//        ScheduleListItemMainPart(scheduleCheck = check){}
    }
}

@ExperimentalMaterialApi
@Composable
@Deprecated("", ReplaceWith("ScheduleLazyList(viewModel = )"))
fun MenuItemImpl(schedule: Schedule) {
    var expandState = remember { mutableStateOf(false) }
    val elevationDp by animateDpAsState(targetValue = if (expandState.value) 4.dp else Dp.Hairline)
    val paddingDp by animateDpAsState(targetValue = if (expandState.value) 6.dp else Dp.Hairline)

    val colorAnim by animateColorAsState(
        targetValue =
        colorResource(
            id = if (expandState.value) R.color.color_on_background_custom else
                R.color.color_on_primary_custom
        )
    )

    var float by remember {
        mutableStateOf(0f)
    }
    val draggable = rememberDraggableState(onDelta = { float = it })
    //  val dragDp by animateFloatAsState(targetValue = float)
//    val swipeable= rememberSwipeableState(source = 0)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            // .offset(x = dragDp.dp)
            //  .swipeable(state = swipeable, anchors = emptyMap(), orientation = Orientation.Horizontal, reverseDirection = true)
            //  .draggable(state = draggable, Orientation.Horizontal, reverseDirection = true)
            .padding(horizontal = paddingDp),
        shape = RoundedCornerShape(20.dp),
        elevation = elevationDp,
        color = colorAnim,
//        border = BorderStroke(1.dp, Color.Gray),
    ) {
//        ScheduleListItem(
//            scheduleCheck = scheduleCheck,
//            expandState = expandState,
//            modifier = Modifier.padding(top = 8.dp, start = 8.dp)
//        )

    }
}
