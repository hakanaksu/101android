package org.softlang.company;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * A department has a name, a manager, employees, and subdepartments.
 */
public class Department implements Serializable {

	private static final long serialVersionUID = -2008895922177165250L;
	
	private String name;
	private Employee manager;
	private List<Department> subdepts = new LinkedList<Department>();
	private List<Employee> employees = new LinkedList<Employee>();


	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Employee getManager() {
		return manager;
	}

	public void setManager(Employee manager) {
		this.manager = manager;
	}

	public List<Department> getSubdepts() {
		return subdepts;
	}

	public void setSubdepts(List<Department> subdepts) {
		this.subdepts = subdepts;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	public double total() {
		double total = 0;
		if (getManager() != null)
			total += getManager().getSalary();
		for (Department s : getSubdepts())
			total += s.total();
		for (Employee e : getEmployees())
			total += e.getSalary();
		return total;		
	}	
	
	public void cut() {
		if (getManager() != null)
			getManager().cut();
		for (Department s : getSubdepts())
			s.cut();
		for (Employee e : getEmployees())
			e.cut();
	}	
}
