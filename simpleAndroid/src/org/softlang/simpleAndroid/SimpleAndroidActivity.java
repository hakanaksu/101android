package org.softlang.simpleAndroid;

import org.softlang.simpleAndroid.CompanyClickActivity;
import org.softlang.simpleAndroid.R;
import org.softlang.company.Company;
import org.softlang.tests.sampleCompany;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SimpleAndroidActivity extends Activity {


	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    private Company loadedCompany;

    public void onButtonClick(View v) {
		switch(v.getId()){
			
		case R.id.bt_Company:
			if (loadedCompany != null){
				Intent intent = new Intent(this,CompanyClickActivity.class);
				intent.putExtra("Company",loadedCompany);
				startActivity(intent);
			}else {
				Toast.makeText(this, R.string.company_not_loaded, Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.bt_loadtestCompany:
			loadedCompany = new sampleCompany().getCompany();
			((Button)findViewById(R.id.bt_Company)).setText(loadedCompany.getName());
			break;
		}
	}
    
}