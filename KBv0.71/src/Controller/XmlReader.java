package Controller;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import Model.*;

/**
 * The XmlReader class is used to read objects from xml files.
 * The readProfileListXml method reads the list of valid profiles from xml file in
 * the xml directory.
 * The readProfileLogXml method reads a ProfileLog for a valid profile from xml file
 * in the xml/"profileId" directory.  The file has the same name as the directory.
 * The importProfileLogXml method attempts to read a ProfileLog xml file and write
 * it to an xml/"profileId" directory.
 * 
 * @author : KB Group - Robin Osborne
 */
public class XmlReader
{
	//The application path to the xml sub-directory
	private String sPath;
	//The application path for writing sensor data
	private String sSensorDataPath;	
	//The application path for writing the profile list
	private String sProfileListPath;	
	
	/**
	 * The constructor for XmlReader sets the path to the application's xml directory.
	 */
	public XmlReader()
	{	    
		//Set the application path to the xml sub-directory
		File fiXmlDIR = new File("xml");
		sPath = fiXmlDIR.getAbsolutePath();  
		sPath = fiXmlDIR + "\\";				
	}

	
	/**
	 * The readProfileListXml method reads in the list of profiles from the
	 * ProfileList.xml file.
	 * 
	 * @return : a Profiles object read from file, or a new empty profile object
	 * on a failed read 
	 */
	public Profiles readProfileListXml()
	{				
		//The path to ProfileList.xml
		sProfileListPath = sPath + "ProfileList.xml";		
		
		if(!new File(sProfileListPath).exists()) return new Profiles();
		
	    try {
		    File file = new File(sProfileListPath);
		    //Use the Profiles class		    
			JAXBContext jaxbContext = JAXBContext.newInstance(Profiles.class);
	 
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			//Read the Profiles object from file	
			Profiles prProfiles = (Profiles) jaxbUnmarshaller.unmarshal(file);
			
			//Return a successfully read Profiles object (print out for testing)
			if(prProfiles != null) {
			    System.out.println(prProfiles);
			    return prProfiles;
			}
			else return new Profiles();
	 
		} catch (JAXBException e) {
			e.printStackTrace();
			return new Profiles();
		}
	}
	
	
	/**
	 * The readProfileLogXml method reads the ProfileLog (the object holding all
	 * session data for a profile) to a ProfileLog object, ready for use. 
	 * The xml directory and file is named after the profile id, passed as sFileName.
	 * 
	 * @param sFileName : a String name to be used for the directory and file
	 * @return : the ProfileLog object to be written to xml file
	 */
	public ProfileLog readProfileLogXml(String sFileName)
	{		
		//the file name based on profile name (sFileName)
	    sSensorDataPath = sPath + sFileName + "\\" + sFileName + ".xml";				
	    
		if(!new File(sSensorDataPath).exists()) return new ProfileLog();	    
	    
		try {
			File file = new File(sSensorDataPath);
			 
		    //Use the ProfileLog class			
			JAXBContext jaxbContext = JAXBContext.newInstance(ProfileLog.class);
		 
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			//Read the ProfileLog object from file
			ProfileLog plProfileLog = (ProfileLog) jaxbUnmarshaller.unmarshal(file);
				
			//Return a successfully read ProfileLog object (print out for testing)
			if(plProfileLog != null) {
				 System.out.println(plProfileLog);
				 return plProfileLog;
			}
			//On a failure above it should result in a catch, but just in case
			else return new ProfileLog();
			
		} catch (JAXBException e) {
			e.printStackTrace();
			return new ProfileLog();
		}
	}
	
	/**
	 * The importProfileLogXml method reads the ProfileLog (the object holding all
	 * session data for a profile) to a ProfileLog object, ready for use. 
	 * 
	 * @param sPath : a String name to be used for the file path
	 * @param id : a String name to be used for the profile name
	 * @return : the ProfileLog object read from the xml file
	 */
	public ProfileLog importProfileLogXml(String sPath, String id)
	{		
		//the file name based on profile name (sFileName)
	    sSensorDataPath = sPath + ".xml";				
	    
		if(!new File(sSensorDataPath).exists()) return new ProfileLog();	    
	    
		try {
			File file = new File(sSensorDataPath);
			 
		    //Use the ProfileLog class			
			JAXBContext jaxbContext = JAXBContext.newInstance(ProfileLog.class);
		 
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			//Read the ProfileLog object from file
			ProfileLog plProfileLog = (ProfileLog) jaxbUnmarshaller.unmarshal(file);
				
			//Return a successfully read ProfileLog object (print out for testing)
			if(plProfileLog != null) {
				 System.out.println(plProfileLog);
				 return plProfileLog;
			}
			//On a failure above it should result in a catch, but just in case
			else return new ProfileLog();
			
		} catch (JAXBException e) {
			e.printStackTrace();
			return new ProfileLog();
		}
	}
	
}