[![](https://jitpack.io/v/XuQK/KdPager.svg)](https://jitpack.io/#XuQK/KdPager)

# 声明

这是一个基于 Row 实现的，无 Lazy 功能的，横向滑动的 Pager，为何造这么个轮子，理由如下：

1. Accompanist 版本的 Pager 是基于 Lazy 系列组件实现，且还未实现 offscreenPageLimit 功能，由于 Lazy 系列组件糟糕的性能（尤其在低版本和低端机器上，完全卡得无法使用），导致 Pager 在业务中实际使用时，只能用于超级轻量的功能，比如仅展示图片，完全无法承载主页的复杂功能，如果 Pager 承载的页面稍微复杂一点，然后页面数 > 2，在滑动到渲染新页面的过程中，会有明显卡顿。
2. 主页的 Pager 使用，一般同时最多只会有 2-3 个页面，所以以更多的内存空间换取流畅的用户体验是非常划算的。
3. Accompanist 版本的 Pager 的动画体验实在是非常一般，让人觉得很别扭。

希望 Google 早日优化好 Lazy 系列组件的性能，让此项目早日废弃。

# 效果

![](demo.mp4)

# 使用：

1. 将JitPack存储库添加到构建文件(项目根目录下build.gradle文件)

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2. 添加依赖项

```groovy
// 版本号参看Release
implementation 'com.github.XuQK:KdPager:versionCode'
```
