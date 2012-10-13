package org.softlang.company.mobileAndroid;

import java.io.File;

import org.softlang.company.data.Profile;
import org.softlang.company.data.ProfileXMLParser;
import org.softlang.tests.sampleProfile;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileSettings extends Activity {

	//Employee Data
	private TextView companyName;
	private TextView employeeName;
	private TextView address;
	
	//Work Data
	private TextView moneyPerHour;
	
	//Geo-Coordinates Data
	private TextView nwLong;
	private TextView nwLat;
	private TextView seLong;
	private TextView seLat;
	
	//The Profile Object
	private Profile profile;
	
	//Spinner to list the Employees
	private Spinner empSpinner;
	private ArrayAdapter<String> adapter;
	
	//Internal Storage to save the Employee Data's
	private File path;

	//External Starage for Import (Browse)
	private File externalPath;
	private File selectedPath;

	//Save the Employee choice
	private SharedPreferences settings;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile);
		path = getDir("profile", MODE_PRIVATE);
				
		companyName = (TextView) findViewById(R.id.tv_company_profile);
		employeeName = (TextView) findViewById(R.id.tv_name_profile);
		address = (TextView) findViewById(R.id.tv_address_profile);
		moneyPerHour = (TextView) findViewById(R.id.tv_money_per_hour_profile);
		nwLong = (TextView) findViewById(R.id.tv_longitude_nw_profile);
		nwLat = (TextView) findViewById(R.id.tv_latitude_nw_profile);
		seLong = (TextView) findViewById(R.id.tv_longitude_se_profile);
		seLat = (TextView) findViewById(R.id.tv_latitude_se_profile);
		
		empSpinner = (Spinner) findViewById(R.id.sp_employeelist_profile);
				
		settings = getSharedPreferences(getPackageName()+"_preferences", MODE_PRIVATE);	
		
		if(path.list().length!=0){
			configureSpinner();
		}
	}

	public void onButtonClick(View v) {

		switch (v.getId()) {
		case R.id.bt_import_employee:
			externalPath = Environment.getExternalStorageDirectory();
			selectedPath = Environment.getExternalStorageDirectory();
			importEmployee();
			break;

		case R.id.bt_export_employee:
			if (profile == null){
				Toast.makeText(this, getText(R.string.noprofiletoexport), Toast.LENGTH_SHORT).show();
			} else {
				File exportPath = new File(Environment.getExternalStorageDirectory().getPath()+"/101Company");
				ProfileXMLParser.exportXMLFromProfile(profile, profile.getEmployeeName()  + ".xml", exportPath);
				Toast.makeText(ProfileSettings.this, getText(R.string.exportto) + exportPath.getPath()  , Toast.LENGTH_SHORT).show();
			}
			break;
		
		case R.id.bt_export_sample:
			Profile prof = sampleProfile.getSampleProfile();
			File exportPath = new File(Environment.getExternalStorageDirectory().getPath()+"/101Company");
			ProfileXMLParser.exportXMLFromProfile(prof, prof.getEmployeeName()  + "_sample.xml", exportPath);
			Toast.makeText(ProfileSettings.this, getText(R.string.exportto) + exportPath.getPath()  , Toast.LENGTH_SHORT).show();
			break;
			
		case R.id.bt_delete_all:
			if (isMyServiceRunning()) {
				Toast.makeText(this, getText(R.string.pleasefinishatfirsttheworkmode), Toast.LENGTH_SHORT).show();
			} else {
				for(int i = path.listFiles().length-1; i >= 0; i--)
					path.listFiles()[i].delete();
				profile = null;
				onCreate(null);
				settings.edit().putInt("profile", 0).commit();
				Toast.makeText(this, R.string.successful, Toast.LENGTH_SHORT).show();
			}
			break;
			
		default:
			break;
		}
	}
	
	private void importEmployee(){
		Builder builder = new Builder(this);
		
		builder.setTitle("Select a Profile")
				.setItems(selectedPath.list(), new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						selectedPath = new File(selectedPath.getPath() + "/" + selectedPath.list()[which]);
						if (selectedPath.isDirectory()){
							externalPath = new File(externalPath.getPath() + "/" + externalPath.list()[which]);
							importEmployee();
						} else {
							
							Profile tmpProfile = ProfileXMLParser.importProfileFromXML(externalPath.list()[which], externalPath);
							if (tmpProfile == null) {
								Toast.makeText(ProfileSettings.this, getText(R.string.notaprofile), Toast.LENGTH_SHORT).show();
							} else {
								if (isMyServiceRunning() && profile.getEmployeeName().equals(tmpProfile.getEmployeeName())){
									Toast.makeText(ProfileSettings.this, getText(R.string.pleasefinishatfirsttheworkmode) , Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(ProfileSettings.this, getText(R.string.import_) + ": " + externalPath.getPath() + "/" + externalPath.list()[which] , Toast.LENGTH_SHORT).show();
									ProfileXMLParser.exportXMLFromProfile(tmpProfile, tmpProfile.getEmployeeName(), path);
									configureSpinner();									
								}
							}
						}
					}
				})
				.setNegativeButton("Cancel",null)
				.show();
	}
	
	private void configureSpinner() {
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, path.list());
		empSpinner.setAdapter(adapter);
		final int profileChoice = settings.getInt("profile", 0);
		empSpinner.setSelection(profileChoice);
		empSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

				if (!companyName.getText().toString().equals(getText(R.string.no_data)) && isMyServiceRunning()){
					Toast.makeText(ProfileSettings.this, getText(R.string.pleasefinishatfirsttheworkmode), Toast.LENGTH_SHORT).show();
					empSpinner.setSelection(profileChoice);
				} else {
					profile = ProfileXMLParser.importProfileFromXML(path.list()[pos], path);
					settings.edit().putInt("profile", pos).commit();
					setProfile();					
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
	}
	
	public void setProfile(){
		companyName.setText(profile.getCompanyName());
		employeeName.setText(profile.getEmployeeName());
		address.setText(profile.getAddress());
		moneyPerHour.setText(String.valueOf(profile.getMoneyPerHour()));
		nwLong.setText(String.valueOf(profile.getNwLongitude()));
		nwLat.setText(String.valueOf(profile.getNwLatitude()));
		seLong.setText(String.valueOf(profile.getSeLongitude()));
		seLat.setText(String.valueOf(profile.getSeLatitude()));
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
