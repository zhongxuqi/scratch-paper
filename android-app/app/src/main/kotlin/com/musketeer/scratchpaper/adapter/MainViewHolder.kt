package com.musketeer.scratchpaper.adapter

import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.muskeeter.base.utils.ScreenUtils
import com.musketeer.scratchpaper.MainApplication
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.common.SharePreferenceConfig
import com.musketeer.scratchpaper.paperfile.PaperFileUtils
import com.musketeer.scratchpaper.utils.SharePreferenceUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.util.DisplayMetrics



/**
 * Created by zhongxuqi on 08/07/2017.
 */
class MainViewHolder constructor(itemView: View?, adapter: MainAdapter): RecyclerView.ViewHolder(itemView) {
    val adapter: MainAdapter

    init {
        this.adapter = adapter
    }

    val timeLineTop: View by lazy {
        itemView?.findViewById(R.id.time_line_top) as View
    }

    val timeLineBottom: View by lazy {
        itemView?.findViewById(R.id.time_line_bottom) as View
    }

    val mTimeLineText: TextView by lazy {
        itemView?.findViewById(R.id.tv_time_line) as TextView
    }

    val mPaperContainer: LinearLayout by lazy {
        itemView?.findViewById(R.id.paper_container) as LinearLayout
    }

    fun initLineContainer(): LinearLayout {
        val container = LinearLayout(adapter.context)
        val lp = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.weight = 1F
        lp.bottomMargin = ScreenUtils.dpToPx(adapter.context, 5F).toInt()
        container.layoutParams = lp
        container.orientation = LinearLayout.HORIZONTAL
        return container
    }

    fun initCardLayoutParams(): LinearLayout.LayoutParams {
        val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.weight = 1F
        return lp
    }

    fun getScreenSize(): Int {
        val metrics = adapter.context!!.resources.getDisplayMetrics()
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val z = Math.sqrt(Math.pow(width.toDouble(), 2.toDouble()) + Math.pow(height.toDouble(), 2.toDouble()))
        val f = (z / (metrics.xdpi * metrics.density))
        return f.toInt()
    }

    fun bindData(timeOfDay: Long, fileList: List<File>, isTop: Boolean, isBottom: Boolean) {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"))
        calendar.timeInMillis = timeOfDay
        mTimeLineText.setText("${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH)+1}月${calendar.get(Calendar.DAY_OF_MONTH)}日")
        mPaperContainer.removeAllViews()
        timeLineTop.visibility = if (isTop) View.INVISIBLE else View.VISIBLE
        timeLineBottom.visibility = if (isBottom) View.INVISIBLE else View.VISIBLE

        var rowNum = 3
        if (getScreenSize() > 7) {
            rowNum = 4
        }

        for (i in 0..fileList.size step rowNum) {
            val container = initLineContainer()

            for (j in 0..(rowNum-1)) {
                val cardView = LayoutInflater.from(adapter.context).inflate(R.layout.component_paper_card, null)
                cardView.layoutParams = initCardLayoutParams()
                if (i + j < fileList.size) {
                    val imageView = cardView.findViewById(R.id.paper_content) as ImageView
                    val bitmap = PaperFileUtils.getPaperBitmap(fileList[i + j])
                    imageView.setImageBitmap(bitmap)
                    imageView.setTag(fileList[i + j])
                    imageView.setOnClickListener(object: View.OnClickListener{
                        override fun onClick(v: View?) {
                            adapter.onItemClickListener?.onClick(v)
                        }
                    })
                    imageView.setOnLongClickListener(object: View.OnLongClickListener{
                        override fun onLongClick(v: View?): Boolean {
                            adapter.onItemLongClickListener?.onLongClick(v)
                            return true
                        }
                    })
                }
                container.addView(cardView)
            }

            mPaperContainer.addView(container)
        }
    }
}