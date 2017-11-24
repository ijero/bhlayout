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
	
		compile 'cn.ijero.bhlayout:bhlayout:0.1.0'
		
	可能出现无法下载的情况，需要在Project的build.gradle添加一下源：
	
        allprojects {
            repositories {
                google()
                jcenter()
                maven {
                    url  "https://dl.bintray.com/jero/android"
                }
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

6. 简单示例：

	这里简单介绍一下接口用法，并不是实际的demo效果：
	
		class MainActivity : AppCompatActivity(), BHLayout.OnStateChangeListener, BHLayout.OnDragCallback {
		    // 该方法的返回结果直接决定了是否支持拖拽，可以参考demo中的解决首页recyclerView上下滑动冲突所做的操作，
		    // 通过返回recyclerView的滑动距离是否等于0，才让这个组件进行工作。
		    override fun onDragEnable(): Boolean {
		        return true
		    }
		
		    override fun onStateChange(view: View?, state: BHLayout.State, snapState: BHLayout.SnapState, dx: Int, dy: Int) {
		        // 参考demo中的示例，根据滑动的dx或者dy来设置其他的view进行透明度/位移/缩放等动画可以实现更加炫酷的效果
		        // view 当前事件产生的view
		        // state 当前的滑动事件
		        // snapState 切换事件
		        // dx x方向上的变化距离
		        // dy y方向上的变化距离
		    }
		
		    override fun onCreate(savedInstanceState: Bundle?) {
		        super.onCreate(savedInstanceState)
		        setContentView(R.layout.activity_main)
		
		        // 设置滑动监听器
		        bhLayout.setOnStateChangeListener(this)
		
		        // 设置拖拽回调
		        bhLayout.setOnDragCallback(this)
		
		        // 获取当前的BrowserHomeLayout.State
		        bhLayout.getCurrentState()
		        
		    }
		
		}

7. 意见反馈

	[提交改进意见或反馈](https://github.com/ijero/browser-home-layout/issues)
