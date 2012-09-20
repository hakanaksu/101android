package org.softlang.activities;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import org.softlang.company.*;
import org.softlang.features.Options;
import org.softlang.features.Serialization;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
public class CompanyClickActivity extends ListActivity implements OnItemLongClickListener {

	private Company company;
	
	private File path;
	
	private String[] deptsString;
	private ArrayAdapter<String> adapter;
	
	ArrayList<Integer> positionOfDept;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.departmentlist);
		
		company = (Company) getIntent().getExtras().get("Company");		
		setTitle(company.getName());
				
		getListView().setOnItemLongClickListener(this);
		
		positionOfDept = new ArrayList<Integer>();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Intent i = new Intent(this, DepartmentClickActivity.class);
		positionOfDept = new ArrayList<Integer>();
		positionOfDept.add(position);
		i.putExtra("Department", company.getDepts().get(position));
		i.putExtra("Company", company);
		i.putExtra("positionofdept", positionOfDept);
		startActivity(i);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menue, menu);
		menu.removeItem(R.id.opt_addElement2);
		menu.getItem(0).setTitle(R.string.addDepartment);
		return super.onCreateOptionsMenu(menu);
	}
	
	private void refreshListView(){
		
		if (company != null){
			File path = getDir("company", MODE_PRIVATE);
			company = Serialization.readCompany(company.getName(), path);
		}
		
		positionOfDept = new ArrayList<Integer>();
		
		LinkedList<Department> Depts = (LinkedList<Department>)company.getDepts();
		
		//The names of the departments are written in an Array of String
		deptsString = new String[Depts.size()];
		for(int i = 0;i<Depts.size(); i++){
			deptsString[i]= "[D] " + Depts.get(i).getName();
		}
			
		adapter = new ArrayAdapter<String>(	this,
											android.R.layout.simple_list_item_1,
											deptsString);
		setListAdapter(adapter);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
			//total salaries
			case R.id.opt_total:{
				Toast.makeText(this, company.total()+"", Toast.LENGTH_SHORT).show();
				return true;
			}
			
			//cut salaries
			case R.id.opt_cut:{
				company.cut();
				File path = getDir("company", MODE_PRIVATE);
				Serialization.writeCompany(company, company.getName(), path);
				Toast.makeText(this,R.string.successful, Toast.LENGTH_SHORT).show();
				return true;
			}
			//add Department
			case R.id.opt_addElement1:{
				final Dialog dialog;
				dialog = new Dialog(this);
				dialog.setTitle("Name");
				dialog.setContentView(R.layout.newelement);
				((Button)dialog.findViewById(R.id.bt_ok_newelement)).setOnClickListener(new OnClickListener() {
					
					private Department newdept;

					@Override
					public void onClick(View v) {
						String text = ((EditText)dialog.findViewById(R.id.et_newname)).getText().toString();

						boolean alreadyexist = false;
						if (!text.trim().isEmpty()){
							for (int i=0; i < company.getDepts().size();i++)
								if (company.getDepts().get(i).getName().equals(text.trim()))
									alreadyexist = true;
							if (!alreadyexist){
								newdept = new Department();
								newdept.setName(text.trim());
								
								

								company.getDepts().add(newdept);
								path = getDir("company", MODE_PRIVATE);
								Serialization.writeCompany(company, company.getName(), path);
								Toast.makeText(dialog.getContext(), R.string.successful, Toast.LENGTH_SHORT).show();
								
								refreshListView();

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
		}
		
		return false;
	}

	@Override
	protected void onResume() {
		
		refreshListView();
		
		super.onResume();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
		
		Builder builder = new Builder(this);
		String[] optionlist =	{getText(R.string.copy).toString(),
				 				 getText(R.string.paste).toString(),
				 				 getText(R.string.rename).toString(),
				 				 getText(R.string.delete).toString()};
		
		builder.setTitle(R.string.option)
		.setItems(optionlist, new DialogInterface.OnClickListener() {
		
		@SuppressWarnings("unchecked")
		@Override
		public void onClick(DialogInterface dialog, int which) {
		
		final Options options = new Options();
		
		
		switch (which) {
			
		//copy
		case 0:
			if(options.copy(company.getDepts().get(position), getDir("temp", MODE_PRIVATE)))
				Toast.makeText(getBaseContext(), R.string.successful, Toast.LENGTH_SHORT).show();
			else Toast.makeText(CompanyClickActivity.this, R.string.processfailed, Toast.LENGTH_SHORT).show();
			break;
			
		//paste
		case 1:
			ArrayList<Integer> positionofdepartment = (ArrayList<Integer>) positionOfDept.clone();
			positionofdepartment.add(position);
			if (options.paste(getDir("temp", MODE_PRIVATE), company, positionofdepartment, getDir("company", MODE_PRIVATE)))
				Toast.makeText(CompanyClickActivity.this, R.string.successful, Toast.LENGTH_SHORT).show();
			else Toast.makeText(CompanyClickActivity.this, R.string.processfailed, Toast.LENGTH_SHORT).show();
			break;
		
		//rename	
		case 2:
			final ArrayList<Integer> positionofdep = (ArrayList<Integer>) positionOfDept.clone();
			positionofdep.add(position);
			final Dialog dialog1 = new Dialog(CompanyClickActivity.this);
			dialog1.setTitle(R.string.name);
			dialog1.setContentView(R.layout.newelement);
			((Button)dialog1.findViewById(R.id.bt_ok_newelement)).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String newname = ((EditText)dialog1.findViewById(R.id.et_newname)).getText().toString();
					if (options.rename(company, positionofdep, newname, getDir("company", MODE_PRIVATE)))
						Toast.makeText(CompanyClickActivity.this, R.string.successful, Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(CompanyClickActivity.this, R.string.processfailed, Toast.LENGTH_SHORT).show();
					dialog1.dismiss();
					
					refreshListView();
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
			Builder build = new Builder(CompanyClickActivity.this);
			build.setTitle(R.string.areyousure)
				 .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						ArrayList<Integer> tempPositionOfDepartment = (ArrayList<Integer>) positionOfDept.clone();
						tempPositionOfDepartment.add(position);
						if (options.delete(CompanyClickActivity.this, company, tempPositionOfDepartment, getDir("company", MODE_PRIVATE)))
							Toast.makeText(getBaseContext(), R.string.successful, Toast.LENGTH_SHORT).show();						
						
						
						refreshListView();
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
		
		return false;
	}	
}
