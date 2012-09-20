package org.softlang.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import org.softlang.company.Company;
import org.softlang.company.Department;
import org.softlang.company.Employee;
import org.softlang.features.Options;
import org.softlang.features.Serialization;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/*
 * @author Hakan Aksu
 */
public class DepartmentClickActivity extends ListActivity implements OnItemLongClickListener{

	Department department;
	Company company;
	
	private ArrayAdapter<String> adapter;
	
	private ArrayList<Integer> positionOfDepartment;
	private ArrayList<Integer> tempPositionOfDepartment;
	
	private File path;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.departmentlist);
		department = (Department) getIntent().getExtras().get("Department");
		company = (Company) getIntent().getExtras().get("Company");
		positionOfDepartment =  (ArrayList<Integer>) getIntent().getExtras().get("positionofdept");
		
		setTitle(department.getName());

		path = getDir("company",MODE_PRIVATE);
		
		String[] all = makeListOfAll(); 
		adapter = new ArrayAdapter<String>(
											this,
											android.R.layout.simple_list_item_1,
											all);
		setListAdapter(adapter);
		getListView().setOnItemLongClickListener(this);
		
	}
	
	@Override
	protected void onResume() {
		
		if (company != null){
			company = Serialization.readCompany(company.getName(), path);
		}
		
		department = company.getDepts().get(positionOfDepartment.get(0));
		
		for (int i = 1; i < positionOfDepartment.size(); i++) {
			department = department.getSubdepts().get(positionOfDepartment.get(i));
		}
		
		String[] all = makeListOfAll();
		adapter = new ArrayAdapter<String>(	getBaseContext(),
											android.R.layout.simple_list_item_1,
											all);
		setListAdapter(adapter);
		
		super.onResume();
	}
	
	/*
	 * The method creates an Array of String. 
	 * The elements are the names of the manager, the departments and the employees
	 * 
	 * @return String[]
	 * @author Hakan Aksu
	 */
	public String[] makeListOfAll(){
		
		//only the departments
		LinkedList<Department> depts = (LinkedList<Department>)department.getSubdepts();
		String[] deptsString = new String[depts.size()];
		for(int i = 0; i < depts.size(); i++){
			deptsString[i] = "[D] " + depts.get(i).getName();
		}
		//only the employees
		LinkedList<Employee> empl = (LinkedList<Employee>) department.getEmployees();
		String[] emplString = new String[empl.size()];
		for(int i = 0; i < empl.size(); i++){
			emplString[i] = "[E] " + empl.get(i).getName();
		}
		//all in one
		String[] all;
		if (department.getManager() == null){
			all = new String[deptsString.length + emplString.length];
			for (int i = 0; i < all.length;i++){
				if (i < deptsString.length){
					all[i] = deptsString[i];
				} else {
					all[i] = emplString[i - deptsString.length];
				}
			}
		} else {
			all = new String[deptsString.length + emplString.length + 1];
			all[0] = "[M] " + department.getManager().getName();
			for (int i = 1; i <= (all.length - 1);i++){
				if (i <= deptsString.length){
					all[i] = deptsString[i - 1];
				} else {
					all[i] = emplString[i - deptsString.length - 1];
				}
			}
		}

		return all;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent intent = null;
		
		if (department.getManager() == null){			
			if (position < department.getSubdepts().size()){
				Department depts = department.getSubdepts().get(position);
				intent = new Intent(this, DepartmentClickActivity.class);
				intent.putExtra("Department", depts);
				intent.putExtra("Company", company);
				tempPositionOfDepartment = (ArrayList<Integer>) positionOfDepartment.clone();
				tempPositionOfDepartment.add(position);
				intent.putExtra("positionofdept", tempPositionOfDepartment);
			} else {
				Employee emp = department.getEmployees().get(position-department.getSubdepts().size());
				intent = new Intent(this, EmployeeClickActivity.class);
				intent.putExtra("Employee", emp);
				intent.putExtra("Company", company);
				intent.putExtra("positionofdept", positionOfDepartment);
				intent.putExtra("Manager", false);
				intent.putExtra("position", (position-department.getSubdepts().size()) );
			}
		} else {			
			if (position == 0){
				Employee manager = department.getManager();
				intent = new Intent(this, EmployeeClickActivity.class);
				intent.putExtra("Employee", manager);
				intent.putExtra("Company", company);
				intent.putExtra("positionofdept", positionOfDepartment);
				intent.putExtra("Manager", true);
			} else {
				if (position <= department.getSubdepts().size()){
					Department depts = department.getSubdepts().get(position-1);
					intent = new Intent(this, DepartmentClickActivity.class);
					intent.putExtra("Department", depts);
					intent.putExtra("Company", company);
					tempPositionOfDepartment = (ArrayList<Integer>) positionOfDepartment.clone();
					tempPositionOfDepartment.add(position-1);
					intent.putExtra("positionofdept", tempPositionOfDepartment);
				} else {
					Employee emp = department.getEmployees().get(position-department.getSubdepts().size()-1);
					intent = new Intent(this, EmployeeClickActivity.class);
					intent.putExtra("Employee", emp);
					intent.putExtra("Company", company);
					intent.putExtra("positionofdept", positionOfDepartment);
					intent.putExtra("Manager", false);
					intent.putExtra("position", (position-department.getSubdepts().size())-1);
				}	
			}
		}
		startActivity(intent);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		
		
		Builder builder = new Builder(this);
		builder.setTitle(R.string.option);
		
		//Manager
		if (position == 0 && department.getManager() != null){
			String[] optionlist = {getText(R.string.copy).toString(),
					 			   getText(R.string.delete).toString()};

			builder.setItems(optionlist, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					final Options options = new Options();
					switch (which) {
					
					//copy
					case 0:
						if(options.copy(department.getManager(), getDir("temp", MODE_PRIVATE)))
							Toast.makeText(getBaseContext(), R.string.successful, Toast.LENGTH_SHORT).show();
						else Toast.makeText(DepartmentClickActivity.this, R.string.processfailed, Toast.LENGTH_SHORT).show();
						break;
						
						//delete
					case 1:
						Builder build = new Builder(DepartmentClickActivity.this);
						build.setTitle(R.string.areyousure)
						.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
							
							@SuppressWarnings("unchecked")
							@Override
							public void onClick(DialogInterface dialog, int which) {
								tempPositionOfDepartment = (ArrayList<Integer>) positionOfDepartment.clone();
								tempPositionOfDepartment.add(position);
								if (options.delete(DepartmentClickActivity.this, company, tempPositionOfDepartment, path))
									Toast.makeText(getBaseContext(), R.string.successful, Toast.LENGTH_SHORT).show();
								
								onResume();
								/*
								String[] all = makeListOfAll(); 
								
								//Adapter wird erstellt
								adapter = new ArrayAdapter<String>( DepartmentClickActivity.this,
										android.R.layout.simple_list_item_1,
										all);
								setListAdapter(adapter);
								*/
							}
						})
						.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						})
						.show();
						break;
					}
					
				}
			}).show();
			
		} else {
			//Department is clicked
			if (position < department.getSubdepts().size() || (department.getManager() != null && position == department.getSubdepts().size()) ){
				
				
				String[] optionlist =	{getText(R.string.copy).toString(),
						 				 getText(R.string.paste).toString(),
						 				 getText(R.string.rename).toString(),
						 				 getText(R.string.delete).toString()};
				
				builder.setItems(optionlist, new DialogInterface.OnClickListener() {
						
					@SuppressWarnings("unchecked")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						final Options options = new Options();

						
						switch (which) {
							
						//copy
						case 0:
							Department departmentToCopy;
							
							if (department.getManager() == null)
								departmentToCopy = department.getSubdepts().get(position);
							else
								departmentToCopy = department.getSubdepts().get(position-1);
							
							if(options.copy(departmentToCopy, getDir("temp", MODE_PRIVATE)))
								Toast.makeText(getBaseContext(), R.string.successful, Toast.LENGTH_SHORT).show();
							else Toast.makeText(DepartmentClickActivity.this, R.string.processfailed, Toast.LENGTH_SHORT).show();
							break;
							
						//paste
						case 1:
							int positionToPaste = 0;
							if (department.getManager() == null)
								positionToPaste = position;
							else
								positionToPaste = position-1;
							
							tempPositionOfDepartment = (ArrayList<Integer>) positionOfDepartment.clone();
							tempPositionOfDepartment.add(positionToPaste);
							if (options.paste(getDir("temp", MODE_PRIVATE), company, tempPositionOfDepartment, path))
								Toast.makeText(DepartmentClickActivity.this, R.string.successful, Toast.LENGTH_SHORT).show();
							else Toast.makeText(DepartmentClickActivity.this, R.string.processfailed, Toast.LENGTH_SHORT).show();
							break;
						
						//rename
						case 2:
							tempPositionOfDepartment = (ArrayList<Integer>) positionOfDepartment.clone();
							
							if (department.getManager() == null)
								tempPositionOfDepartment.add(position);
							else
								tempPositionOfDepartment.add(position-1);
								
							
							final Dialog dialog1 = new Dialog(DepartmentClickActivity.this);
							dialog1.setTitle(R.string.name);
							dialog1.setContentView(R.layout.newelement);
							((Button)dialog1.findViewById(R.id.bt_ok_newelement)).setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									String newname = ((EditText)dialog1.findViewById(R.id.et_newname)).getText().toString();
									if (options.rename(company, tempPositionOfDepartment, newname, path))
										Toast.makeText(DepartmentClickActivity.this, R.string.successful, Toast.LENGTH_SHORT).show();
									else
										Toast.makeText(DepartmentClickActivity.this, R.string.processfailed, Toast.LENGTH_SHORT).show();
									dialog1.dismiss();
									
									onResume();
									String[] all = makeListOfAll(); 
									
									//Adapter wird erstellt
									adapter = new ArrayAdapter<String>( DepartmentClickActivity.this,
																		android.R.layout.simple_list_item_1,
																		all);
									setListAdapter(adapter);
								}
							});
							((Button)dialog1.findViewById(R.id.bt_Cancel_newelement)).setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									dialog1.dismiss();
								}
							});
							dialog1.show();
							break;
							
						//delete
						case 3:
							Builder build = new Builder(DepartmentClickActivity.this);
							build.setTitle(R.string.areyousure)
								 .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
								
									@Override
									public void onClick(DialogInterface dialog, int which) {
										tempPositionOfDepartment = (ArrayList<Integer>) positionOfDepartment.clone();
										tempPositionOfDepartment.add(position);
										if (options.delete(DepartmentClickActivity.this, company, tempPositionOfDepartment, path))
											Toast.makeText(getBaseContext(), R.string.successful, Toast.LENGTH_SHORT).show();
										
										onResume();
										String[] all = makeListOfAll(); 
										
										//Adapter wird erstellt
										adapter = new ArrayAdapter<String>( DepartmentClickActivity.this,
																			android.R.layout.simple_list_item_1,
																			all);
										setListAdapter(adapter);
									}
								 })
								 .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								 })
								 .show();
							break;

						}
						
					}
				}).show();
				
				//Normale Employees
			} else {
				
				String[] optionlist =	{getText(R.string.copy).toString(),
										 getText(R.string.delete).toString(),
										 getText(R.string.setasmanager).toString()};
				
				builder.setItems(optionlist, new DialogInterface.OnClickListener() {
						
					@SuppressWarnings("unchecked")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						final Options options = new Options();
						
						switch (which) {
							
						//copy
						case 0:							
							if(options.copy(department.getEmployees().get(position-department.getSubdepts().size()-1), getDir("temp", MODE_PRIVATE)))
								Toast.makeText(getBaseContext(), R.string.successful, Toast.LENGTH_SHORT).show();
							else Toast.makeText(DepartmentClickActivity.this, R.string.processfailed, Toast.LENGTH_SHORT).show();
							
							break;
							
						//delete
						case 1:
							Builder build = new Builder(DepartmentClickActivity.this);
							build.setTitle(R.string.areyousure)
								 .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
								
									@Override
									public void onClick(DialogInterface dialog, int which) {
										tempPositionOfDepartment = (ArrayList<Integer>) positionOfDepartment.clone();
										tempPositionOfDepartment.add(position);
										if (options.delete(DepartmentClickActivity.this, company, tempPositionOfDepartment, path))
											Toast.makeText(getBaseContext(), R.string.successful, Toast.LENGTH_SHORT).show();

										onResume();
										String[] all = makeListOfAll(); 
										
										//Adapter wird erstellt
										adapter = new ArrayAdapter<String>( DepartmentClickActivity.this,
																			android.R.layout.simple_list_item_1,
																			all);
										setListAdapter(adapter);
									}
								 })
								 .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								 })
								 .show();
							break;
							
						//Set as Manager
						case 2:
							tempPositionOfDepartment = (ArrayList<Integer>) positionOfDepartment.clone();
							tempPositionOfDepartment.add(position);
							options.setAsManager(DepartmentClickActivity.this, company, tempPositionOfDepartment,department.getManager(), path);
							
							onResume();
							String[] all = makeListOfAll(); 
							
							adapter = new ArrayAdapter<String>( DepartmentClickActivity.this,
																android.R.layout.simple_list_item_1,
																all);
							setListAdapter(adapter);
							break;
	
						}
						
					}
				}).show();
			}
		}
		
		return true;
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menue, menu);
		menu.getItem(1).setTitle(R.string.addEmployee);
		menu.getItem(0).setTitle(R.string.addDepartment);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.opt_total:{
				Toast.makeText(this, department.total()+"", Toast.LENGTH_SHORT).show();
				return true;
			}
	
			case R.id.opt_cut:{
				onResume();
				department.cut();
				Serialization.writeCompany(company, company.getName(), path);
				Toast.makeText(this,R.string.successful, Toast.LENGTH_SHORT).show();
				return true;
			}
			
			
			
			// add Department
			case R.id.opt_addElement1:{
				
				final Dialog dialog = new Dialog(this);
				
				dialog.setTitle(R.string.name);
				dialog.setContentView(R.layout.newelement);
				((Button)dialog.findViewById(R.id.bt_ok_newelement)).setOnClickListener(new OnClickListener() {
					
					private Department newdept;

					@Override
					public void onClick(View v) {
						String text = ((EditText)dialog.findViewById(R.id.et_newname)).getText().toString();

						boolean alreadyexist = false;
						if (!text.trim().isEmpty()){
							for (int i=0; i < department.getSubdepts().size();i++)
								if (department.getSubdepts().get(i).getName().equals(text.trim()))
									alreadyexist = true;
							if (!alreadyexist){
								newdept = new Department();
								newdept.setName(text.trim());
								
								department.getSubdepts().add(newdept);
											
											
								Serialization.writeCompany(company, company.getName(), path);
								Toast.makeText(dialog.getContext(), R.string.successful, Toast.LENGTH_SHORT).show();
								String[] all = makeListOfAll();
								adapter = new ArrayAdapter<String>(	getBaseContext(),
																	android.R.layout.simple_list_item_1,
																	all);
								setListAdapter(adapter);

								dialog.dismiss();
							}
							else
								Toast.makeText(dialog.getContext(), R.string.namealreadyexists, Toast.LENGTH_SHORT).show();
						}
						else
							Toast.makeText(dialog.getContext(), R.string.invalidname, Toast.LENGTH_SHORT).show();					}
				});
				((Button)dialog.findViewById(R.id.bt_Cancel_newelement)).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
				dialog.show();
				
				return true;
			}
			
			//add Employee
			case R.id.opt_addElement2: {
				
				final Dialog dialog = new Dialog(this);
				
				dialog.setTitle(R.string.createtheemployee);
				dialog.setContentView(R.layout.newemployee);
				
				((Button)dialog.findViewById(R.id.bt_ok_newemployee)).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						String name = ((EditText)dialog.findViewById(R.id.et_newemployee_name)).getText().toString();
						String address = ((EditText)dialog.findViewById(R.id.et_newemployee_address)).getText().toString();
						double salary = 0;
						
						boolean allright = true;
						
						if (name.trim().isEmpty() || address.trim().isEmpty()){
							allright = false;
							Toast t = Toast.makeText(dialog.getContext(),R.string.nameoraddressisempty, Toast.LENGTH_SHORT);
							t.setGravity(Gravity.CENTER, 0, 0);
							t.show();
						}
						try{
							salary = Double.parseDouble(((EditText)dialog.findViewById(R.id.et_newemployee_salary)).getText().toString().trim());
						}catch (NumberFormatException e) {
							allright = false;
							Toast.makeText(dialog.getContext(), R.string.invalidsalaray, Toast.LENGTH_SHORT).show();
						}
						
						if (allright){
							
							Employee newemployee = new Employee();
							newemployee.setName(name.trim());
							newemployee.setAddress(address.trim());
							newemployee.setSalary(salary);
							
							department.getEmployees().add(newemployee);
							
							Serialization.writeCompany(company, company.getName(), path);
							Toast.makeText(dialog.getContext(), R.string.successful, Toast.LENGTH_SHORT).show();
							
							String[] all = makeListOfAll();
							adapter = new ArrayAdapter<String>(	getBaseContext(),
																android.R.layout.simple_list_item_1,
																all);
							setListAdapter(adapter);

							dialog.dismiss();
						}
					}
				});
				
				((Button)dialog.findViewById(R.id.bt_Cancel_newemployee)).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});

				 
				
				dialog.show();
				
				return true;
			}
			
		}
		
		return false;
	}
	

	
	
}
