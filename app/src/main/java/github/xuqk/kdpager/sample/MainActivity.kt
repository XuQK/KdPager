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
                        .onSizeChanged {
                            Log.d(
                                "MainActivity",
                                "onCreate: $page -> $it"
                            )
                        }, horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "TabIndex: $page", fontSize = 30.sp)
                        Text(
                            text = "IT之家了解到，该研究的主要作者、佛蒙特大学精神病学助理教授贝德-查拉尼（Bader Chaarani）告诉法新社，他自己是一个喜欢玩游戏的人，并且拥有神经影像学方面的专业知识，所以自然而然地被吸引到这个话题上。\n" +
                                    "\n" +
                                    "之前的研究集中在电子游戏有害的影响上，经常将游戏与抑郁症和攻击性增加联系起来。但查拉尼表示，这些研究由于参与者数量相对较少而受到限制，特别是那些涉及大脑成像的研究。\n" +
                                    "\n" +
                                    "在新的研究中，查拉尼及其同事分析了正在进行的大型青少年大脑认知发展（ABCD）研究的数据，该研究由美国国立卫生研究院资助。他们研究了大约 2000 名九岁和十岁儿童的调查答案、认知测试结果和大脑图像，这些儿童被分为两组：从不玩游戏的儿童和每天玩三个小时或以上的儿童。\n" +
                                    "\n" +
                                    "每组儿童被安排进行两个任务。第一个任务是看到指向左边或右边的箭头，要求孩子们以最快的速度向左或向右按。他们还被要求在看到“停止”信号时不要按任何东西，以衡量他们对冲动的控制能力如何。\n" +
                                    "\n" +
                                    "在第二项任务中，研究人员会向他们会展示一张人脸图片，让他们尝试记住。随后再展示一些人脸图片，询问他们图片是否跟之前展示的人脸图片相匹配，以测试他们的“工作记忆”。“工作记忆”是一种对信息进行暂时加工和贮存的容量有限的记忆系统，在许多复杂的认知活动中起重要作用。\n" +
                                    "\n" +
                                    "在使用统计学方法控制变量后，例如父母的收入、智商和心理健康症状，研究小组发现玩视频游戏的一组儿童在这两项任务中的表现始终更好。"
                        )
                    }

                }
            }
        }
    }
}
