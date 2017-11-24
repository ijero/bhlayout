package cn.ijero.bhlayout

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_home_recycler.view.*

/**
 * Created by Administrator on 2017/11/22.
 */
class MainRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is ViewHolder){
            holder.notifyUI()
        }
    }

    override fun getItemCount() = 40

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_home_recycler, parent, false))
    }

    private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun notifyUI() {
            itemView.itemTextView.text = "测试条目 $adapterPosition 测试条目 测试条目 测试条目 测试条目 测试条目 测试条目"
        }
    }
}