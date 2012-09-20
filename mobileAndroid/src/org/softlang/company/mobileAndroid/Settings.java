package org.softlang.company.mobileAndroid;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class Settings extends TabActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    setContentView(R.layout.settings);

	    Resources res = getResources(); // Resource object to get Drawables
	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, GeneralSettings.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("general").setIndicator("General",
	                      res.getDrawable(android.R.drawable.ic_dialog_map))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, StatusSettings.class);
	    spec = tabHost.newTabSpec("status").setIndicator("Status",
	                      res.getDrawable(android.R.drawable.ic_dialog_info))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    intent = new Intent().setClass(this, ProfileSettings.class);
	    spec = tabHost.newTabSpec("profile").setIndicator("Profile",
	                      res.getDrawable(android.R.drawable.ic_dialog_dialer))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    
	    tabHost.setCurrentTab(1);
	
	}
	
}
