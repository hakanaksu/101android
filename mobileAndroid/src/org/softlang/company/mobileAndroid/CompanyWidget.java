package org.softlang.company.mobileAndroid;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningServiceInfo;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class CompanyWidget extends AppWidgetProvider{

	public static final String ACTION_WORK_CLICK = "ACTION_WORK_CLICK";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
		Intent intent = new Intent(context, Settings.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
		views.setOnClickPendingIntent(R.id.ibt_settings_widget, pendingIntent);
		
		Intent intent2 = new Intent(context, CompanyWidget.class);
		intent2.setAction(ACTION_WORK_CLICK);
		PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0, intent2, 0);
		views.setOnClickPendingIntent(R.id.tv_work_widget, pendingIntent2);
		
		appWidgetManager.updateAppWidget(appWidgetIds, views);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
		//CLICK the TextView WorkMode on the Widget
		if (intent.getAction().equals(ACTION_WORK_CLICK)){
			if (isMyServiceRunning(context)) {
				Toast.makeText(context, "The Service is Running", Toast.LENGTH_SHORT).show();				
			} else {
				Toast.makeText(context, "The Service is not Running", Toast.LENGTH_SHORT).show();								
			}
		}
		
		super.onReceive(context, intent);
	}
	
	private boolean isMyServiceRunning(Context context) {
	    ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("org.softlang.company.services.WorkService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}
