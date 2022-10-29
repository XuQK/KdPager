package github.xuqk.kdpager

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
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun rememberKdPagerState(): KdPagerState = rememberSaveable(saver = KdPagerState.Saver) {
    KdPagerState()
}

/**
 * 由于目前无法实现初次初始化时，预设 currentPage，所以带参数的私有构造方法仅用于状态恢复，故设为 private
 */
@Stable
class KdPagerState private constructor(@IntRange(from = 0) initialScrollValue: Int = 0) :
    ScrollableState {

    constructor() : this(0)

    internal val scrollState = ScrollState(initialScrollValue)

    /**
     * 记录的 Pager 宽度，用于 Pager 的子 Box 宽度设置
     *
     * 此值会在重组的时候设置初始值，初始值为 1.1 倍屏幕宽度。
     * 然后在 Pager 尺寸测量完毕后，被设置为准确值。
     *
     * 此值如果不记录在这里，那当 Pager 组件消失，然后重新出现时，如果此时 scrollState.value 不为 0，视觉效果会出现问题
     */
    internal var pageWidth: Dp = Dp.Unspecified

    private var pageBounds = mutableListOf<Rect>()

    private var itemSpacing: Float = 0f

    private val _currentPage: Int by derivedStateOf {
        val centerX = (pageBounds.firstOrNull()?.width ?: 0f) / 2 + scrollState.value
        pageBounds.forEachIndexed { index, rect ->
            if (index == 0) {
                if (centerX <= (rect.right + itemSpacing / 2)) return@derivedStateOf 0
            } else {
                if (centerX >= (rect.left - itemSpacing / 2) && centerX <= (rect.right + itemSpacing / 2)) {
                    return@derivedStateOf index
                }
            }
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
        val currentPageBounds = pageBounds.getOrNull(_currentPage) ?: return@derivedStateOf 0f
        (scrollState.value - currentPageBounds.left) / currentPageBounds.width
    }

    /**
     * 更新 Pager 宽度
     */
    internal fun updatePageWidth(width: Dp, force: Boolean) {
        if (pageWidth == Dp.Unspecified || force) {
            pageWidth = width
        }
    }

    internal fun updatePageBounds(count: Int, index: Int, bounds: Rect, itemSpacing: Float) {
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

    internal suspend fun fling(initialVelocity: Float, scrollBy: (Float) -> Unit): Float {
        // fling 速度在 -300 ~ 300 之间的，被认为是静止状态
        val targetPage = when {
            initialVelocity > 300 -> {
                if (currentPageOffset > 0) {
                    currentPage + 1
                } else {
                    currentPage
                }
            }
            initialVelocity < -300 -> {
                if (currentPageOffset > 0) {
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
        Animatable(0f).animateTo(
            finalValue,
            spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow),
            initialVelocity
        ) {
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
         * The default [Saver] implementation for [KdPagerState].
         */
        val Saver: Saver<KdPagerState, *> = listSaver(
            save = {
                listOf<Any>(
                    it.scrollState.value,
                )
            },
            restore = {
                KdPagerState(
                    initialScrollValue = it[0] as Int,
                )
            }
        )
    }
}
