package cn.ijero.appjava;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Jero . Created on 2018/1/19.
 */

public class MainRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_recycler, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).notifyUI();
        }
    }

    @Override
    public int getItemCount() {
        return 40;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View itemView) {
            super(itemView);
        }

        void notifyUI() {
            ((TextView) itemView.findViewById(R.id.itemTextView)).setText("测试条目 $adapterPosition 测试条目 测试条目 测试条目 测试条目 测试条目 测试条目");

        }
    }

}
