package org.softlang.activities;

import java.io.File;
import java.util.ArrayList;

import org.softlang.company.Company;
import org.softlang.features.Options;
import org.softlang.features.Serialization;
import org.softlang.tests.sampleCompany;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/*
 * @author Hakan Aksu
 */
public class MainActivity extends Activity{
	
	Company loadedCompany = null;
	private Dialog dialog;
	private File path;
	private Builder builder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);
	    ((Button)findViewById(R.id.bt_Company)).setOnLongClickListener(new OnLongClickListener() {
		
			@Override
			public boolean onLongClick(View v) {
				if (loadedCompany != null){
					final Options options = new Options();
					Builder builder = new Builder(MainActivity.this);
					String[] optionList = {getText(R.string.paste).toString()};
					builder.setTitle(R.string.option).setItems(optionList, new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
						if(options.paste(getDir("temp", MODE_PRIVATE), loadedCompany, new ArrayList<Integer>(), path))
							Toast.makeText(getBaseContext(), R.string.successful, Toast.LENGTH_SHORT).show();
						else Toast.makeText(getBaseContext(), R.string.processfailed, Toast.LENGTH_SHORT).show();
						}
					}).show();
					return true;
				} else return false;
			}
			});

	}

	public void onButtonClick(View v) {
		switch(v.getId()){
			
		case R.id.bt_Company:
			if (loadedCompany != null){
				Intent i = new Intent(this,CompanyClickActivity.class);
				i.putExtra("Company",loadedCompany);
				startActivity(i);
			}else {
				Toast.makeText(this, R.string.company_not_loaded, Toast.LENGTH_SHORT).show();
			}
			break;
			
		case R.id.bt_loadtestCompany:
			Company sC = new sampleCompany().getCompany();
			
			path = getDir("company", MODE_PRIVATE);
			Serialization.writeCompany(sC, sC.getName(),path);
			
			loadedCompany =Serialization.readCompany(sC.getName(),path);
			((Button)findViewById(R.id.bt_Company)).setText(loadedCompany.getName());
			
			break;
			
		case R.id.bt_newCompany:
			dialog = new Dialog(this);
			dialog.setContentView(R.layout.newelement);
			((Button)dialog.findViewById(R.id.bt_ok_newelement)).setOnClickListener(new android.view.View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					String text = ((EditText)dialog.findViewById(R.id.et_newname)).getText().toString();
					
					path = getDir("company", MODE_PRIVATE);
					boolean alreadyExist = false;
					
					if (!text.trim().isEmpty()){
						for (int i = 0; i < path.list().length; i++)
							if (path.list()[i].equals(text.trim()))
								alreadyExist = true;
					
						if (!alreadyExist){
							loadedCompany = new Company();
							loadedCompany.setName(text.trim());
							path = getDir("company", MODE_PRIVATE);
							Serialization.writeCompany(loadedCompany, text.trim(), path);
							((Button)findViewById(R.id.bt_Company)).setText(loadedCompany.getName());
							Toast.makeText(dialog.getContext(), R.string.successful, Toast.LENGTH_SHORT).show();
							dialog.dismiss();
						}
						else 
							Toast.makeText(dialog.getContext(), R.string.namealreadyexists , Toast.LENGTH_SHORT).show();

					}
					else
						Toast.makeText(dialog.getContext(), R.string.invalidname , Toast.LENGTH_SHORT).show();
					
				}
			});
			((Button)dialog.findViewById(R.id.bt_Cancel_newelement)).setOnClickListener(new android.view.View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			dialog.setTitle(R.string.name);
			dialog.show();
			break;
			
		case R.id.bt_loadCompany:
			builder = new Builder(this);
			path = getDir("company", MODE_PRIVATE);

			builder.setTitle(R.string.chooseacompany)
					.setItems(path.list(),new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							loadedCompany = Serialization.readCompany(path.list()[which],path);
							((Button)findViewById(R.id.bt_Company)).setText(loadedCompany.getName());
							dialog.dismiss();
						}
					})
					.setNegativeButton(R.string.cancel,null)
					.show();
			
			
			break;
			
		case R.id.bt_editCompany:
			
			if(loadedCompany != null){
			
				dialog = new Dialog(this);
				dialog.setTitle(R.string.name);
				dialog.setContentView(R.layout.newelement);
				((Button)dialog.findViewById(R.id.bt_ok_newelement)).setOnClickListener(new android.view.View.OnClickListener() {
					
					
					@Override
					public void onClick(View v){
	
						String text = ((EditText)dialog.findViewById(R.id.et_newname)).getText().toString();
						
						boolean alreadyexist = false;
						
						if (!text.trim().isEmpty()){
							for (int i = 0; i < path.list().length; i++)
								if (path.list()[i].equals(text.trim()))
									alreadyexist = true;
							
							
							if (!alreadyexist){
								path = getDir("company", MODE_PRIVATE);
								int i = 0;
								while( !path.listFiles()[i].getName().equals(loadedCompany.getName()) )
									i++;
								path.listFiles()[i].delete();
								loadedCompany.setName(text.trim());
								Serialization.writeCompany(loadedCompany, text.trim(), path);
								((Button)findViewById(R.id.bt_Company)).setText(loadedCompany.getName());
								Toast.makeText(dialog.getContext(), R.string.successful, Toast.LENGTH_SHORT).show();
								dialog.dismiss();
							}
							else 
								Toast.makeText(dialog.getContext(), R.string.namealreadyexists , Toast.LENGTH_SHORT).show();
						}
						else
							Toast.makeText(dialog.getContext(), R.string.invalidname , Toast.LENGTH_SHORT).show();
							
					}
				});
				((Button)dialog.findViewById(R.id.bt_Cancel_newelement)).setOnClickListener(new android.view.View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
	
				
				dialog.show();
			
			}
			else
				Toast.makeText(this, R.string.company_not_loaded, Toast.LENGTH_SHORT).show();
			break;
			
		case R.id.bt_deleteCompany:
			builder = new Builder(this);
			path = getDir("company", MODE_PRIVATE);
			builder.setTitle(R.string.deleteCompany)
					.setItems(path.list(),new OnClickListener() {
						
						private int selecttodelete;

						@Override
						public void onClick(DialogInterface dialog, int which) {
							selecttodelete = which;
							if ( loadedCompany == null || (!(path.list()[selecttodelete].equalsIgnoreCase(loadedCompany.getName()))) ){
								builder = new Builder(MainActivity.this);
								builder.setTitle(R.string.areyousure)
										.setPositiveButton(R.string.ok, new OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
												path.listFiles()[selecttodelete].delete();
											}
										})
										.setNegativeButton(R.string.no, new OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
											}
										})
										.show();
								}
								if (!(loadedCompany == null) && (path.list()[selecttodelete].equalsIgnoreCase(loadedCompany.getName()))){
									builder = new Builder(MainActivity.this);
									
									builder.setTitle(path.list()[selecttodelete] + " " + getText(R.string.inuse) + "!").setNegativeButton(R.string.cancel, null).show();
							}
								
						}
					})
					.setNegativeButton(R.string.cancel,null)
					.show();
			break;
		default:
			break;
		}
	}


	
	
	@Override
	protected void onResume() {
	
		if (loadedCompany != null){
			path = getDir("company", MODE_PRIVATE);
			loadedCompany = Serialization.readCompany(loadedCompany.getName(), path);
		}
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		File pathtemp = getDir("temp", MODE_PRIVATE);
		while (!(pathtemp.listFiles().length == 0))
			pathtemp.listFiles()[0].delete();
		pathtemp.delete();
		super.onStop();
	}
	
}
