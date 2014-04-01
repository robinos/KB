package Controller;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import Model.Lang;
import Model.Profile;
import Model.ProfileLog;
import Model.Profiles;
import View.Dialogs.ConfigDialog;
import View.Main.Main;

/**
 * MainLogic is meant to hold program-wide variables and logic that can be called from
 * many different classes in the application through passed reference.
 * This class also starts the application with the main GUI through the Main object
 * from View.
 * 
 * It creates a reference to itself (mlMainLogic) and the main GUI (mMain), though at
 * the moment mMain is not really used except to start the application.
 * 
 * Valid profiles (prProfiles), the currently active profile (sCurrentProfile), the
 * recorded session for the currently active profile (plProfileLog) and the object for
 * save/load logic (plProfileLogic) are all stored here with get/set methods.
 * 
 * The values of iMaxColumns and iMaxRows are also stored here with get/set methods so
 * that grid sizes can be changed dynamically - though this currently assumes a
 * rectangular grid (no strange formats). 
 * 
 * In order to support multiple open (but not active) profiles, some additional logic
 * has been added.
 * 
 **IMPORTANT NOTE: Using less than 6 grid cells for row*column causes a severe slowdown,
 **probably because having so little data in our TreeMaps causes lookup issues.   
 * 
 * @author: KB Group - Robin Osborne, lock logic by Stefan Arvidson
 *
 */
public class MainLogic
{
    //Instance variables
	private static MainLogic mlMainLogic;
	private static Main mMain;
	
	//The list of valid profiles
	private static Profiles prProfiles;
	
	//The current profile by name
	private static String sCurrentProfile = "default";
	
	//The log object containing all session data
	private ProfileLog plProfileLog;
	
	//The object for handling profile logic
	private static ProfileLogic plProfileLogic;
	
	//The object handling all sensor panel logic
	private static SensorPanelLogic splSensorPanelLogic;
	
	//A hash map to note if the stop playing flag is set for the profile
	private static HashMap<String, Boolean> hmStopPlaying;
	
	//**Note: implementing the below may cause unexpected concurrency
	//behaviour, since our current code assumes a single allowable Profile**	
	//For multiple windows with inactive (non-recording) profiles
	//The ids of all active profiles for comparison with prProfiles
	private static ArrayList<String> alInactiveProfiles;
	//The open but inactive profile logs for the inactive profiles
	private static ArrayList<ProfileLog> alInactiveProfileLogs;	
	//A hash map to mark if inactive profiles have been changed (ie. not saved) 
    private static HashMap<String, Boolean> hmProfilesChanged;
	
	//default grid of 2x3
	private static int iMaxRows = 2;
	private static int iMaxColumns = 3;
	
	//The stop recording flag, true for not recording
	private static boolean bStopRecording = true;
	
	
	/**
	 * Constructor for MainLogic
	 */
	public MainLogic()
	{		
		//An empty profiles object and an empty log object
		prProfiles = new Profiles();
		
		//Add default profile
		Profile pProfile = new Profile();
		pProfile.setId(sCurrentProfile);
		prProfiles.adjustProfileList(pProfile, true);		
		
		//get a ProfileLogic object to handle saving and loading
		plProfileLogic = ProfileLogic.getInstance(this);
		
		//Load up the default profile - if default.xml doesn't already exist there
		//will be some exceptions, but they will not halt execution or hinder the
		//application
		XmlReader xrXmlReader = new XmlReader();
	    plProfileLog = xrXmlReader.readProfileLogXml(sCurrentProfile);
	    plProfileLog.setProfileId(sCurrentProfile);
		
		//get a SensorPanelLogic object to handle all start/stop/record/stop_record
		//actions
		splSensorPanelLogic = new SensorPanelLogic(this);
		
		//Initialise the inactive profile names and inactive ProfileLog
		//array lists
		alInactiveProfiles = new ArrayList<String>();	
		alInactiveProfileLogs = new ArrayList<ProfileLog>();
		hmProfilesChanged = new HashMap<String, Boolean>();
		
		//Create the hashmap of stop playing flags for profiles
		hmStopPlaying = new HashMap<String, Boolean>();
		
		//Set the default profile as unchanged, and not playing
		hmProfilesChanged.put("default", false);
		hmStopPlaying.put("default", true);
	}
	
	
	//*************************************************************************
	//************* One Instance section part 2 of 2 in this class ************
	//*************************************************************************	
	private static File file;
	private static FileChannel channel;
	private static FileLock lock;
	
