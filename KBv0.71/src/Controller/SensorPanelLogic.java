package Controller;

import Model.GetSensor;
import Model.ReplaySensor;
import View.MainPanel;
import View.SensorPanel;
import View.SensorMenu.State;

/**
 * The SensorPanelLogic class deals with all play/stop and record/stop_record
 * logic for the SensorPanel.
 * Note that pause is currently only implemented to be the same as stop.
 * 
 * @author: KB Group - Robin Osborne
 */
public class SensorPanelLogic
{		
	//There is current only one ever one ReadArduino and GetSensor object,
    //also static to allows static calls to start/stop sensor reading
    private static ReadArduino readArd = null;
    private static GetSensor getsensor = null;	 
    
    //The static local reference to the MainLogic object
    private static MainLogic mlMainLogic;    
    
	//The ReplaySensor object for profile log replays
	private static ReplaySensor rsReplaySensor = null;    
    
    /**
     * The constructor for SensorPanelLogic
     * 
     * @param mlMainLogic : a reference to the MainLogic object
     */
    public SensorPanelLogic(MainLogic mlMainLogic)
    {
    	SensorPanelLogic.mlMainLogic = mlMainLogic;
    	
		//Create the GetSensor object (used for retrieving data from Arduino) 
		getsensor = new GetSensor(mlMainLogic);
		
		//Set the ReadArduino object, initialise it to work with the GetSensor object
		readArd = new ReadArduino();   	
		
		//Set the ReplaySensor object, set it for the current profile log
        rsReplaySensor = new ReplaySensor(mlMainLogic, mlMainLogic.getProfileLog());
    }    
    
    
	/**
	 * The getReadArduino returns the current ReadArduino object, or null
	 * if one has not been created.
	 * 
	 * @return : a ReadArduino object or null
	 */
    public ReadArduino getReadArduino()
    {
    	if(readArd == null) {
    		readArd = new ReadArduino();
    	}
    	return readArd;
    }

    
    /**
     * The getGetSensor returns the current GetSensor object, or null if
     * one has not been created.
     * 
     * @return : a GetSensor object or null
     */
    public GetSensor getGetSensor()
    {
    	if(getsensor == null) getsensor = new GetSensor(mlMainLogic);
    	return getsensor;
    }    
    
    
    /**
     * The getReplaySensor returns the current ReplaySensor object, or null if
     * one has not been created.
     * 
     * @return : a ReplaySensor object or null
     */
    public ReplaySensor getReplaySensor()
    {
    	return rsReplaySensor;
    }         
    
    
    /**
     * The initialiseRecording method initialises the ReadArduino object for use.
     */
    public void initialiseRecording()
    {
		getReadArduino().initialize(mlMainLogic, getGetSensor());	
    }    
    
    
    /**
     * The initialisePlaying method starts the replay DataGenerator thread.
     */
    public void initialisePlaying()
    {
		rsReplaySensor.startReplay();	
    }     
    
    
    /**
     * The stopPlaying method is used to stop a replay of a recorded ProfileLog.
     * 
     * @param sId : the String id name for the Profile to stop playing
     * @return : true if successful, otherwise false
     */
    public boolean stopPlaying(String sId)
    {
		if(!mlMainLogic.getStopPlaying(sId)) {
			mlMainLogic.setStopPlaying(sId,true);
            State.getInstance().updateState(State.States.PAUSED);
            return true;
		}
    	
    	return false;
    }
    
    
    /**
     * The startPlaying method is used to start a replay of a recorded ProfileLog.
     * 
     * @param sId : the String id name for the Profile to start playing
     */
    public void startPlaying(String sId)
    {  		   	
    	//Stop any recording
    	mlMainLogic.setStopRecording(true);
    	//Close the ReadArduino object and null both it, and the GetSensor object
    	if(getReadArduino() != null) readArd.close();
        readArd = null;
        getsensor = null;    	
        
        //Reset the ReplaySensor object
    	rsReplaySensor.reset();    	    	
		mlMainLogic.setStopPlaying(sId,false);
		
		//Since there's no way to pause or resume yet, just reload
		MainPanel.unloadSensorPanel();
		MainPanel.loadSensorPanel(true);
		State.getInstance().updateState(State.States.PLAYING);	
    }    
    
    
    /**
     * The stopRecording method is used to stop the reading of sensor data from Arduino.
     * It uses the close method in the ReadArduino class.
     * 
     * @return boolean: true is success, false is failure
     */
	public boolean stopRecording() {
		//If the stop recording flag isn't already set
		if(!mlMainLogic.getStopRecording()) {
			//Set the stop recording flag, close the ReadArduino object, and null
			//it, and the GetSensor object
	    	mlMainLogic.setStopRecording(true);
	    	if(getReadArduino() != null) readArd.close();
            readArd = null;
            getsensor = null;
            //Update the icon state to STOP
            State.getInstance().updateState(State.States.STOP);
            return true;
		}
		
		return false;
    }

	
	/**
	 * The record method is used to start/restart the reading of sensor data from
	 * Arduino.  It uses the initialize method in the ReadArduino class and starts
	 * the indata thread.
	 */
	public boolean record() {
		//Stop playing
		mlMainLogic.setStopPlaying(mlMainLogic.getCurrentProfile(),true);
		
		//If the sensor panel doesn't exist, create one (it will start data reading)
		if(!SensorPanel.getExistance()) {
			MainPanel.loadSensorPanel(false);
			return true;
		}
		else {
			//Otherwise, restart data reading
			if(SensorPanel.getExistance()) {
				//if its a play window, recreate a record window
				if(SensorPanel.isReplay()) {
					MainPanel.unloadSensorPanel();
					MainPanel.loadSensorPanel(false);
		            return true;
				}
				else {
					//start recording
					mlMainLogic.setStopRecording(false);
					//Create new GetSensor and ReadArduino objects
					getsensor = new GetSensor(mlMainLogic);
					readArd = new ReadArduino(); 
					//Let the SensorPanel observe the GetSensor object for updates
					getsensor.addObserver(SensorPanel.getInstance(mlMainLogic, false));
					//Initialise recording and start the ReadArduino thread
					initialiseRecording();		
					readArd.start();
					//Set the icon display state to Recording
		            State.getInstance().updateState(State.States.RECORDING);
		            return true;
				}
			}
		}
		
		return false;
    }
}
