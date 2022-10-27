package github.xuqk.kdpager

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp


@Composable
fun KdPager(
    count: Int,
    modifier: Modifier = Modifier,
    state: KdPagerState = rememberKdPagerState(),
    itemSpacing: Dp = 30.dp,
    contentPadding: PaddingValues = PaddingValues(30.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    flingBehavior: FlingBehavior = remember { SnapFlingBehavior(pagerState = state) },
    userScrollEnabled: Boolean = true,
    content: @Composable KdNoRecyclePagerScope.(page: Int) -> Unit,
) {

    val pagerScope = remember(state) { KdNoRecyclePagerScope(state) }
    val consumeFlingNestedScrollConnection = remember {
        ConsumeFlingNestedScrollConnection(
            consumeHorizontal = true,
            consumeVertical = false,
        )
    }

    var pagerSize: IntSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    Row(
        modifier = modifier
            .onSizeChanged {
                pagerSize = it
                state.pagerSize = it
            }
            .horizontalScroll(
                state = state.scrollState,
                enabled = userScrollEnabled,
                flingBehavior = flingBehavior,
            ),
        verticalAlignment = verticalAlignment,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing, Alignment.CenterHorizontally),
    ) {
        repeat(count) { page ->
            Box(
                Modifier
                    .onGloballyPositioned {
                        state.updatePageBounds(
                            count,
                            page,
                            it.boundsInParent(),
                            with(density) { itemSpacing.toPx() })
                    }
                    // We don't any nested flings to continue in the pager, so we add a
                    // connection which consumes them.
                    // See: https://github.com/google/accompanist/issues/347
                    .nestedScroll(connection = consumeFlingNestedScrollConnection)
                    // Constraint the content width to be <= than the width of the pager.
                    .width(with(density) { pagerSize.width.toDp() })
                    .wrapContentSize()
                    .padding(contentPadding)
            ) {
                pagerScope.content(page)
            }
        }
    }
}

private class SnapFlingBehavior(
    val pagerState: KdPagerState,
) : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        return pagerState.fling(initialVelocity) {
            scrollBy(it)
        }
    }
}

private class ConsumeFlingNestedScrollConnection(
    private val consumeHorizontal: Boolean,
    private val consumeVertical: Boolean,
) : NestedScrollConnection {
    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset = when (source) {
        // We can consume all resting fling scrolls so that they don't propagate up to the
        // Pager
        NestedScrollSource.Fling -> available.consume(consumeHorizontal, consumeVertical)
        else -> Offset.Zero
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        // We can consume all post fling velocity on the main-axis
        // so that it doesn't propagate up to the Pager
        return available.consume(consumeHorizontal, consumeVertical)
    }
}

private fun Offset.consume(
    consumeHorizontal: Boolean,
    consumeVertical: Boolean,
): Offset = Offset(
    x = if (consumeHorizontal) this.x else 0f,
    y = if (consumeVertical) this.y else 0f,
)

private fun Velocity.consume(
    consumeHorizontal: Boolean,
    consumeVertical: Boolean,
): Velocity = Velocity(
    x = if (consumeHorizontal) this.x else 0f,
    y = if (consumeVertical) this.y else 0f,
)

class KdNoRecyclePagerScope(
    private val state: KdPagerState,
) {
    val currentPage: Int get() = state.currentPage
    val currentPageOffset: Float get() = state.currentPageOffset
}