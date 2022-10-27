package github.xuqk.kdpager

import android.util.Log
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun rememberKdPagerState(
    @IntRange(from = 0) initialPage: Int = 0,
): KdPagerState = rememberSaveable(saver = KdPagerState.Saver) {
    KdPagerState(
        currentPage = initialPage,
    )
}

@Stable
class KdPagerState(
    @IntRange(from = 0) currentPage: Int = 0,
) : ScrollableState {
    val scrollState = ScrollState(0)

    var pagerSize = IntSize.Zero
    var pageBounds = mutableListOf<Rect>()

    var itemSpacing: Float = 0f

    private val _currentPage: Int by derivedStateOf {
        try {
            val centerX = pagerSize.width / 2 + scrollState.value
            pageBounds.forEachIndexed { index, rect ->
                if (index == 0) {
                    if (centerX <= (rect.right + itemSpacing / 2)) return@derivedStateOf 0
                } else {
                    if (centerX >= (rect.left - itemSpacing / 2) && centerX <= (rect.right + itemSpacing / 2)) {
                        return@derivedStateOf index
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        0
    }

    val currentPage: Int
        get() {
            return _currentPage
        }

    val interactionSource: InteractionSource
        get() = scrollState.interactionSource

    val pageCount: Int
        get() = pageBounds.size

    val currentPageOffset: Float by derivedStateOf {
        try {
            (pageBounds[_currentPage].left - scrollState.value) / pageBounds[_currentPage].width
        } catch (e: Exception) {
            e.printStackTrace()
            0f
        }
    }

    fun updatePageBounds(count: Int, index: Int, bounds: Rect, itemSpacing: Float) {
        this.itemSpacing = itemSpacing
        if (count != pageCount) {
            pageBounds = MutableList(count) { pageBounds.getOrElse(it) { Rect.Zero } }
        }
        pageBounds[index] = bounds
    }

    suspend fun animateScrollToPage(
        @IntRange(from = 0) page: Int,
        @FloatRange(from = -1.0, to = 1.0) pageOffset: Float = 0f,
    ) {
        val targetValue = pageBounds[page].left + pageBounds[page].width * pageOffset
        scrollState.animateScrollTo(targetValue.toInt())
    }

    suspend fun scrollToPage(
        @IntRange(from = 0) page: Int,
        @FloatRange(from = -1.0, to = 1.0) pageOffset: Float = 0f,
    ) {
        val targetValue = pageBounds[page].left + pageBounds[page].width * pageOffset
        scrollState.scrollTo(targetValue.toInt())
    }

    suspend fun fling(initialVelocity: Float, scrollBy: (Float) -> Unit): Float {
        // fling 速度在 -300 ~ 300 之间的，被认为是静止状态
        Log.d(
            "KdPagerState",
            "fling() called with: initialVelocity = $initialVelocity, scrollBy = $scrollBy"
        )
        val targetPage = when {
            initialVelocity > 300 -> {
                if (currentPageOffset < 0) {
                    currentPage + 1
                } else {
                    currentPage
                }
            }
            initialVelocity < -300 -> {
                if (currentPageOffset < 0) {
                    currentPage
                } else {
                    currentPage - 1
                }
            }
            else -> currentPage
        }
        pageBounds.getOrNull(targetPage) ?: return initialVelocity
        val targetValue = pageBounds[targetPage].left
        val finalValue = targetValue - scrollState.value
        var scrolledValue = 0f
        Animatable(0f).animateTo(finalValue, spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow), initialVelocity) {
            val v = if (finalValue > 0) {
                value.coerceAtMost(finalValue) - scrolledValue
            } else {
                value.coerceAtLeast(finalValue) - scrolledValue
            }
            scrollBy(v)
            scrolledValue = value
            if (abs(value) > abs(finalValue)) {
                MainScope().launch {
                    stop()
                    scrollToPage(targetPage)
                }
            }
        }
        return 0f
    }

    override val isScrollInProgress: Boolean
        get() = scrollState.isScrollInProgress

    override fun dispatchRawDelta(delta: Float): Float {
        return scrollState.dispatchRawDelta(delta)
    }

    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit
    ) {
        scrollState.scroll(scrollPriority, block)
    }

    companion object {
        /**
         * The default [Saver] implementation for [PagerState].
         */
        val Saver: Saver<KdPagerState, *> = listSaver(
            save = {
                listOf<Any>(
                    it.currentPage,
                )
            },
            restore = {
                KdPagerState(
                    currentPage = it[0] as Int,
                )
            }
        )
    }
}
