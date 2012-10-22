package org.softlang.company.services;

import java.io.File;

import java.util.Date;

import org.softlang.company.data.Profile;
import org.softlang.company.data.ProfileXMLParser;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

public class WorkService extends Service {
	
	private WorkServiceBinder mBinder = new WorkServiceBinder();
	private Profile profile;

	private File path;
	
	private LocationManager lManager;
	private ConnectivityManager cManager;
	private boolean gps_enabled = false;
	private boolean network_enabled = false;
	private NetworkInfo netInfo;
	
	//Update Time in Millis
	//1000 = 1sec 60000 = 1min   600 000 = 10min 900 000 = 15min		
	private final int UPDATE_TIME = 15*60*1000;
		
	public class WorkServiceBinder extends Binder{
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	private long lastUpdateTime;
	private Date date;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		path = getDir("profile", MODE_PRIVATE);
		
		lManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);		
		
		SharedPreferences settings = getSharedPreferences(getPackageName()+"_preferences", MODE_PRIVATE);	
		int profileChoice = settings.getInt("profile", 0);
		profile = ProfileXMLParser.importProfileFromXML(path.list()[profileChoice], path);
		
		date = new Date();
		lastUpdateTime = date.getTime();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		/*
		try{
			Bundle bundle = intent.getExtras();
			profile = (Profile) bundle.get("profile");
		} catch (NullPointerException e) {

		}
		*/

		//Proof Connection
		netInfo = cManager.getActiveNetworkInfo();
        gps_enabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER); 
        network_enabled = lManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if(!gps_enabled && !((netInfo != null) && netInfo.isConnected()) ){
			Toast.makeText(getApplicationContext(), "No Connection to get the location", Toast.LENGTH_SHORT).show();
			stopSelf();
		} else {
			//Proof Location in Company
			if(gps_enabled) {
				lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_TIME, 0, updateListenerForGPS);
			}
			else
				if(network_enabled)
					lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_TIME, 0, updateListenerForNetwork);			
		}
		
		return super.onStartCommand(intent, flags, startId);
	}
	

	
	@Override
	public void onDestroy() {
		super.onDestroy();		
        try {
        	lManager.removeUpdates(updateListenerForGPS);
        	lManager.removeUpdates(updateListenerForNetwork);			
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
		}
	}
	
	public void startProofing(){
		
		
	}
	
	
	
	private double actualLatitude = 0.0;
	private double actualLongitude = 0.0;
	
	private LocationListener updateListenerForGPS = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {			
		}
		
		@Override
		public void onProviderEnabled(String provider) {	
		}

		@Override
		public void onProviderDisabled(String provider) {
			NetworkInfo netInfo = cManager.getActiveNetworkInfo();
	        network_enabled = lManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (netInfo != null && netInfo.isConnected() && network_enabled){
				lManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_TIME, 0, updateListenerForNetwork);
			} else {
				WorkService.this.stopSelf();
			}
		}
		
		@Override
		public void onLocationChanged(Location location) {
			
			actualLatitude = location.getLatitude();
			actualLongitude = location.getLongitude();
			
			date = new Date();			
			 
			boolean boolLongitude = (profile.getSeLongitude()>actualLongitude) && (profile.getNwLongitude()<actualLongitude);
			boolean boolLatitude = (profile.getSeLatitude()<actualLatitude) && (profile.getNwLatitude()>actualLatitude);
			long actualTime = date.getTime();
			//Proof location in Company
			if (!(boolLongitude && boolLatitude)){
				Toast.makeText(getApplicationContext(), "Not in Company", Toast.LENGTH_SHORT).show();
			} else {
				profile.addWorkedTime((double)(actualTime-lastUpdateTime)/60000);
				ProfileXMLParser.exportXMLFromProfile(profile, profile.getEmployeeName(), path);
				//Serialization.writeProfile(profile, profile.getEmployeeName(), path);
			}
			lastUpdateTime = actualTime;
			
		}
	};
	
	private LocationListener updateListenerForNetwork = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			Toast.makeText(WorkService.this, "Internet with Network Location or GPS have to be on and than start Work Mode again", Toast.LENGTH_SHORT).show();
			lManager.removeUpdates(this);
			try{
				lManager.removeUpdates(updateListenerForGPS);
			} catch (IllegalArgumentException e) {
				// TODO: handle exception
			}
			WorkService.this.stopSelf();
		}
		
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
			actualLatitude = location.getLatitude();
			actualLongitude = location.getLongitude();
			
			date = new Date();
			
			//Proof GPS Provider
	        gps_enabled = lManager.isProviderEnabled(LocationManager.GPS_PROVIDER); 
	        if (gps_enabled) {
	        	lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_TIME, 0, updateListenerForGPS);
	        	lManager.removeUpdates(this);
	        } else {
	        	//Proof Internet Connection
	        	NetworkInfo netInfo = cManager.getActiveNetworkInfo();
	        	if(!(netInfo != null && netInfo.isConnected())){
	        		lManager.removeUpdates(this);
	        		WorkService.this.stopSelf();
	        	} else {
        			boolean boolLongitude = (profile.getSeLongitude()>actualLongitude) && (profile.getNwLongitude()<actualLongitude);
        			boolean boolLatitude = (profile.getSeLatitude()<actualLatitude) && (profile.getNwLatitude()>actualLatitude);
        			long actualTime = date.getTime();
        			//Proof location in Company
        			if (!(boolLongitude && boolLatitude)){
        				Toast.makeText(getApplicationContext(), "Not in Company", Toast.LENGTH_SHORT).show();
        			} else {
        				profile.addWorkedTime((double)(actualTime-lastUpdateTime)/60000);
        				ProfileXMLParser.exportXMLFromProfile(profile, profile.getEmployeeName(), path);
        				//Serialization.writeProfile(profile, profile.getEmployeeName(), path);
        			}
        			lastUpdateTime = actualTime;			
	        	}
	        }
			
		}
	};
	
	
}
