package org.softlang.company.data;

import java.io.Serializable;


public class Profile implements Serializable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5162988354440977341L;

	//Company
	private String companyName;
	
	//Employee
	private String employeeName;
	private String address;
	
	private double moneyPerHour;
	
	//Geo-Coordinates
	private double nwLongitude;
	private double nwLatitude;
	private double seLongitude;
	private double seLatitude;
	
	//For "status" of the Employee
	private double totalTime;
	private double currentSalary;
	
	public Profile(){
		totalTime = 0;
		currentSalary = 0;
	}
	
	public boolean addWorkedTime(double time){
		
		if (time > 0){
			totalTime += time;
			return true;
		}
		
		return false;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getMoneyPerHour() {
		return moneyPerHour;
	}

	public void setMoneyPerHour(double moneyPerHour) {
		this.moneyPerHour = moneyPerHour;
	}

	public double getNwLongitude() {
		return nwLongitude;
	}

	public void setNwLongitude(double nwLongitude) {
		this.nwLongitude = nwLongitude;
	}

	public double getNwLatitude() {
		return nwLatitude;
	}

	public void setNwLatitude(double nwLatitude) {
		this.nwLatitude = nwLatitude;
	}

	public double getSeLongitude() {
		return seLongitude;
	}

	public void setSeLongitude(double seLongitude) {
		this.seLongitude = seLongitude;
	}

	public double getSeLatitude() {
		return seLatitude;
	}

	public void setSeLatitude(double seLatitude) {
		this.seLatitude = seLatitude;
	}

	public double getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}

	public double getCurrentSalary() {
		int hour =  (int) totalTime / 60;
		int minute = (int) totalTime % 60;
		currentSalary = hour * getMoneyPerHour() + (getMoneyPerHour()/60.0) * minute;
		return currentSalary;
	}

	public void setCurrentSalary(double currentSalary) {
		this.currentSalary = currentSalary;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}