	public static void unlockFile() {
		// release and delete file lock
		try {
			if(lock != null) {
				lock.release();
				channel.close();
				file.delete();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	static class ShutdownHook extends Thread {
		public void run() {
			 unlockFile();
		}
	}
	
	//*************************************************************************
	//*************************************************************************		
	
	/**
	 * main - This starts the application
	 * @param args
	 */
	public static void main(String[] args) {
		
		//*************************************************************************
		//************* One Instance section part 1 of 2 in this class ************
		//*************************************************************************
		// source:
//http://jimlife.wordpress.com/2008/07/21/java-application-make-sure-only-singleone-instance-running-with-file-lock-ampampampampamp-shutdownhook/ 
		// NOTE: THIS SOLUTION DOES NOT WORK ON LINUX SYSTEMS because its file lock is advisory 

		try {
			file = new File("RingOnRequest.lock");
			// Check if the lock exist
			if (file.exists()) {
				// if exist try to delete it
				file.delete();
			}
			// Try to get the lock
			channel = new RandomAccessFile(file, "rw").getChannel();
			lock = channel.tryLock();
			if(lock == null){
				// File is lock by other application
				channel.close();
				
				ConfigDialog.getInstance();
				JOptionPane.showMessageDialog(null,
					    Lang.rb.getString("already_running"),
					    Lang.rb.getString("already_running.title"),
					    JOptionPane.ERROR_MESSAGE);

            //throw new RuntimeException("Only 1 instance of MyApp can run.");
			}else{
			// Add shutdown hook to release lock when application shutdown
			ShutdownHook shutdownHook = new ShutdownHook();
			Runtime.getRuntime().addShutdownHook(shutdownHook);
		
			//APPLICATION TASKS HERE!!!
			//System.out.println("Running");
			
			//create the MainLogic object and the Main (GUI) object,
			//starting the application
			MainLogic.mlMainLogic = new MainLogic();
			mMain = new Main(mlMainLogic);
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			}
		}catch(IOException e){

			 throw new RuntimeException("Could not start process.", e);
			 // Dialog here: Application already runs...
		}
	}	
	
	
	//Accessor methods
	/**
	 * The getMain method returns the Main GUI object, in case we would
	 * ever need main logic access to objects/methods created in the instance
	 * of mMain, like MainPanel.
	 * 
	 * @return : the Main object for the application
	 */
	public static Main getMain()
	{
		return mMain;
	}	
	
	/**
	 * The getCurrentProfile method returns the current active profile name.  This
	 * is used to identify the current active profile, and the name of the
	 * file/directory that is the save path.  
	 * Only the current profile should be allowed to record data actively.
	 * 
	 * @return : a String representing the current active profile Id
	 */
	public String getCurrentProfile()
	{
		return sCurrentProfile;
	}
	
	/**
	 * The getProfileLog method returns the ProfileLog object that contains all
	 * data during the session for the current profile.
	 * 
	 * @return : the ProfileLog object
	 */
	public ProfileLog getProfileLog()
	{
		return plProfileLog;
	}

	/**
	 * The getProfileLogic method returns the ProfileLogic object used for logic
	 * interacting with profiles;
	 * 
	 * @return : the ProfileLogic object
	 */
	public static ProfileLogic getProfileLogic()
	{
		return plProfileLogic;
	}	

	/**
	 * The getSensorPanelLogic method returns the SensorPanelLogic object used for
	 * play/pause, stop, and record/stop_record logic for the SensorPanel.
	 * 
	 * @return : the SensorPanelLogic object
	 */
	public SensorPanelLogic getSensorPanelLogic()
	{
		return splSensorPanelLogic;
	}	
	
	/**
	 * The getProfiles method returns the Profiles object that contains the
	 * array list (loaded from file) with all valid profiles.
	 * 
	 * @return: the Profiles object, representing all known valid profiles
	 */
	public Profiles getProfiles()
	{
		if(prProfiles == null) prProfiles = new Profiles();
		return prProfiles;
	}	
	
	/**
	 * The getMaxColumns method returns the number of columns for the current chosen
	 * grid layout.
	 * 
	 * @return : an integer value representing maximum columns
	 */
	public int getMaxColumns()
	{
		return iMaxColumns;
	}
	
	/**
	 * The getMaxRows method returns the number of rows for the current chosen
	 * grid layout.
	 * 
	 * @return : an integer value representing maximum rows
	 */
	public int getMaxRows()
	{
		return iMaxRows;
	}		
	
	/**
	 * This gets the stop recording data flag.  If set to true, it stops data
	 * retrieval.
	 * 
	 * @return : a boolean value, true for stop, false for continue/start
	 */
	public boolean getStopRecording()
	{
		return bStopRecording;
	}	
	
	/**
	 * The getStopPlaying method gets the stop playing data flag for the given
	 * profile.
	 * This is implemented with a HashMap to allow multiple inactive (non-recording)
	 * profiles to be allowed to play at the same time, though this functionality
	 * is not implemented.
	 * 
	 * @param sId : the String id name of the profile
	 * @return : true for stop, false for continue/start
	 */
	public boolean getStopPlaying(String sId)
	{
		return hmStopPlaying.get(sId);
	}	
	
	/**
	 * The getInactiveProfiles method returns an array list of the names of
	 * inactive (but open in other windows) profiles.
	 * This is not currently implemented.  
	 * 
	 * @return: an array list of Strings representing inactive profile names
	 */
	public ArrayList<String> getInactiveProfiles()
	{
		if(alInactiveProfiles == null) alInactiveProfiles = new ArrayList<String>();
		return alInactiveProfiles;
	}
	
	/**
	 * The getInactiveProfileLogs method returns an array list of profile logs
	 * (session information) for inactive (but open in other windows) profiles.  
	 * This is not currently implemented.
	 * 
	 * @return: an array list of ProfileLog objects
	 */
	public ArrayList<ProfileLog> getInactiveProfileLogs()
	{
		if(alInactiveProfileLogs == null) alInactiveProfileLogs = new ArrayList<ProfileLog>();
		return alInactiveProfileLogs;
	}	

	/**
	 * The getProfilesChanged method returns the hash map that stores whether a
	 * profile has been changed, and needs to be saved.  It is meant to be used
	 * if we use multiple windows with inactive profiles, in order to prompt saving.
	 * This is not currently implemented.
	 * 
	 * @return: a hash map of boolean values, keyed by profile name 
	 */
	public HashMap<String, Boolean> getProfilesChanged()
	{
		if(hmProfilesChanged == null) hmProfilesChanged = new HashMap<String, Boolean>();
		return hmProfilesChanged;
	}	
	
	//Manipulator methods
	/**
	 * The setProfiles method sets the Profiles object, which holds all profiles
	 * made with the application.  This is only used by ProfileLogic when reading
	 * in all the valid profiles.
	 * 
	 * @param prProfiles : A Profiles object
	 */
	public void setProfiles(Profiles prProfiles)
	{
		MainLogic.prProfiles = prProfiles;
	}
	
	/**
	 * The adjustProfiles method adjusts the Profiles object, adding or removing
	 * a Profile object from the set.  This is to allow deleting of unwanted
	 * Profiles and importing profiles not created by this installation of the 
	 * application.
	 * 
	 * @param prProfiles : the Profile object to add or delete
	 * @param bAdd : a boolean variable, true to add, false to delete
	 */
	public void adjustProfiles(Profile pProfile, boolean bAdd)
	{
		prProfiles.adjustProfileList(pProfile, bAdd);
	}	
	
	/**
	 * The setCurrentProfile method sets the current profile name.  This is used
	 * to identify the current profile, and the name of the file/directory that
	 * is the save path.  It is used when creating new profiles, and when loading.
	 * It also sets the new current profile to unchanged.
	 * 
	 * @param sCurrentProfile : a String representing profile name 
	 */
	public void setCurrentProfile(String sCurrentProfile)
	{
		MainLogic.sCurrentProfile = sCurrentProfile;
		
		hmProfilesChanged.put(sCurrentProfile, false);			
	}
	
	/**
	 * The setProfileLog method sets the ProfileLog object for the current profile.
	 * This is used when creating new profiles or when loading a profile log from file.
	 * 
	 * @param plProfileLog: the ProfileLog to set as current
	 */
	public void setProfileLog(ProfileLog plProfileLog)
	{
		this.plProfileLog = plProfileLog;
	}

	/**
	 * The setProfileLogic method sets a static ProfileLogic object for user with the
	 * application.
	 * This is used when interacting with profiles (create/save/load/import).
	 * 
	 * @param plProfileLogic: the ProfileLogic object to set for application use
	 */
	public static void setProfileLogic(ProfileLogic plProfileLogic)
	{
		MainLogic.plProfileLogic = plProfileLogic;
	}	
	
	/**
	 * The setMaxColumns method sets the number of columns for the current chosen
	 * grid layout.
	 * 
	 * @param : an integer value representing maximum columns
	 */
	public void setMaxColumns(int iMaxColumns)
	{
		MainLogic.iMaxColumns = iMaxColumns;
	}
	
	/**
	 * The setMaxRows method sets the number of rows for the current chosen
	 * grid layout.
	 * 
	 * @param : an integer value representing maximum rows
	 */
	public void setMaxRows(int iMaxRows)
	{
		MainLogic.iMaxRows = iMaxRows;
	}
	
	/**
	 * This sets the stop recording data flag
	 * 
	 * @param bStop : true for stop, false for continue/start
	 */
	public void setStopRecording(boolean bStop)
	{
		MainLogic.bStopRecording = bStop;
	}

	/**
	 * This sets the stop playing data flag for the given profile.
	 * 
	 * @param sId : the String id name of the profile
	 * @param bStop : true for stop, false for continue/start
	 */
	public void setStopPlaying(String sId, boolean bStop)
	{
		hmStopPlaying.put(sId, bStop);
	}		
	
	/**
	 * The setInactiveProfiles method sets an array list of the names of
	 * inactive (but open in other windows) profiles. 
	 * This probably won't be used compared to adjustInactiveProfiles, and does set
	 * all imported inactive profiles to unchanged.
	 * This is not currently implemented. 
	 * 
	 * @param alInactiveProfiles: an array list of Strings representing inactive profile names
	 */
	public void setInactiveProfiles(ArrayList<String> alInactiveProfiles)
	{
		MainLogic.alInactiveProfiles = alInactiveProfiles;
		
		for(String sId : alInactiveProfiles) {
		    hmProfilesChanged.put(sId, false);
		}
	}
	
    /**
     * The adjustInactiveProfiles method adds or removes a String id from
     * the array of inactive profile names.   
     * New inactive profiles added (or removed) are set as unchanged.
     * This is not currently implemented.
     * 
     * @param sId : a String representing profile id
     * @param bAdd : a boolean value, true for adding, false for removing 
     */
    public void adjustInactiveProfiles(String sId, boolean bAdd)
    {
    	if(alInactiveProfiles == null) alInactiveProfiles = new ArrayList<String>();
    	
    	if(sId != null) {
	    	if(bAdd) {
	    		alInactiveProfiles.add(sId);
	    	}
	    	else {
	    		if(alInactiveProfiles.contains(sId)) {
	    			alInactiveProfiles.remove(sId);	    			
	    		}
	    	}
    	}
    }	
	
	/**
	 * The setInactiveProfileLogs method sets an array list of profile logs
	 * (session information) for inactive (but open in other windows) profiles.  
	 * This is not currently implemented.
	 * 
	 * @param alInactiveProfileLogs: an array list of ProfileLog objects
	 */
	public void setInactiveProfileLogs(ArrayList<ProfileLog> alInactiveProfileLogs)
	{
		MainLogic.alInactiveProfileLogs = alInactiveProfileLogs;
	}	
	
    /**
     * The adjustInactiveProfileLogs method adds or removes a ProfileLog from
     * the array of inactive profile logs.   
     * This is not currently implemented.
     * 
     * @param plProfileLog : the ProfileLog to add or remove
     * @param bAdd : a boolean value, true for adding, false for removing 
     */
    public void adjustInactiveProfileLogs(ProfileLog plProfileLog, boolean bAdd)
    {
    	if(alInactiveProfileLogs == null) alInactiveProfileLogs =
    			new ArrayList<ProfileLog>();
    	
    	if(plProfileLog != null) {
	    	if(bAdd) {
	    		alInactiveProfileLogs.add(plProfileLog);
	    	}
	    	else {
	    		if(alInactiveProfileLogs.contains(plProfileLog)) {
	    			alInactiveProfileLogs.remove(plProfileLog);	    			
	    		}
	    	}
    	}
    }	
	
	/**
	 * The setProfilesChanged method sets the hash map that stores whether a profile
	 * has been changed, and needs to be saved.  It is meant to be used if we use
	 * multiple windows with inactive profiles, in order to prompt saving.
	 * 
	 * @param hmProfilesChanged: a hash map of boolean values, keyed by profile name 
	 */
	public void setProfilesChanged(HashMap<String, Boolean> hmProfilesChanged)
	{
		MainLogic.hmProfilesChanged = hmProfilesChanged;
	}    
    
	/**
	 * The adjustProfilesChanged method stores whether a profile has been changed, and
	 * needs to be saved.  It is meant to be used if we use multiple windows with
	 * inactive profiles, in order to prompt saving.
	 * Though this is used in the code, a check is not current implemented with the
	 * Save On Exit Dialog. 
	 * 
	 * @param sId: a String representing profile id
	 * @param bChanged: true for changed status, false for unchanged
	 */
	public void adjustProfilesChanged(String sId, boolean bChanged)
	{
		if(sId != null) {
			hmProfilesChanged.put(sId, bChanged);
		}
	}	
}
