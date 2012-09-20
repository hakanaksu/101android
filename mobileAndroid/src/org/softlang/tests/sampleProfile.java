package org.softlang.tests;

import org.softlang.company.data.Profile;

public class sampleProfile {

	
	public static Profile getSampleProfile(){
		Profile profile = new Profile();
		
		profile.setCompanyName("meganalysis");
		profile.setEmployeeName("Ray");
		profile.setAddress("Redmond");
		
		profile.setMoneyPerHour(11.5);

		profile.setNwLatitude(80);
		profile.setNwLongitude(-179);
		profile.setSeLatitude(-80);
		profile.setSeLongitude(179);
		
		/*
		profile.setNwLatitude(50.36515149446278);
		profile.setNwLongitude(7.555814981460571);
		profile.setSeLatitude(50.36100805035697);
		profile.setSeLongitude(7.560546398162842);
		*/
		
		return profile;
	}
	
}
