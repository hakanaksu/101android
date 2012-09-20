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
	 * The method create an Array of String. 
	 * The elements are the names of the manager, the departmens and the employees
	 * 
	 * @return String[]
	 * @author Hakan Aksu
	 */
	public String[] makeListOfAll(){
		
		//only the departments
		LinkedList<Department> depts = (LinkedList<Department>)dept.getSubdepts();
		String[] deptsString = new String[depts.size()];
		for(int i = 0; i < depts.size(); i++){
			deptsString[i]= "[D] " + depts.get(i).getName();
		}
		//only the employees
		LinkedList<Employee> empl = (LinkedList<Employee>) dept.getEmployees();
		String[] emplString = new String[empl.size()];
		for(int i = 0; i < empl.size(); i++){
			emplString[i] = "[E] " + empl.get(i).getName();
		}
		//all in one
		String[] all;
		//0 -> false, 1 -> true
		int managerIsNotNull = 0;;
		if (dept.getManager() == null){
			managerIsNotNull = 0;
			all = new String[deptsString.length + emplString.length];
		} else {
			managerIsNotNull = 1;
			all = new String[deptsString.length + emplString.length + 1];
			all[0] = "[M] " + dept.getManager().getName();
		}
		for (int i = managerIsNotNull; i <= (all.length - managerIsNotNull);i++){
			if (i <= deptsString.length){
				all[i] = deptsString[i - managerIsNotNull];
			} else {
				all[i] = emplString[i - deptsString.length - managerIsNotNull];
			}
		}
		return all;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = null;
		
		if (dept.getManager() == null){			
			if (position < dept.getSubdepts().size()){
				Department depts = dept.getSubdepts().get(position);
				intent = new Intent(this, DepartmentClickActivity.class);
				intent.putExtra("Department", depts);
			//A Employee is given to the next activity
			} else {
				Employee emp = dept.getEmployees().get(position-dept.getSubdepts().size()-1);
				intent = new Intent(this, EmployeeClickActivity.class);
				intent.putExtra("Employee", emp);
			}
		} else {			
			if (position == 0){
				Employee manager = dept.getManager();
				intent = new Intent(this, EmployeeClickActivity.class);
				intent.putExtra("Employee", manager);
			} else {
				if (position <= dept.getSubdepts().size()){
					Department depts = dept.getSubdepts().get(position-1);
					intent = new Intent(this, DepartmentClickActivity.class);
					intent.putExtra("Department", depts);
				} else {
					Employee emp = dept.getEmployees().get(position-dept.getSubdepts().size()-1);
					intent = new Intent(this, EmployeeClickActivity.class);
					intent.putExtra("Employee", emp);
				}
				
			}
		}
		startActivity(intent);
	}	
}