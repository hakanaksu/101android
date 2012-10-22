package org.softlang.company.data;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ProfileXMLParser {

	// XML Tag names
	private final static String PROFILE = "Profile";
	private final static String EMPLOYEE = "Employee";
	private final static String COMPANYNAME = "CompanyName";
	private final static String EMLOYEENAME = "EmployeeName";
	private final static String ADDRESS = "Address";
	private final static String SALARY = "Salary";
	private final static String MONEYPERHOUR = "MoneyPerHour";
	private final static String GEOCOORDINATES = "geoCoordinates";
	private final static String NWLONGITUDE = "NorthWestLongitude";
	private final static String NWLATITUDE = "NorthWestLatitude";
	private final static String SELONGITUDE = "SouthEastLongitude";
	private final static String SELATITUDE = "SouthEastLatitude";
	private final static String TOTALTIME = "TotalTime";

	
	
	public static void exportXMLFromProfile (Profile profile, String filename, File path){
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement(PROFILE);
			doc.appendChild(rootElement);
	 
			// employee elements
			Element employee = doc.createElement(EMPLOYEE);
			rootElement.appendChild(employee);

			// companyName elements
			Element companyName = doc.createElement(COMPANYNAME);
			companyName.appendChild(doc.createTextNode(profile.getCompanyName()));
			employee.appendChild(companyName);

			// employeeName elements
			Element employeeName = doc.createElement(EMLOYEENAME);
			employeeName.appendChild(doc.createTextNode(profile.getEmployeeName()));
			employee.appendChild(employeeName);
	 
			// address elements
			Element address = doc.createElement(ADDRESS);
			address.appendChild(doc.createTextNode(profile.getAddress()));
			employee.appendChild(address);
			
			// salary elements
			Element salary = doc.createElement(SALARY);
			rootElement.appendChild(salary);
	 
			// moneyPerHour elements
			Element moneyPerHour = doc.createElement(MONEYPERHOUR);
			moneyPerHour.appendChild(doc.createTextNode(profile.getMoneyPerHour()+""));
			salary.appendChild(moneyPerHour);

			// totalTime elements
			Element totalTime = doc.createElement(TOTALTIME);
			totalTime.appendChild(doc.createTextNode(profile.getTotalTime()+""));
			salary.appendChild(totalTime);
			
			// geoCoordinates elements
			Element geoCoordinates = doc.createElement(GEOCOORDINATES);
			rootElement.appendChild(geoCoordinates);
			
			// nwLongitude elements
			Element nwLongitude = doc.createElement(NWLONGITUDE);
			nwLongitude.appendChild(doc.createTextNode(profile.getNwLongitude()+""));
			geoCoordinates.appendChild(nwLongitude);
			
			// nwLatitude elements
			Element nwLatitude = doc.createElement(NWLATITUDE);
			nwLatitude.appendChild(doc.createTextNode(profile.getNwLatitude()+""));
			geoCoordinates.appendChild(nwLatitude);

			// seLongitude elements
			Element seLongitude = doc.createElement(SELONGITUDE);
			seLongitude.appendChild(doc.createTextNode(profile.getSeLongitude()+""));
			geoCoordinates.appendChild(seLongitude);
			
			// seLatitude elements
			Element seLatitude = doc.createElement(SELATITUDE);
			seLatitude.appendChild(doc.createTextNode(profile.getSeLatitude()+""));
			geoCoordinates.appendChild(seLatitude);
	 
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			doc.normalizeDocument();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(path,filename));
			transformer.transform(source, result);
	 	 
		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }
			
	}
	
	public static Profile importProfileFromXML (String filename, File path){
		
		Profile profile = new Profile();

		try {
		  
			File fXmlFile = new File(path, filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			try{
				profile.setCompanyName(doc.getElementsByTagName(COMPANYNAME).item(0).getChildNodes().item(0).getNodeValue().trim());
				profile.setEmployeeName(doc.getElementsByTagName(EMLOYEENAME).item(0).getChildNodes().item(0).getNodeValue().trim());
				profile.setAddress(doc.getElementsByTagName(ADDRESS).item(0).getChildNodes().item(0).getNodeValue().trim());
				
				double moneyPerHour;
				double totalTime;
				double nwLongitude;
				double nwLatitude;
				double seLongitude;
				double seLatitude;
				
				
				moneyPerHour = Double.parseDouble(doc.getElementsByTagName(MONEYPERHOUR).item(0).getChildNodes().item(0).getNodeValue().trim());
				totalTime = Double.parseDouble(doc.getElementsByTagName(TOTALTIME).item(0).getChildNodes().item(0).getNodeValue().trim());
				nwLongitude = Double.parseDouble(doc.getElementsByTagName(NWLONGITUDE).item(0).getChildNodes().item(0).getNodeValue().trim());
				nwLatitude = Double.parseDouble(doc.getElementsByTagName(NWLATITUDE).item(0).getChildNodes().item(0).getNodeValue().trim());
				seLongitude = Double.parseDouble(doc.getElementsByTagName(SELONGITUDE).item(0).getChildNodes().item(0).getNodeValue().trim());
				seLatitude = Double.parseDouble(doc.getElementsByTagName(SELATITUDE).item(0).getChildNodes().item(0).getNodeValue().trim());
				
				
				profile.setMoneyPerHour(moneyPerHour);
				profile.setTotalTime(totalTime);
				profile.setNwLongitude(nwLongitude);
				profile.setNwLatitude(nwLatitude);
				profile.setSeLongitude(seLongitude);
				profile.setSeLatitude(seLatitude);
				
				profile.setCurrentSalary(profile.getCurrentSalary());
				
			} catch (NullPointerException e) {
				return null;
			} catch (NumberFormatException e) {
				return null;
			}
		  } catch (Exception e) {
			e.printStackTrace();
		  }
		return profile;
	}
		 
	
}
