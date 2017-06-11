package com.musketeer.scratchpaper.activity

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.RemoteViews

import com.musketeer.baselibrary.util.SharePreferenceUtils
import com.musketeer.scratchpaper.R
import com.musketeer.scratchpaper.common.SharePreferenceConfig
import com.musketeer.scratchpaper.paperfile.PaperFileUtils

class PaperWidgetProvider : AppWidgetProvider() {

    private var mAppWidgetManager: AppWidgetManager? = null
    private var mAppWidgetIds: IntArray? = null
    private var mWidgetViews: RemoteViews? = null

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // TODO Auto-generated method stub
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val paper_name = SharePreferenceUtils.getString(context, SharePreferenceConfig.WIDGET_PAPER_NAME, "")
        if (paper_name.isNotEmpty()) {
            val bitmap = PaperFileUtils.getPaperThumbNail(paper_name)
            mWidgetViews = RemoteViews(context.packageName, R.layout.paper_widget)
            mWidgetViews!!.setImageViewBitmap(R.id.widget_paper_content, bitmap)
            appWidgetManager.updateAppWidget(appWidgetIds, mWidgetViews)
        }

        val intent = Intent(context, BrowsePaperActivity::class.java)
        val bundle = Bundle()
        bundle.putString("paper_name", paper_name)
        intent.putExtras(bundle)
        val Pintent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mWidgetViews = RemoteViews(context.packageName, R.layout.paper_widget)
        mWidgetViews!!.setOnClickPendingIntent(R.id.widget_paper_content, Pintent)
        appWidgetManager.updateAppWidget(appWidgetIds, mWidgetViews)
    }

    override fun onReceive(context: Context, intent: Intent?) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent)
        if (intent != null && intent.hasExtra("widget_name") && intent.getStringExtra("widget_name") == TAG) {
            val bitmap = PaperFileUtils.getPaperThumbNail(intent.getStringExtra("paper_name"))
            mWidgetViews = RemoteViews(context.packageName, R.layout.paper_widget)
            mWidgetViews!!.setImageViewBitmap(R.id.widget_paper_content, bitmap)
            mAppWidgetManager = AppWidgetManager.getInstance(context)
            mAppWidgetIds = mAppWidgetManager!!.getAppWidgetIds(ComponentName(context, PaperWidgetProvider::class.java))
            mAppWidgetManager!!.updateAppWidget(mAppWidgetIds, mWidgetViews)
        }
    }

    companion object {
        val TAG = "PaperWidgetProvider"
    }

}
