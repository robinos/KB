package Controller;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import Model.*;

/**
 * The XmlWriter class is used to write objects to xml files.
 * The writeProfileListXml method writes the list of valid profiles to xml file under
 * the xml directory.
 * The writeProfileLogXml method writes a ProfileLog for a valid profile to xml file
 * under the xml/profileId directory.  The file has the same name as the directory.
 * The makeProfileLogDirectory method creates a ProfileLog directory with the same
 * name as the profile id.
 * The removeProfileLogDirectory method deletes the .xml file and attempts to delete
 * the directory, that share the name of the profile id  
 * 
 * @author : KB Group - Robin Osborne
 */
public class XmlWriter
{
	//The application path to the xml sub-directory	
	private String sPath;	
	//The application path for writing sensor data	
	private String sSensorDataPath;	
	//The application path for writing the profile list	
	private String sProfileListPath;	
	
	/**
	 * The constructor for XmlWriter sets the path to the application's xml directory.
	 */	
	public XmlWriter()
	{
		//Sets the application path to the xml sub-directory		
		File fiXmlDIR = new File("xml");
		sPath = fiXmlDIR.getAbsolutePath();  
		sPath = fiXmlDIR + "\\";	
	}	


	/**
	 * The writeProfileListXml method writes to the ProfileList.xml file, updating
	 * by overwriting all data (important to have read the file before writing).
	 * 
	 * @param prProfiles: a Profiles object to write to file 
	 */
	public boolean writeProfileListXml(Profiles prProfiles)
	{
		//The Profiles file path
		sProfileListPath = sPath + "ProfileList.xml";		
		
		try {		 
		    File file = new File(sProfileListPath);
		    //Use the Profiles class
			JAXBContext jaxbContext = JAXBContext.newInstance(Profiles.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
		 
			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			//Write the Profiles object to file			
			jaxbMarshaller.marshal(prProfiles, file);
			//Output to see that the file was really written to (for testing)
			jaxbMarshaller.marshal(prProfiles, System.out);
			
			return true;
			
		} catch (JAXBException e) {
		    e.printStackTrace();
		    return false;
		}	    
	}	

	
	/**
	 * The makeProfileLogDirectory method creates the profile directory with the
	 * same name as the passed string.
	 * 
	 * @param sFileName : a String name to be used for the directory and file
	 * @return : true if success, false if failed
	 */
	public boolean makeProfileLogDirectory(String sFileName)
	{
		//Make the directory based on profile name (sFileName)
		File fiProfileDIR = new File(sPath+sFileName);
		return (fiProfileDIR.mkdir());		
	}
	
	
	/**
	 * The removeProfileLogDirectory method attempts to remove the profile
	 * directory and file with the same name as the passed string.
	 * 
	 * @param sFileName : a String name to be used for the directory and file
	 * @return : true if success, false if failed
	 */
	public boolean removeProfileLogDirectory(String sFileName)
	{
		String sDirPath = sPath+sFileName; 
		File fiProfileDIR = new File(sDirPath);
		//Delete the directory based on profile name (sFileName)
		File fiProfileXml = new File(fiProfileDIR+sFileName);
		fiProfileXml.delete();		
		//Delete the directory based on profile name (sFileName)
		return (fiProfileDIR.delete());		
	}	
	
	
	/**
	 * The writeProfileLogXml method writes the ProfileLog (the object holding all
	 * session data for a profile) to an xml file. 
	 * The xml directory and file is named after the profile id, passed as sFileName.
	 * 
	 * @param sFileName : a String name to be used for the directory and file
	 * @param plProfileLog : the ProfileLog object to be written to xml file
	 */
	public boolean writeProfileLogXml(String sFileName, ProfileLog plProfileLog)
	{			
		//Make the file name based on profile name (sFileName)
		sSensorDataPath = sPath + sFileName + "\\" + sFileName + ".xml";
			
		try {		 
			File file = new File(sSensorDataPath);
			//Use the ProfileLog class
			JAXBContext jaxbContext = JAXBContext.newInstance(ProfileLog.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			 
			//output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			 
			//Write the ProfileLog object to file
			jaxbMarshaller.marshal(plProfileLog, file);
			//Output to see that the file was really written to (for testing)
			jaxbMarshaller.marshal(plProfileLog, System.out);
				
			return true;
				
		} catch (JAXBException e) {
	        e.printStackTrace();
	        return false;
		}	    
	}

	
	/**
	 * The exportProfileLogXml method writes the ProfileLog (the object holding all
	 * session data for a profile) to an xml file. 
	 * The xml file is written to the path location passed as a String.
	 * 
	 * @param sPath : a String file path for the xml file to be written to
	 * @param plProfileLog : the ProfileLog object to be written to xml file
	 */
	public boolean exportProfileLogXml(String sPath, ProfileLog plProfileLog)
	{			
		//Make the file name based on profile name (sFileName)
		sSensorDataPath = sPath;
			
		try {		 
			File file = new File(sSensorDataPath);
			//Use the ProfileLog class
			JAXBContext jaxbContext = JAXBContext.newInstance(ProfileLog.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			 
			//output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			 
			//Write the ProfileLog object to file
			jaxbMarshaller.marshal(plProfileLog, file);
			//Output to see that the file was really written to (for testing)
			jaxbMarshaller.marshal(plProfileLog, System.out);
				
			return true;
				
		} catch (JAXBException e) {
	        e.printStackTrace();
	        return false;
		}	    
	}	
	
}