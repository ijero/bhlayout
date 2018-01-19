# BHLayout

以主流浏览器首页布局为参考的一个自定义Layout

### 下载安装Demo

[点击下载Demo Apk](./public/apk/app-debug.apk)

-----

自定义ViewGroup实现浏览器首页滑动效果。

自定义的目的：

	1. 完成需求（重中之重）。
	2. 解决使用google自带控件CoordinatorLayout+AppBarLayout+CollapsingToolbarLayout实现此效果的快速滑动的bug。
	3. 避免使用ViewPager实现时使Activity/Fragment代码逻辑变得复杂且庞大的问题。

## 效果

### 360浏览器首页效果图：

[点击查看（图片较大，流量慎点）](./public/images/360_home.gif)

### UC浏览器首页效果图：

[点击查看（图片较大，流量慎点）](./public/images/uc_home.gif)

### 第一个初始效果：

[点击查看（图片较大，流量慎点）](./public/images/first_demo.gif)

### 最终效果（添加一些额外效果后）：

![最终效果](./public/images/last_demo.gif)

----

## 简单入门：

1. 原理

	此控件由三部分组成，主页header、主页内容和右侧内容，所以子控件数量不能少于三个！如图：

	![组成图片](./public/images/base_01.jpg)

2. 导入项目
	
	在项目module的build.gradle中添加：
	
		compile 'cn.ijero.bhlayout:bhlayout:0.1.2'
		
	可能出现无法下载的情况，需要在Project的build.gradle添加一下源：
	
        allprojects {
            repositories {
                jcenter()
            }
        }

3. 资源文件中使用

	在xml中使用BrowserHomeLayout作为根布局，该布局的子布局数量必须大于等于三个：
	
		<?xml version="1.0" encoding="utf-8"?>
		<cn.ijero.bhlayout.BHLayout xmlns:android="http://schemas.android.com/apk/res/android"
		    xmlns:app="http://schemas.android.com/apk/res-auto"
		    xmlns:tools="http://schemas.android.com/tools"
		    android:id="@+id/bhLayout"
		    android:layout_width="match_parent"
		    android:layout_height="match_parent"
		    app:expandedHeight="200dp"
		    app:unexpandedHeight="56dp"
		    app:expanded="true">
	
		    <FrameLayout
		        android:layout_width="match_parent"
		        android:layout_height="match_parent"
		        android:background="#E92640" />
		
		    <FrameLayout
		        android:layout_width="match_parent"
		        android:layout_height="match_parent"
		        android:background="#3265A3" />
		
		    <FrameLayout
		        android:layout_width="match_parent"
		        android:layout_height="match_parent"
		        android:background="#DDDC30" />
		
		</cn.ijero.bhlayout.BHLayout>

4. 属性

		app:expandedHeight="200dp" // 设置header展开的最高高度
	    app:unexpandedHeight="56dp" // 设置header收起的最低高度
	    app:expanded="true" // 设置header默认的展开状态 true为展开，false为收起

5. 监听实现更多效果

	在java中，对BHLayout设置滑动监听器（setOnStateChangeListener），可以实时监听滑动状态，以实现更多的动画效果。可以参考示例程序：[demo](./app)

6. Demo简单示例（Kotlin）：

		package cn.ijero.bhlayout
        
        import android.os.Bundle
        import android.support.v7.app.AppCompatActivity
        import android.support.v7.widget.DividerItemDecoration
        import android.view.View
        import android.view.ViewGroup
        import kotlinx.android.synthetic.main.activity_main.*
        import kotlinx.android.synthetic.main.layout_home_content.*
        import kotlinx.android.synthetic.main.layout_home_header.*
        import kotlinx.android.synthetic.main.layout_right_content.*
        
        class MainActivity : AppCompatActivity(), BHLayout.OnStateChangeListener, BHLayout.OnDragBHLayoutCallback {
            // 该方法的返回结果直接决定了是否支持拖拽
            // 通过返回recyclerView的滑动距离等于0时，才让这个组件进行工作。
            override fun onDragBHLayoutEnable(): Boolean {
                return mainRecyclerView.computeVerticalScrollOffset() == 0
            }
        
            override fun onStateChange(view: View?, state: BHLayout.State, snapState: BHLayout.SnapState, dx: Int, dy: Int) {
                val headerHeight = resources.getDimensionPixelSize(R.dimen.dimen_header_height)
                val toolbarHeight = resources.getDimensionPixelSize(R.dimen.dimen_toolbar_height)
        
                // 根据滑动距离计算搜索框应该移动的位移
                val searchTop = (dy * (dy.toFloat() / (headerHeight + toolbarHeight))).toInt()
                searchBarLayout.top = searchTop
        
                // 设置透明度和位移
                headerWeatherLayout.alpha = dy.toFloat() / (headerHeight - toolbarHeight)
                headerNaviLayout.alpha = dy.toFloat() / (headerHeight - toolbarHeight)
                overrideView.alpha = (1 - dy.toFloat() / (headerHeight - toolbarHeight))
                headerWeatherLayout.top = dy - headerHeight + toolbarHeight + (headerWeatherLayout.layoutParams as ViewGroup.MarginLayoutParams).topMargin
                headerNaviLayout.top = dy - headerNaviLayout.measuredHeight + toolbarHeight - (headerNaviLayout.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin
            }
        
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_main)
        
                browserHomeLayout.apply {
                    setOnStateChangeListener(this@MainActivity)
                    setOnDragCallback(this@MainActivity)
                }
        
                mainRecyclerView.apply {
                    adapter = MainRecyclerAdapter()
                    addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
                }
        
                rightRecyclerView.apply {
                    adapter = RightRecyclerAdapter()
                }
            }
        
        }


7. 意见反馈

	[提交改进意见或反馈](https://github.com/ijero/browser-home-layout/issues)
