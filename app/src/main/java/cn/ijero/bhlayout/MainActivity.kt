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


class MainActivity : AppCompatActivity(), BHLayout.OnStateChangeListener, BHLayout.OnDragCallback {
    override fun onDragEnable(): Boolean {
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
