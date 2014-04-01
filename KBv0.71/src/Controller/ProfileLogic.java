package Controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import Model.Profile;
import Model.ProfileLog;
import Model.Profiles;

/**
 * The ProfileLogic class handles all logic for creating, saving, loading/importing,
 * and deleting profiles with their corresponding profile logs.
 * All data files are currently saved under the xml directory.
 * One file, ProfileList.xml, holds all valid profiles.  The Profile class was meant
 * to hold more than the profile name, but currently does not.
 * The other file is placed in a sub-directory with the profile name as a the directory
 * name and file name.  This file holds all data for the profile as represented by
 * the ProfileLog class.
 * 
 * @author KB Group - Robin Osborne and Stefan Arvidsson
 */
public class ProfileLogic
{
	private static ProfileLogic instance = null;
	
	private static MainLogic mlMainLogic;
	
	//The xml reader and writer objects
	private XmlReader xrXmlReader;
	private XmlWriter xwXmlWriter;	
	
	/**
	 * The constructor for ProfileLogic.
	 * 
	 * @param mlMainLogic : a reference to the MainLogic object
	 */
	private ProfileLogic(MainLogic mlMainLogic)
	{
		//Sets the local reference to the MainLogic object
		ProfileLogic.mlMainLogic = mlMainLogic;
		
		//creates the main reader and writer for the application
		xrXmlReader = new XmlReader();
		xwXmlWriter = new XmlWriter();
	
		//Read in all previous valid profiles
		Profiles prProfiles = xrXmlReader.readProfileListXml();

		//If there were previous profiles, they are added to the list of
		//known valid profiles during runtime for purposes of loading
	    if(prProfiles != null) {
	        ProfileLogic.mlMainLogic.setProfiles(prProfiles);
	    }		    
	}
	
	
	/**
	 * Creates an instance of ProfileLogic if one doesn't already exist.
	 * Singleton pattern.
	 * 
	 * @param mlMainLogic : a reference to the MainLogic program object
	 * @return : the ProfileLogic object
	 */
	public static ProfileLogic getInstance(MainLogic mlMainLogic){
		if(instance == null)
			instance = new ProfileLogic(mlMainLogic);
		return instance;
	}
	
	
	/**
	 * The loadProfile method loads a ProfileLog from the xml file associated with
	 * the given id, switching it to be the active profile.
	 * 
	 * @param id : the profile id which is also the directory and filename
	 * @return : true for a successful load, otherwise false
	 */
	public boolean loadProfile(String id)
	{	
		if(id != null) {
		    ProfileLog plProfileLog = xrXmlReader.readProfileLogXml(id);
		    
			//Stop the current profile from recording or playing
			mlMainLogic.getSensorPanelLogic().stopRecording();
			mlMainLogic.getSensorPanelLogic().stopPlaying(mlMainLogic.getCurrentProfile());						
			
	    	//Make sure we have a new clean Profile Log in MainLogic
	    	mlMainLogic.setProfileLog(new ProfileLog());
	    	mlMainLogic.setCurrentProfile(id);
	    	//Set the new id to being unchanged (no need to save prompt)
			mlMainLogic.adjustProfilesChanged(id, false);
			
	    	//If the profile log loaded successfully, set the data
		    if(plProfileLog != null) {
		    	//Set the current profile log data
		    	mlMainLogic.setProfileLog(plProfileLog);
		        mlMainLogic.getProfileLog().setProfileId(id);
		        return true;
		    }
		    //Otherwise create a new profile log for that profile
		    else {
		    	System.out.println("Profile Id accepted, but load failed.  Creating new profile log.");
		    	mlMainLogic.setProfileLog(new ProfileLog());
		    	mlMainLogic.getProfileLog().setProfileId(id);
		    	return false;
		    }		    		    		    
		}
		else return false;
	}
	
	
	/**
	 * The saveCurrentProfile method saves the currently active profile.
	 * 
	 * @return: true if the save was successful, otherwise false
	 */
	public boolean saveCurrentProfile()
	{	
		return (xwXmlWriter.writeProfileLogXml(mlMainLogic.getCurrentProfile(), mlMainLogic.getProfileLog()) );
	}			
	
	
	/**
	 * The saveAsProfile method saves the current profile log given under a
	 * given profile name, and sets that name as the active profile.
	 * 
	 * @param plProfileLog: The ProfileLog object to save
	 * @return: true if the save was successful, otherwise false
	 */
	public boolean saveAsProfile(String sId)
	{
		//Make sure any current profile stops recording or playing
		mlMainLogic.getSensorPanelLogic().stopRecording();
		mlMainLogic.getSensorPanelLogic().stopPlaying(mlMainLogic.getCurrentProfile());		

		boolean bAlreadyExists = false;
		
		//Check if given id already exists
		for(Profile pTemp : mlMainLogic.getProfiles().getProfileList()) {
			if(pTemp.getId().equals(sId)) {
				//Tests if the profile already exists in the list
				bAlreadyExists = true;
				// Send notification
			}
		}
		
		//If the profile name doesn't already exist, create it
		if(bAlreadyExists == false) {
			Profile pProfile = new Profile();
			pProfile.setId(sId);
			
			mlMainLogic.adjustProfiles(pProfile, true);
			mlMainLogic.setCurrentProfile(sId);
			
			// add the new profile to the xml file
			xwXmlWriter.writeProfileListXml(mlMainLogic.getProfiles());			
		    
			//create the directory for this profile id
			xwXmlWriter.makeProfileLogDirectory(sId);		
			//write an empty xml file with the profile name as file name,
			//hopefully to be overwritten with data after saving later
			xwXmlWriter.writeProfileLogXml(sId, mlMainLogic.getProfileLog());
			mlMainLogic.getProfileLog().setProfileId(sId);
			
			//Have the imported profile start unchanged and not playing
			mlMainLogic.adjustProfilesChanged(sId, false);
			mlMainLogic.setStopPlaying(sId, true);
			
			return true;
		}else{
			//Otherwise have some sort-of pop-up notification
			System.out.println("Profile already exists warning.  Nothing saved.");
		}		
		
		return false;		
	}	
	
	
	/**
	 * The createProfile method is used when creating a new operation profile.
	 * There is currently no true warning when attempting to use an id that
	 * already exists, but it will fail, changing nothing.
	 * 
	 * @param id : a String representing operation id (used as filename)
	 */
	public void createProfile(String id)
	{	
		//A catch for null indata - default should already exist
		if(id == null) id = "default";
	
		//Make sure any current profile stops recording or playing
		mlMainLogic.getSensorPanelLogic().stopRecording();
		mlMainLogic.getSensorPanelLogic().stopPlaying(mlMainLogic.getCurrentProfile());			
		
		// add the new profile to Profiles, change current profile
		Profile pProfile = new Profile();
		pProfile.setId(id);
		boolean bAlreadyExists = false;

		//Check if the id already exists
		for(Profile pTemp : mlMainLogic.getProfiles().getProfileList()) {
			if(pTemp.getId().equals(pProfile.getId())) {
				//Tests if the profile already exists in the list
				bAlreadyExists = true;
				// Send notification
			}
		}
		
		//If the profile doesn't exist, create it
		if(bAlreadyExists == false) {
			//Add the profile to the profile list in MainLogic
			mlMainLogic.adjustProfiles(pProfile, true);
			//Change the current id, and reference the id with the profile log
			mlMainLogic.setCurrentProfile(id);
		    mlMainLogic.getProfileLog().setProfileId(id);
			
			// add the new profile to the xml file
			xwXmlWriter.writeProfileListXml(mlMainLogic.getProfiles());
			
			//create the directory for this profile id
			xwXmlWriter.makeProfileLogDirectory(id);		
			//write an empty xml file with the profile name as file name,
			//hopefully to be overwritten with data after saving later
			xwXmlWriter.writeProfileLogXml(id, mlMainLogic.getProfileLog());
			
			//Set the profile to be unchanged
			mlMainLogic.adjustProfilesChanged(id, false);
			//Set the profile to start in a non-playing mode (recording was already
			//stopped above)
			mlMainLogic.setStopPlaying(id, true);
			
		}else{
			//Otherwise have some sort-of pop-up notification
			System.out.println("Profile already exists warning.  Nothing created.");
		}
	}
	
	
	/**
	 * The importProfile method takes a file as input and extracts the filename
	 * as the user id. It then attempts to read in the filename.xml and import it
	 * to the application's xml/filename directory.
	 * This assumes the File sent in is an actual file, and not a directory.  The
	 * code in XmlReader also doesn't currently check if it is a valid ProfileLog
	 * xml file before trying to load it.
	 * 
	 * @param fFile: a File to import
	 * @return: if false, a profile of that name already existed, if true, an
	 *          attempt was made to create the new profile (it didn't necessarily
	 *          succeed).
	 */
	public boolean importProfile(File fFile)
	{
		//Make sure any current profile stops recording or playing
		mlMainLogic.getSensorPanelLogic().stopRecording();
		mlMainLogic.getSensorPanelLogic().stopPlaying(mlMainLogic.getCurrentProfile());		
		
		//Get the path from the file
		String sPath = fFile.getAbsolutePath();
		//Split the directories away
		String[] sDivided = sPath.split("\\");
		//Take the final name and split off any .xxx extension
		String sLast[] = sDivided[sDivided.length-1].split(".");
		//Take the first part as a new id
        String id = sLast[0];
		
		// add the new profile to Profiles, change current profile
		Profile pProfile = new Profile();
		pProfile.setId(id);
		boolean bAlreadyExists = false;

		for(Profile pTemp : mlMainLogic.getProfiles().getProfileList()) {
			if(pTemp.getId().equals(pProfile.getId())) {
				//Tests if the profile already exists in the list
				bAlreadyExists = true;
				// Send notification
			}
		}
		
		//If the profile doesn't already exist, create it
		if(bAlreadyExists == false) {
			mlMainLogic.adjustProfiles(pProfile, true);
			mlMainLogic.setCurrentProfile(id);
			
			// add the new profile to the xml file
			xwXmlWriter.writeProfileListXml(mlMainLogic.getProfiles());			
		    
		    ProfileLog plProfileLog = xrXmlReader.importProfileLogXml(sPath, id);
		    
		    //If the import was successful, set it as the current profile log,
		    //otherwise make a new empty profile log
		    if(plProfileLog != null) {
		    	mlMainLogic.setProfileLog(plProfileLog);
		    }
		    else {
		    	plProfileLog = new ProfileLog();
		    	mlMainLogic.setProfileLog(plProfileLog);		    	
		    }
		    
		    //Associate the id with the profile log
		    mlMainLogic.getProfileLog().setProfileId(id);		    
			
			// write an xml file with id as directory- and file- name
			xwXmlWriter.makeProfileLogDirectory(id);			
			xwXmlWriter.writeProfileLogXml(id, mlMainLogic.getProfileLog());
			
			//Have the imported profile start unchanged and not playing
			mlMainLogic.adjustProfilesChanged(id, false);
			mlMainLogic.setStopPlaying(id, true);
			
			return true;
		}else{
			//Otherwise have some sort-of pop-up notification
			System.out.println("Profile already exists warning.  Nothing created.");
		}		
		
		return false;
	}	
	
	
	/**
	 * The exportProfile method takes a path String, and a profile id String as
	 * input. 
	 * This method does not remove the profile from the known valid profile list,
	 * or remove its directory or .xml file.  It just writes the .xml file to
	 * another location.
	 * 
	 * @param path: the String path to export to
	 * @param plProfileLog: the ProfileLog object to export
	 * @return: false if that profile does not exists, true if the profile was
	 *          successfully written to the given path
	 */
	public boolean exportProfile(String sPath, ProfileLog plProfileLog)
	{	
		if(sPath == null || plProfileLog == null) return false;
		
		for(Profile pProfile : mlMainLogic.getProfiles().getProfileList()) {
			
			if(pProfile.getId().equals(plProfileLog.getProfileId())) {
				if(plProfileLog.getProfileId().equals(mlMainLogic.getCurrentProfile())) {
					//Make sure any current profile stops recording or playing
					mlMainLogic.getSensorPanelLogic().stopRecording();
					mlMainLogic.getSensorPanelLogic().stopPlaying(mlMainLogic.getCurrentProfile());					
				}
				
				// write an xml file with the give profile log to the given path			
				if(xwXmlWriter.exportProfileLogXml(sPath, plProfileLog)) return true;				
				
				return false;
			}
			
		}
		
		return false;
	}
	
