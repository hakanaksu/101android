package org.softlang.simpleAndroid;

import java.util.LinkedList;

import org.softlang.company.Department;
import org.softlang.company.Employee;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DepartmentClickActivity extends ListActivity{

	Department dept;
	
	private String[] deptsString;
	private ArrayAdapter<String> adapter;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.departmentlist);
		dept = (Department) getIntent().getExtras().get("Department");
		setTitle(dept.getName());
		String[] all = makeListOfAll(); 
		adapter = new ArrayAdapter<String>(this,
										   android.R.layout.simple_list_item_1,
										   all);
		setListAdapter(adapter);
	}

	/*
	 * The Method create an Array of String. 
	 * The Elements are the names of the Departmens, the manager and the Employees
	 * 
	 * @return String[]
	 * @author Hakan Aksu
	 */
	public String[] makeListOfAll(){
		LinkedList<Department> depts = (LinkedList<Department>)dept.getSubdepts();
		deptsString = new String[depts.size()];
		for(int i = 0;i<depts.size(); i++){
			deptsString[i]= depts.get(i).getName();
		}
		LinkedList<Employee> empl = (LinkedList<Employee>) dept.getEmployees();
		String[] emplString = new String[empl.size()];
		for(int i = 0; i < empl.size(); i++){
			emplString[i]= empl.get(i).getName();
		}
		String[] all = new String[deptsString.length + emplString.length + 1];
		for (int i = 0; i<all.length-1;i++){
			if (i < deptsString.length){
				all[i] = deptsString[i];
			} else {
				all[i+1] = emplString[i-deptsString.length];
			}
		}
		//Der Manager wird als Trennung fŸr Departments und Employees verwendet
		all[deptsString.length] = dept.getManager().getName() + " (Manager)";
		return all;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = null;
		//The Sub-Departments are given to the next activity
		if (position < dept.getSubdepts().size()){
			Department depts = dept.getSubdepts().get(position);
			intent = new Intent(this, DepartmentClickActivity.class);
			intent.putExtra("Department", depts);
		//A Employee is given to the next activity
		} else {
			//The Manager
			if (position == dept.getSubdepts().size()){
				Employee emp = dept.getManager();
				intent = new Intent(this, EmployeeClickActivity.class);
				intent.putExtra("Employee", emp);
				//The normal Employee
			} else {
				Employee emp = dept.getEmployees().get(position-dept.getSubdepts().size()-1);
				intent = new Intent(this, EmployeeClickActivity.class);
				intent.putExtra("Employee", emp);
			}
		}
		startActivity(intent);
	}	
}