package org.softlang.simpleAndroid;

import org.softlang.company.*;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;


public class EmployeeClickActivity extends Activity{	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.employeedata);
		
		Employee employee = (Employee)getIntent().getExtras().get("Employee");
		setTitle(employee.getName());

		TextView tv_name = ((TextView)findViewById(R.id.tv_name));
		TextView tv_address = ((TextView)findViewById(R.id.tv_address));
		TextView tv_salary = ((TextView)findViewById(R.id.tv_salary));
		
		tv_name.setText(employee.getName());
		tv_address.setText(employee.getAddress());
		tv_salary.setText(employee.getSalary()+"");	
	}
}
