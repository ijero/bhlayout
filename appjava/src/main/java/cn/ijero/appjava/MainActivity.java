package cn.ijero.appjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cn.ijero.bhlayout.BHLayout;

public class MainActivity extends AppCompatActivity implements BHLayout.OnDragBHLayoutCallback, BHLayout.OnStateChangeListener {

    private View weatherLayout;
    private View naviLayout;
    private View overrideView;
    private View searchBarLayout;

    @Override
    public boolean onDragBHLayoutEnable() {
        // 返回true表示控件支持拖拽，false则关闭拖拽功能
        return true;
    }

    @Override
    public void onStateChange(View view, BHLayout.State state, BHLayout.SnapState snapState, int dx, int dy) {
        int headerHeight = getResources().getDimensionPixelSize(R.dimen.dimen_header_height);
        int toolbarHeight = getResources().getDimensionPixelSize(R.dimen.dimen_toolbar_height);

        // 根据滑动距离计算搜索框应该移动的位移
        searchBarLayout.setTop((int) (dy * ((float) dy / (headerHeight + toolbarHeight))));

        // 设置透明度和位移
        float alpha = (float) dy / (headerHeight - toolbarHeight);
        weatherLayout.setAlpha(alpha);
        weatherLayout.setTop(dy - headerHeight + toolbarHeight + ((ViewGroup.MarginLayoutParams) weatherLayout.getLayoutParams()).topMargin);

        naviLayout.setAlpha(alpha);
        naviLayout.setTop(dy - naviLayout.getMeasuredHeight() + toolbarHeight - ((ViewGroup.MarginLayoutParams) naviLayout.getLayoutParams()).bottomMargin);

        overrideView.setAlpha((1 - (float) dy / (headerHeight - toolbarHeight)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initHeaderView();
        initMainRecyclerView();
        initBHLayout();
        initRightRecyclerView();
    }

    private void initHeaderView() {
        searchBarLayout = findViewById(R.id.searchBarLayout);
        overrideView = findViewById(R.id.overrideView);
        weatherLayout = findViewById(R.id.headerWeatherLayout);
        naviLayout = findViewById(R.id.headerNaviLayout);
    }

    private void initRightRecyclerView() {
        RecyclerView rightRecyclerView = findViewById(R.id.rightRecyclerView);
        rightRecyclerView.setAdapter(new RightRecyclerAdapter());

    }

    private void initMainRecyclerView() {
        RecyclerView homeRecyclerView = findViewById(R.id.mainRecyclerView);
        homeRecyclerView.addItemDecoration(new DividerItemDecoration(this, 0));
        MainRecyclerAdapter mainRecyclerAdapter = new MainRecyclerAdapter();
        homeRecyclerView.setAdapter(mainRecyclerAdapter);
    }

    private void initBHLayout() {
        BHLayout bhLayout = findViewById(R.id.browserHomeLayout);
        // 设置拖拽回调，可以通过回调方法控制控件工作状态
        bhLayout.setOnDragCallback(this);
        // 设置状态回调
        bhLayout.setOnStateChangeListener(this);
    }

}