	/**
	 * The removeProfile method removes the profile from the valid profile list and
	 * removes the file and directory associated with it.
	 * 
	 * @param plProfileLog: The ProfileLog object to remove
	 * @return: true if the removal was successful, otherwise false
	 */
	public boolean removeProfile(ProfileLog plProfileLog)
	{
		for(Profile pProfile : mlMainLogic.getProfiles().getProfileList()) {
			if(pProfile.getId().equals(plProfileLog.getProfileId())) {
				//remove the profile from the list
				mlMainLogic.adjustProfiles(pProfile, false);
				//remove the profile from the xml file over valid profiles
				xwXmlWriter.writeProfileListXml(mlMainLogic.getProfiles());
				
				//Removes the file and attempts to remove the directory
                return(xwXmlWriter.removeProfileLogDirectory(plProfileLog.getProfileId()));
			}
		}
		
		return false;
	}	
	
	
	/**
	 * The saveProfile method saves any profile log given under the current profile name.
	 * This basically allows saving an inactive profile, something that isn't currently
	 * implemented.
	 * 
	 * @param plProfileLog: The ProfileLog object to save
	 * @return: true if the save was successful, otherwise false
	 */
	public boolean saveProfile(ProfileLog plProfileLog)
	{
		return (xwXmlWriter.writeProfileLogXml(plProfileLog.getProfileId(), plProfileLog) );
	}	
	
	
	/**
	 * The saveInactiveProfiles method saves all unsaved inactive profiles.
	 * This is not currently implemented.
	 * 
	 * @return : true if all profiles were saved successfully, false if even one failed
	 */
	public boolean saveInactiveProfiles()
	{
		boolean bSuccess = true;
		ArrayList<ProfileLog> alInactiveProfiles = mlMainLogic.getInactiveProfileLogs();
		HashMap<String,Boolean> hmProfilesChanged = mlMainLogic.getProfilesChanged();
		
		for(ProfileLog plProfileLog : alInactiveProfiles) {
			//If the profile is unsaved, save it
			if(hmProfilesChanged.get(plProfileLog.getProfileId())) {
				if(!xwXmlWriter.writeProfileLogXml(plProfileLog.getProfileId(), plProfileLog)) {
					bSuccess = false;
				}
				mlMainLogic.adjustProfilesChanged(plProfileLog.getProfileId(), false);
			}
		}
		return bSuccess;
	}	
	
}
