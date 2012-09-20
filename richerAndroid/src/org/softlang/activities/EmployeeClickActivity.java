package org.softlang.activities;

import java.io.File;
import java.util.ArrayList;

import org.softlang.company.*;
import org.softlang.features.Serialization;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

/*
 * @author Hakan Aksu
 */
public class EmployeeClickActivity extends Activity{
	
	private EditText ed_name;
	private EditText ed_address;
	private EditText ed_salary;
	
	private Employee employee;
	private Company company;
	private ArrayList<Integer> positionofdept;
	
	boolean manager;
	

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.employeedata);
		
		employee = (Employee)getIntent().getExtras().get("Employee");
		company = (Company) getIntent().getExtras().get("Company");
		positionofdept = (ArrayList<Integer>) getIntent().getExtras().get("positionofdept");
		
		setTitle(employee.getName());

		ed_name = ((EditText)findViewById(R.id.ed_name));
		ed_address = ((EditText)findViewById(R.id.ed_address));
		ed_salary = ((EditText)findViewById(R.id.ed_salary));
		
		ed_name.setText(employee.getName());
		ed_address.setText(employee.getAddress());
		ed_salary.setText(employee.getSalary()+"");
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menue, menu);
		menu.removeItem(R.id.opt_total);
		menu.removeItem(R.id.opt_addElement2);
		menu.getItem(0).setTitle(R.string.save);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		
		//save
		case R.id.opt_addElement1:
			
			String changedname = ((EditText)findViewById(R.id.ed_name)).getText().toString().trim();
			String changedaddress = ((EditText)findViewById(R.id.ed_address)).getText().toString().trim();
			double changedsalary = 0;
			
			boolean allright = true;
			
			if (changedname.trim().isEmpty() || changedaddress.trim().isEmpty()){
				allright = false;
				Toast t = Toast.makeText(this,R.string.nameoraddressisempty, Toast.LENGTH_SHORT);
				t.setGravity(Gravity.CENTER, 0, 0);
				t.show();
			}
			try{
				changedsalary = Double.parseDouble(((EditText)findViewById(R.id.ed_salary)).getText().toString().trim());
			}catch (NumberFormatException e) {
				allright = false;
				Toast.makeText(this, R.string.invalidsalaray, Toast.LENGTH_SHORT).show();
			}
			
			if (allright){
				employee.setName(changedname);
				employee.setAddress(changedaddress);
				employee.setSalary(changedsalary);
				
				Department changedept;
				changedept = company.getDepts().get(positionofdept.get(0));
				for (int i = 1; i < positionofdept.size(); i++) {
					changedept = changedept.getSubdepts().get(positionofdept.get(i));
				}
				
				if (getIntent().getExtras().getBoolean("Manager")){
					changedept.setManager(employee);
				} else {
					changedept.getEmployees().set(getIntent().getExtras().getInt("position"), employee);
				}
							
				File path = getDir("company", MODE_PRIVATE);
				Serialization.writeCompany(company, company.getName(), path);
				Toast.makeText(this, R.string.successful, Toast.LENGTH_SHORT).show();
			}
			
			return true;
			
		case R.id.opt_cut:
			employee.cut();
			ed_salary.setText(employee.getSalary()+"");
			return true;
			
		default:
			break;
		}
		
		
		return false;
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
   
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
     
			String changedname = ((EditText)findViewById(R.id.ed_name)).getText().toString().trim();
			String changedaddress = ((EditText)findViewById(R.id.ed_address)).getText().toString().trim();
			double changedsalary = 0;
			
			try{
				changedsalary = Double.parseDouble(((EditText)findViewById(R.id.ed_salary)).getText().toString());
			}catch (NumberFormatException e) {
				((EditText)findViewById(R.id.ed_salary)).setText(((EditText)findViewById(R.id.ed_salary)).getText().toString().trim() + " " +getText(R.string.invalidsalaray));
			}
			
			if (  !(changedname.equals(employee.getName())) || !(changedaddress.equals(employee.getAddress())) || !(changedsalary == employee.getSalary()) ){
	
		        Builder back = new AlertDialog.Builder(this);
		        back.setTitle(R.string.youvechangedsth);
		        back.setMessage(R.string.discardchanges);
		
		        back.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		
		            public void onClick(DialogInterface dialog, int whichButton) {
		            finish();
		            }
		        });
		        back.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
		
		            public void onClick(DialogInterface dialog, int whichButton) {
		            }
		        });
		        back.show();
			} else{
				finish();
			}
	    }
	    return false;
	}
}
