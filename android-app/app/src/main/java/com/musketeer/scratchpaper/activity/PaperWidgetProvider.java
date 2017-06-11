package com.musketeer.scratchpaper.activity;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.musketeer.baselibrary.util.SharePreferenceUtils;
import com.musketeer.scratchpaper.R;
import com.musketeer.scratchpaper.common.SharePreferenceConfig;
import com.musketeer.scratchpaper.paperfile.PaperFileUtils;

public class PaperWidgetProvider extends AppWidgetProvider {
	public final static String TAG="PaperWidgetProvider";
	
	private AppWidgetManager mAppWidgetManager;
	private int[] mAppWidgetIds;
	private RemoteViews mWidgetViews;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		String paper_name= SharePreferenceUtils.getString(context, SharePreferenceConfig.WIDGET_PAPER_NAME, "");
		if (paper_name.length()>0) {
			Bitmap bitmap= PaperFileUtils.getPaperThumbNail(paper_name);
			mWidgetViews=new RemoteViews(context.getPackageName(), R.layout.paper_widget);
			mWidgetViews.setImageViewBitmap(R.id.widget_paper_content, bitmap);
			appWidgetManager.updateAppWidget(appWidgetIds, mWidgetViews);
		}
		
		Intent intent=new Intent(context,BrowsePaperActivity.class);
		Bundle bundle=new Bundle();
		bundle.putString("paper_name",paper_name);
		intent.putExtras(bundle);
		PendingIntent Pintent= PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		mWidgetViews= new RemoteViews(context.getPackageName(), R.layout.paper_widget);
		mWidgetViews.setOnClickPendingIntent(R.id.widget_paper_content, Pintent);
		appWidgetManager.updateAppWidget(appWidgetIds, mWidgetViews);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		if (intent!=null&&intent.hasExtra("widget_name")&&intent.getStringExtra("widget_name").equals(TAG)) {
			Bitmap bitmap= PaperFileUtils.getPaperThumbNail(intent.getStringExtra("paper_name"));
			mWidgetViews=new RemoteViews(context.getPackageName(), R.layout.paper_widget);
			mWidgetViews.setImageViewBitmap(R.id.widget_paper_content, bitmap);
			mAppWidgetManager=AppWidgetManager.getInstance(context);
			mAppWidgetIds=mAppWidgetManager.getAppWidgetIds(new ComponentName(context, PaperWidgetProvider.class));
			mAppWidgetManager.updateAppWidget(mAppWidgetIds, mWidgetViews);
		}
	}

}
