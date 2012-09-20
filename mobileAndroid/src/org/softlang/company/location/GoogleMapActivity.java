package org.softlang.company.location;

import org.softlang.company.mobileAndroid.R;
import org.softlang.company.data.Profile;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class GoogleMapActivity extends MapActivity {

	private MapView mView;
	private MapController mController;
	private LocationManager lManager;
	private ConnectivityManager cManager;
	private boolean gps_enabled;
	private boolean network_enabled;

	private Profile profile;
	private MyLocationOverlay myLocationOverlay;
	private CompanyOverlay compOverlay;


	@Override
	protected void onCreate(Bundle icicle) {

		super.onCreate(icicle);
		setContentView(R.layout.google_maps);
		
		lManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cManager.getActiveNetworkInfo();
		gps_enabled=lManager.isProviderEnabled(LocationManager.GPS_PROVIDER); 
		network_enabled=lManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if(!gps_enabled && !(network_enabled && (netInfo!=null) && netInfo.isConnected())){
			Toast.makeText(this, "Please turn GPS or Network-Location on", Toast.LENGTH_SHORT).show();
			finish();
		}
		if (!((netInfo!=null) && netInfo.isConnected())){
			Toast.makeText(this, "Without Internet the Maps will not load", Toast.LENGTH_SHORT).show();
		}
		
		mView = (MapView) findViewById(R.id.mv_google_map);
		mView.setSatellite(true);
		mView.setBuiltInZoomControls(true);
		
		mController = mView.getController();
		mController.setZoom(15);
		
		Bundle bundle = getIntent().getExtras();
		profile = (Profile) bundle.get("profile");
		if (profile != null){
			compOverlay = new CompanyOverlay(profile.getNwLatitude(),
					profile.getNwLongitude(), 
					profile.getSeLatitude(), 
					profile.getSeLongitude());
			mView.getOverlays().add(compOverlay);
		}
		
	    myLocationOverlay = new MyLocationOverlay(this,mView);
	    myLocationOverlay.enableMyLocation();
	    myLocationOverlay.enableCompass();
	    
	    myLocationOverlay.runOnFirstFix(new Runnable() {
			@Override
			public void run() {
				Location location = myLocationOverlay.getLastFix();
				GeoPoint LastFix = new GeoPoint((int) (location.getLatitude() * 1E6),
												(int) (location.getLongitude() * 1E6));
				mController.animateTo(LastFix);
			}
		});
	    
		mView.getOverlays().add(myLocationOverlay);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		myLocationOverlay.disableMyLocation();
		myLocationOverlay.disableCompass();
	}
	
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
