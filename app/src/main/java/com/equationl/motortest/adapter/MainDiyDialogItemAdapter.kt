package com.equationl.motortest.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.equationl.motortest.R


/**
 * FileName: MainDiyDialogItemAdapter
 * Author: equationl
 * Email: admin@likehide.com
 * Date: 2020/3/3 17:47
 * Description: 自由创作模式打开保存文件列表的适配器
 */
class MainDiyDialogItemAdapter(
    private var nameList: List<String>,
    private var dateList: List<String>,
    private var context: Context) :

    BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = View.inflate(context, R.layout.dialog_advanced_diy_item, null)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder= view.tag as ViewHolder
        }

        holder.textName.text = getItem(position)?.get(0)
        holder.textDate.text = getItem(position)?.get(1)

        holder.clickLayout.setOnClickListener {
            mItemClickListener?.onClickItem(position)
        }

        holder.button.setOnClickListener {
            mItemClickListener?.onClickDelete(position)
        }

        return view
    }

    override fun getItem(position: Int): ArrayList<String?>? {
        if (position == count) {
            return null
        }
        return arrayListOf(nameList[position], dateList[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return nameList.size
    }

    class ViewHolder(view: View) {
        var textName: TextView = view.findViewById(R.id.main_diy_dialog_item_text)
        var textDate: TextView = view.findViewById(R.id.main_diy_dialog_item_date)
        var button: Button = view.findViewById(R.id.main_diy_dialog_item_delete)
        var clickLayout: LinearLayout = view.findViewById(R.id.main_diy_dialog_item_layout)
    }


    private var mItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onClickItem(position: Int)
        fun onClickDelete(position: Int)
    }

}