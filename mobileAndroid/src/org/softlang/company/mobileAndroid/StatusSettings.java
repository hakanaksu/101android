package org.softlang.company.mobileAndroid;

import java.io.File;

import org.softlang.company.data.Profile;
import org.softlang.company.data.ProfileXMLParser;
import org.softlang.company.location.GoogleMapActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class StatusSettings extends Activity{
		

	private LocationManager lManager;
	private ConnectivityManager cManager;
	private boolean gps_enabled = false;
	private boolean network_enabled = false;

	private File path;
	private Profile profile;
	
	//Connection
	private TextView internetStatus;
	private TextView networkStatus;
	private TextView gpsStatus;
	private TextView workStatus;
	
	// Employee
	private TextView employeeName;
	private TextView address;
	private TextView currentSalary;
	private TextView totalHour;
	
	//Save the Employee choice
	private SharedPreferences settings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.status);
		
		internetStatus = (TextView)findViewById(R.id.tv_internet_status); 
		networkStatus = (TextView) findViewById(R.id.tv_nlocation_status);
		gpsStatus = (TextView)findViewById(R.id.tv_gps_status);
		workStatus = (TextView)findViewById(R.id.tv_work_status);

		employeeName = (TextView) findViewById(R.id.tv_name_status);
		address = (TextView) findViewById(R.id.tv_address_status);
		currentSalary = (TextView) findViewById(R.id.tv_current_salary_status);
		totalHour = (TextView) findViewById(R.id.tv_total_workhour_status);

		path = getDir("profile",MODE_PRIVATE);
		
		settings = getSharedPreferences(getPackageName()+"_preferences", MODE_PRIVATE);	
	}

	@Override
	protected void onResume() {
		super.onResume();
		onCreate(null);
		
		lManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cManager.getActiveNetworkInfo();
        gps_enabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER); 
        network_enabled = lManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        
        //Internet Connection
        if (netInfo != null && netInfo.isConnected()){
        	internetStatus.setText(R.string.on);
        	internetStatus.setTextColor(Color.GREEN);        	
        } else {	
        	internetStatus.setText(R.string.off);
        	internetStatus.setTextColor(Color.RED);
        }
        
        //Network Location
        if (network_enabled){
        	networkStatus.setText(R.string.on);
        	networkStatus.setTextColor(Color.GREEN);
        } else {
        	networkStatus.setText(R.string.off);
        	networkStatus.setTextColor(Color.RED);
        }
        
        //GPS Enabled
        if (gps_enabled){
        	gpsStatus.setText(R.string.on);
        	gpsStatus.setTextColor(Color.GREEN);
        } else {
        	gpsStatus.setText(R.string.off);
        	gpsStatus.setTextColor(Color.RED);
        }
        
        //Work Mode on
		if (isMyServiceRunning()) {
			//Set Work Mode ON
			workStatus.setText(R.string.on);
			workStatus.setTextColor(Color.GREEN);
		} else {
			//Set Work Mode OFF
			workStatus.setText(R.string.off);
			workStatus.setTextColor(Color.RED);
		}
		
		//Wenn ein Gehalt vorhanden sein sollte, dann wird dieser geladen
		if (path.list().length!=0) {
			int pos = settings.getInt("profile", 0);
			profile = ProfileXMLParser.importProfileFromXML(path.list()[pos], path);
			employeeName.setText(profile.getEmployeeName());
			address.setText(profile.getAddress());
			currentSalary.setText(((double)(int)(profile.getCurrentSalary()*100))/100+"");
			totalHour.setText((int) profile.getTotalTime()/60 + ":" + (int) profile.getTotalTime() % 60 );
		}
	}
	
	@Override
	protected void onPause() {
		profile = null;
		super.onPause();
	}
	
	public void onButtonClick(View v){
		switch (v.getId()) {
		case R.id.bt_refresh_status:
			onResume();
			break;
		case R.id.ibt_map_status:
			Intent intent = new Intent(this, GoogleMapActivity.class);
			intent.putExtra("profile", profile);
			startActivity(intent);
			break;

		default:
			break;
		}
	}
	
	private boolean isMyServiceRunning() {
	    ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("org.softlang.company.services.WorkService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}	
}
