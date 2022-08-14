package com.rmsr.myguard.presentation.ui.schedules.mainfragment.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.composethemeadapter.createMdcTheme
import com.rmsr.myguard.R
import com.rmsr.myguard.presentation.ui.schedules.detailfragment.ScheduleDetailFragment
import com.rmsr.myguard.presentation.ui.schedules.mainfragment.SchedulesMainFragmentDirections
import com.rmsr.myguard.presentation.ui.schedules.mainfragment.SchedulesViewModel
import com.rmsr.myguard.presentation.util.UserCommunicate
import com.rmsr.myguard.presentation.util.displayToast
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private typealias RequestLeaks = ScheduleDetailFragment.Companion.RequestedLeaks


@ExperimentalMaterialApi
@Composable
fun ScheduleScreen(viewModel: SchedulesViewModel, navController: NavController? = null) {
    fun changeScheduleTime() = navController!!.navigate(
        SchedulesMainFragmentDirections.actionScheduleFragmentToSchedulesCheckIntervalDialog()
    )

    val uiState by viewModel.uiState.collectAsState()
    val scaffoldState = rememberScaffoldState()

    uiState.userMessages.firstOrNull()?.let { message ->
        DisplayUserMessage(
            message = message, scaffoldState = scaffoldState,
            afterDisplayed = { viewModel.userMessageShown(message.id) }
        )
    }

    val fabHeightPx = with(LocalDensity.current) { 72.dp.roundToPx().toFloat() }

    var fabOffsetHeightPx by remember { mutableStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {

                val delta = available.y * 2
                val newOffset = fabOffsetHeightPx + delta
                fabOffsetHeightPx = newOffset.coerceIn(-fabHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = colorResource(id = R.color.window_background_custom),//MaterialTheme.colors.background,
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection),
        floatingActionButton = {

            FloatingActionButton(modifier = Modifier.offset {
                IntOffset(x = 0, y = -fabOffsetHeightPx.roundToInt())
            },
                shape = RoundedCornerShape(16.dp),
                onClick = {
                    navController?.navigate(
                        SchedulesMainFragmentDirections.actionScheduleFragmentToAddScheduleFragment()
                    )
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = null
                )
            }

        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

//    TopPart(foundLeaks = viewModel.getTotalFoundLeaks(), onTimeClick = ::changeScheduleTime)
            if (uiState.schedulesItems.isEmpty())
                SchedulesWelcomeMessage(isLoading = uiState.isLoadingSchedules)
            else
                ScheduleLazyList(
                    schedulesItemsState = uiState.schedulesItems,
                    viewModel = viewModel
                ) { expandAction, schedule ->

                    val action = when (expandAction) {
                        is ExpandAction.Edit ->
                            SchedulesMainFragmentDirections.actionScheduleFragmentToAddScheduleFragment()
                                .apply { isInEditMode = true; scheduleToEditId = schedule.id }
                        is ExpandAction.AllLeaks -> SchedulesMainFragmentDirections.actionScheduleFragmentToScheduleDetailFragment(
                            schedule.id,
                            schedule.searchQuery.hint ?: schedule.searchQuery.query,
                            RequestLeaks.ALL_LEAKS.name
                        )
                        is ExpandAction.NewLeaks -> SchedulesMainFragmentDirections.actionScheduleFragmentToScheduleDetailFragment(
                            schedule.id,
                            schedule.searchQuery.hint ?: schedule.searchQuery.query,
                            RequestLeaks.NEW_LEAKS.name,
                        )
                    }

                    navController?.navigate(action)
                }
        }
    }
}

@Composable
fun TopPart(modifier: Modifier = Modifier, foundLeaks: Int = 0, onTimeClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        Column(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Gray.copy(alpha = 0.8f), Color.Blue.copy(alpha = 0.6f))
                    )
                ),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Stay safe and keep it on Schedules",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(top = 20.dp, start = 12.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Get alerts when your info appears in a known breach.",
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 4.dp, start = 20.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colors.background),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Top
        ) {

            TotalFoundLeaks(total = foundLeaks, modifier = Modifier.offset(y = (-45).dp))

            var baseLine by remember {
                mutableStateOf(0f)
            }
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                Text(text = "last Found Leaks: Brrreeaacchh")
                Text(
                    text = "Change Schedule Time",
                    color = Color.Blue,
                    style = TextStyle(textDecoration = TextDecoration.Underline),
                    modifier = Modifier.clickable { onTimeClick() }
                )
            }
        }

    }
}

