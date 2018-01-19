package cn.ijero.appjava;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author Jero . Created on 2018/1/19.
 */

class RightRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Integer> res;
    private ArrayList<String> titles;

    RightRecyclerAdapter() {
        res = new ArrayList<>();
        res.add(R.mipmap.logo_baidu);
        res.add(R.mipmap.logo_huajiao);
        res.add(R.mipmap.logo_alipay);
        res.add(R.mipmap.logo_google);

        titles = new ArrayList<>();
        titles.add("百度");
        titles.add("花椒直播");
        titles.add("支付宝");
        titles.add("Google");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_right_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).notifyUI(holder.getAdapterPosition() % 4);
        }
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        void notifyUI(int position) {
            ((ImageView) itemView.findViewById(R.id.itemRightIconImageView)).setImageResource(res.get(position));
            ((TextView) itemView.findViewById(R.id.itemRightTitleTextView)).setText(titles.get(position));
        }
    }
}
