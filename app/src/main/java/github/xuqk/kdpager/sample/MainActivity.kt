package github.xuqk.kdpager.sample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.xuqk.kdpager.KdPager
import github.xuqk.kdpager.rememberKdPagerState
import kotlinx.coroutines.launch

const val SAMPLE_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer sed viverra dolor, at sollicitudin felis. Nullam lacus eros, pulvinar vitae facilisis sed, maximus eu metus. Nulla commodo rutrum tortor. Vivamus porta purus mi, eu tincidunt purus accumsan vel. Aenean condimentum, nisl et dignissim venenatis, ex quam vulputate nulla, tincidunt varius quam magna sit amet turpis. Nulla dictum justo nisi, iaculis ullamcorper dolor pulvinar vitae. Suspendisse potenti. Donec nec urna arcu. Praesent dolor dui, eleifend id mauris a, rhoncus egestas urna. Integer semper placerat gravida. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Mauris quis hendrerit turpis. Suspendisse potenti. Proin ullamcorper, est ut lobortis consequat, sapien lorem ultrices mauris, nec semper velit ex eu diam. Morbi blandit arcu ac orci malesuada fringilla.\n" +
        "\n" +
        "Curabitur rutrum libero eget sem tempor fermentum. Maecenas fringilla lacinia lorem vel vehicula. Quisque mi turpis, varius eu nunc nec, iaculis facilisis massa. Morbi porta ut ligula ut porta. Praesent vel nibh blandit, rutrum nunc laoreet, cursus dui. Praesent accumsan, eros id aliquet eleifend, dui erat ultricies lacus, id laoreet eros dui ac nisi. In tincidunt ut elit vel luctus. Proin at odio sed felis pellentesque hendrerit. Nunc rhoncus elit dolor, vel vulputate ante bibendum et. Pellentesque tempor maximus laoreet. In tempor pretium erat, in tristique libero consequat elementum. Etiam placerat turpis nec lacinia pharetra.\n" +
        "\n" +
        "Nunc condimentum tincidunt gravida. Nullam tincidunt purus id arcu consectetur blandit. Nunc accumsan purus justo, a egestas ipsum molestie et. Quisque pretium semper massa ac interdum. Nullam accumsan quam at leo pellentesque malesuada. Etiam quis nulla eros. Mauris convallis sem vel iaculis mollis. Aenean odio tortor, blandit ac vulputate vitae, egestas et justo. Ut imperdiet ipsum ut massa mattis, at egestas tortor mollis. Pellentesque in sem sodales, venenatis sem non, maximus metus. Suspendisse potenti. Suspendisse potenti. Proin non porttitor velit. Fusce auctor luctus tristique. Nam et erat mollis, finibus justo nec, dictum lacus.\n" +
        "\n" +
        "Morbi diam urna, viverra eget sem in, maximus vulputate leo. Sed ac ligula a eros iaculis tincidunt. Proin sed semper justo, a tincidunt orci. Donec tempor lectus leo, in cursus tortor efficitur sed. Aliquam erat volutpat. In sed urna velit. Integer non sollicitudin nunc. Integer malesuada, lacus sit amet aliquet commodo, libero mi iaculis augue, eu suscipit nibh metus vitae nisl.\n" +
        "\n" +
        "Phasellus pulvinar massa justo, vitae aliquam dolor iaculis at. Nam nec viverra est. Donec vestibulum lacus in diam luctus dignissim. Phasellus hendrerit lorem mauris. Aenean aliquam tincidunt rhoncus. Proin pretium risus velit, in lobortis est ultrices sit amet. Integer aliquam eu dui ac sagittis. Etiam pellentesque nibh tristique ornare tincidunt. Etiam in dolor pellentesque, accumsan erat sit amet, bibendum massa. Mauris vulputate tortor dui, quis laoreet mi volutpat non. Vivamus mattis feugiat sem. Phasellus hendrerit tincidunt purus, at semper nisl accumsan ac. Sed augue lacus, eleifend non dignissim a, dignissim eu ipsum."

class MainActivity : ComponentActivity() {
    private val colors = listOf(
        Color.DarkGray,
        Color.Gray,
        Color.LightGray,
        Color.White,
        Color.Red,
        Color.Green,
        Color.Blue,
        Color.Yellow,
        Color.Cyan,
        Color.Magenta,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scope = rememberCoroutineScope()
            val pagerState = rememberKdPagerState()
            var count by remember { mutableStateOf(4) }
            Column {
                Row(modifier = Modifier.height(40.dp)) {
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(colors[0])
                        .clickable {
                            scope.launch { pagerState.animateScrollToPage(0, 0f) }
                        }
                    )
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(colors[1])
                        .clickable {
                            scope.launch { pagerState.animateScrollToPage(1, 0f) }
                        }
                    )
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(colors[2])
                        .clickable {
                            scope.launch { pagerState.scrollToPage(2, 0f) }
                        }
                    )
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(colors[3])
                        .clickable {
                            scope.launch { pagerState.scrollToPage(3, 0f) }
                        }
                    )
                    Box(modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.Black)
                        .clickable {
                            count++
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Add Tab", color = Color.White)
                    }
                }

                KdPager(
                    count = count,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    state = pagerState,
                ) { page ->
                    Column(modifier = Modifier
                        .background(colors[page % colors.size])
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .onSizeChanged {
                            Log.d(
                                "MainActivity",
                                "onCreate: $page -> $it"
                            )
                        }, horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "TabIndex: $page", fontSize = 30.sp)
                        Text(
                            text = SAMPLE_TEXT
                        )
                    }
                }
            }
        }
    }
}
