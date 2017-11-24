package cn.ijero.bhlayout

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_right_recycler.view.*

/**
 * Created by Jero on 2017/11/23.
 */
class RightRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val titles = arrayListOf("百度", "花椒直播", "支付宝", "Google")
    private val res = arrayListOf(R.mipmap.logo_baidu, R.mipmap.logo_huajiao, R.mipmap.logo_alipay, R.mipmap.logo_google)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is ViewHolder) {
            holder.notifyUI(holder.adapterPosition % 4)
        }
    }

    override fun getItemCount() = 20

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_right_recycler, parent, false))
    }

    private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun notifyUI(position: Int) {
            itemView.itemRightIconImageView.setImageResource(res[position])
            itemView.itemRightTitleTextView.text = titles[position]
        }
    }
}