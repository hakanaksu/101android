package org.softlang.company.mobileAndroid;

import java.io.File;

import org.softlang.company.data.Profile;
import org.softlang.company.data.ProfileXMLParser;
import org.softlang.company.services.WorkService;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

public class GeneralSettings extends Activity {

	private ToggleButton workMode;
	private LocationManager lManager;
	private ConnectivityManager cManager;
	private boolean gps_enabled;
	private boolean network_enabled;
	
	private Profile profile;
	private File path;
	
	//Save the Employee choice
	private SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.general);
		workMode = (ToggleButton) findViewById(R.id.tbt_work_general);
		path = getDir("profile", MODE_PRIVATE);
		settings = getSharedPreferences(getPackageName()+"_preferences", MODE_PRIVATE);	
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (isMyServiceRunning())
			workMode.setChecked(true);
		else
			workMode.setChecked(false);
		if (path.list().length!=0) {
			int pos = settings.getInt("profile", 0);
			profile = ProfileXMLParser.importProfileFromXML(path.list()[pos], path);
		}
	}
	
	@Override
	protected void onPause() {
		profile = null;
		super.onPause();
	}
	
	public void onButtonClick(View v) {		
		switch (v.getId()) {
		case R.id.tbt_work_general:
			if (workMode.isChecked() &&  !isMyServiceRunning()) {
				if (profile == null){
					Toast.makeText(this, "Load a Profile", Toast.LENGTH_SHORT).show();
		        	workMode.setChecked(false);
				} else {
					lManager = (LocationManager) getSystemService(LOCATION_SERVICE);
					cManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
					NetworkInfo netInfo = cManager.getActiveNetworkInfo();
			        gps_enabled=lManager.isProviderEnabled(LocationManager.GPS_PROVIDER); 
			        network_enabled=lManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			        if(!gps_enabled && !(network_enabled && netInfo!=null && netInfo.isConnected())){
			        	Toast.makeText(this, "Please turn GPS or Network-Location on", Toast.LENGTH_SHORT).show();
			        	workMode.setChecked(false);
			        } else {
			        	Intent i = new Intent(this, WorkService.class);
			        	i.putExtra("profile", profile);
			        	startService(i);
			        }
				}
			} else {
				if (isMyServiceRunning())
					stopService(new Intent(this, WorkService.class));
			}
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
