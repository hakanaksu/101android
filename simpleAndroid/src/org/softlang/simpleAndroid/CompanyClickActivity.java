package org.softlang.simpleAndroid;

import java.util.LinkedList;

import org.softlang.company.*;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CompanyClickActivity extends ListActivity {

	private Company company;
		
	private String[] deptsString;
	private ArrayAdapter<String> adapter;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.departmentlist);
		
		company = (Company) getIntent().getExtras().get("Company");
		setTitle(company.getName());

		LinkedList<Department> depts = (LinkedList<Department>) company.getDepts();
		
		deptsString = new String[depts.size()];
		for(int i = 0; i < depts.size(); i++){
			deptsString[i] = "[D] " + depts.get(i).getName();
		}
		adapter = new ArrayAdapter<String>(	this,
											android.R.layout.simple_list_item_1,
											deptsString);
		setListAdapter(adapter);		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent(this, DepartmentClickActivity.class);
		intent.putExtra("Department", company.getDepts().get(position));
		intent.putExtra("Company", company);
		startActivity(intent);
	}
	
}
