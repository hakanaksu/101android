package org.softlang.features;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;

import org.softlang.company.*;

public class Options{
	
	public boolean copy(Object object, File path) {
		
		Department dept = null;
		Employee emp = null;

		if (object instanceof Department){
			 dept = (Department) object;
			 return write(dept, path);
		}
		if (object instanceof Employee){
			emp = (Employee) object;
			 return write(emp, path);
		}
		
		return false;
		
	}
	
	
	private boolean write(final Object object, final File path){

		File data = new File(path,"temp");
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		
		try {
			fos = new FileOutputStream(data);
			out = new ObjectOutputStream(fos);
			out.writeObject(object);
			out.close();
			
		} catch (IOException ex) {
			return false;
		}
		return true;
	}

	
	public boolean paste(File path, Company company, ArrayList<Integer> positionOfDept, File destinationpath) {
		
		if (!(path.listFiles().length == 0)){
			Object object = null;
	
			File data = new File(path, "temp");
			
			try {
				FileInputStream fis = new FileInputStream(data);
				ObjectInputStream in = new ObjectInputStream(fis);
				object = in.readObject();
				in.close();
			} catch (IOException e) {
				return false;
			} catch (ClassNotFoundException e) {
				return false;
			}
			
			Department dept;
			Employee emp;
			
			Department changedept = null;;
			if (positionOfDept.size() > 1){
				changedept = company.getDepts().get(positionOfDept.get(0));
				for (int i = 1; i < positionOfDept.size(); i++) {
					changedept = changedept.getSubdepts().get(positionOfDept.get(i));
				}
			}
			
			if (object instanceof Department){
				dept = (Department) object;	
				if (positionOfDept.size() > 1){
					changedept.getSubdepts().add(dept);
				} else
					if (positionOfDept.size() == 1)
						company.getDepts().get(positionOfDept.get(0)).getSubdepts().add(dept);
					else
						company.getDepts().add(dept);
			}
			
			if (object instanceof Employee){
				emp = (Employee) object;
				if (positionOfDept.size() > 1){
					changedept.getEmployees().add(emp);
				} else {
					if (positionOfDept.size() == 1)
						company.getDepts().get(positionOfDept.get(0)).getEmployees().add(emp);
					else
						return false;
				}
			}
			
			Serialization.writeCompany(company, company.getName(), destinationpath);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean delete(Context context, Company company, ArrayList<Integer> positionOfDept, File destinationPath) {
		Department dept;
		if (positionOfDept.size() > 1){
			dept = company.getDepts().get(positionOfDept.get(0));
			for (int i = 1; i < positionOfDept.size()-1; i++) {
				dept = dept.getSubdepts().get(positionOfDept.get(i));
			}
			if (dept.getManager() == null){
				//Department
				if (positionOfDept.get(positionOfDept.size()-1) < dept.getSubdepts().size()){
					dept.getSubdepts().remove(positionOfDept.get(positionOfDept.size()-1));
				}
				//Employee
				else
					dept.getEmployees().remove( (positionOfDept.get(positionOfDept.size()-1)) - (dept.getSubdepts().size()) );				
			} else {
				//Manager
				if (positionOfDept.get(positionOfDept.size()-1).equals(0)){
					dept.setManager(null);
				} else {
					//Department
					if (positionOfDept.get(positionOfDept.size()-1) < dept.getSubdepts().size() || (dept.getManager() != null && positionOfDept.get(positionOfDept.size()-1).equals(dept.getSubdepts().size()))){
						dept.getSubdepts().remove(positionOfDept.get(positionOfDept.size()-1)-1);
					}
					//Employee
					else
						dept.getEmployees().remove( (positionOfDept.get(positionOfDept.size()-1)) - (dept.getSubdepts().size()+1) );
				}				
			}
		} else {
			company.getDepts().remove((int)positionOfDept.get(0));
		}		
		Serialization.writeCompany(company, company.getName(), destinationPath);
		return true;
	}
	


	public boolean rename(Company company,
			ArrayList<Integer> positionofdept, String newname, File destinationpath) {
		
		if (newname.trim().isEmpty())
			return false;
		else {
			Department changedept = null;;
			if (positionofdept.size() > 1){
				changedept = company.getDepts().get(positionofdept.get(0));
				for (int i = 1; i < positionofdept.size(); i++) {
					changedept = changedept.getSubdepts().get(positionofdept.get(i));
				}
				
				changedept.setName(newname.trim());
			} else {
				company.getDepts().get(positionofdept.get(0)).setName(newname.trim());
			}
			Serialization.writeCompany(company, company.getName(), destinationpath);
			
			return true;
		}
	}
	
	
	public boolean setAsManager(Context context, Company company, ArrayList<Integer> positionofdept,Employee manager, File destinationpath) {
		
		Department dept;
		
		dept = company.getDepts().get(positionofdept.get(0));
		for (int i = 1; i < positionofdept.size()-1; i++) {
			dept = dept.getSubdepts().get(positionofdept.get(i));
		}
		
		int location;
		
		if (manager != null){
			dept.getEmployees().add(manager);
			location = positionofdept.get(positionofdept.size()-1) - (dept.getSubdepts().size()+1);			
		} else {
			location = positionofdept.get(positionofdept.size()-1) - dept.getSubdepts().size();						
		}
		dept.setManager(dept.getEmployees().get(location));
		dept.getEmployees().remove(location);

		Serialization.writeCompany(company, company.getName(), destinationpath);
		return true;
	}
	
	
}