@Composable
fun TotalFoundLeaks(total: Number, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .wrapContentSize(),
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        border = BorderStroke(
            3.dp,
            Brush.linearGradient(
                listOf(
                    Color.Gray.copy(alpha = 0.1f),
                    Color.Blue.copy(alpha = 0.1f)
                )
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = "TOTAL", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Text(text = total.toString(), fontSize = 30.sp, fontWeight = FontWeight.Medium)

            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = "Found Leaks".uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


@Composable
fun DisplayUserMessage(
    message: UserCommunicate,
    scaffoldState: ScaffoldState,
    afterDisplayed: () -> Unit = {},
) {
    val context = LocalContext.current
    val userMsg = message.msg.asString(context)

    LaunchedEffect(key1 = message.id) {
        when (message) {
            is UserCommunicate.ToastMessage -> {
                context.displayToast(userMsg, message.duration)
                delay(message.duration * 1000L)
            }
            is UserCommunicate.SnackbarMessage -> {
                var result: SnackbarResult? = null
                try {
                    val actionLabel = message.actionLabel
                    result = scaffoldState.snackbarHostState.showSnackbar(
                        userMsg,
                        actionLabel,
                        message.duration
                    )
                }
                // null means screen changed, so snackbar action not reachable to user any way.
                finally {
                    when (result) {
                        null -> {
                            message.onDismissed.invoke()
                            afterDisplayed()
                        }
                        SnackbarResult.Dismissed -> message.onDismissed.invoke()
                        SnackbarResult.ActionPerformed -> message.onAction.invoke()
                    }
                }
            }
        }
        afterDisplayed()
    }
}

@Composable
private fun SchedulesWelcomeMessage(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val (mWidth, mHeight) = maxWidth to maxHeight
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                if (isLoading.not()) {
                    Image(
                        painter = painterResource(id = R.drawable.no_schedule),
                        modifier = Modifier.fillMaxWidth(0.5f),
                        contentDescription = null,
                        alpha = 0.5f
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "You can Schedule Emails-Phones,\nand MyGuard will check them periodically.",
                        textAlign = TextAlign.Center
                    )
                } else {
                    ImageShimmer(
                        painterResource = painterResource(id = R.drawable.no_schedule),
                        maxWidth = mWidth,
                        maxHeight = mHeight,
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageShimmer(
    painterResource: Painter,
    maxWidth: Dp,
    maxHeight: Dp,
) {
    val backgroundColor = colorResource(id = R.color.window_background_custom)
    val colors = listOf(
        backgroundColor,
        Color(0xFF8fa5a5),
        backgroundColor
    )

    val (widthPx, heightPx) = with(LocalDensity.current) { maxWidth.toPx() to maxHeight.toPx() }
    val gradiantWidth = .3f * (widthPx * .5f)

    val infiniteTransition = rememberInfiniteTransition()
    val xShimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (widthPx * .5f) + gradiantWidth,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, 200)
        )
    )
    val yShimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = heightPx + gradiantWidth,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, 200)
        )
    )
    val gradiantBrush = Brush.horizontalGradient(
        colors = colors,
        startX = xShimmer - gradiantWidth,
        endX = xShimmer,
        tileMode = TileMode.Clamp
    )

    Image(
        painter = painterResource,
        modifier = Modifier
            .width(maxWidth * .5f)
            .background(gradiantBrush),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alpha = 0.5f,
    )

}

@Preview(backgroundColor = 12440286L, showBackground = true)
@Composable
private fun PreviewShimmer() {

//    ImageShimmer()
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    MyGuard {
//        TopPart(foundLeaks = 93)
        //TotalFoundLeaks(total = 93)
    }
}

@Composable
private fun MyGuardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {

    val (colors, types, shapes) = createMdcTheme(
        context = LocalContext.current,
        layoutDirection = LocalLayoutDirection.current
    )

    if (colors != null && types != null && shapes != null) {
        MaterialTheme(
            colors = colors,
            typography = types,
            shapes = shapes
        ) {
            content()
        }
    } else {
        MdcTheme {
            content()
        }
    }
}

@Composable
fun MyGuard(content: @Composable () -> Unit) {
    MyGuardTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            content = content
        )
    }
}
