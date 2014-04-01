package View;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JLabel;
import javax.swing.JPanel;

import Controller.DataGenerator;
import Controller.MainLogic;
import Controller.SensorPanelLogic;
import Model.GetSensor;
import Model.ReplaySensor;
import Model.Sensor;
import Model.SensorData;
import Model.SetSensorColor;

/**
 * The SensorPanel class is the main JPanel for displaying sensor data.
 * In its current form, it is a simple grid with data panels, though this will
 * be changing.
 * It is an observer, observing the GetSensor object in order to receive updates
 * on sensor data read in from Arudino.
 */
public class SensorPanel extends JPanel implements Observer {	
	
	//required when extending JPanel
    private static final long serialVersionUID = 2L;
	
	private static SensorPanel instance = null;
	
    // Change to get better color span
	private static final int resolutionEnlarger = 2;	
	
	//Hashmaps because I had some initial trouble with ArrayLists
	//for unknown reasons
    private HashMap<Integer, JLabel> hmDataLabelList;
    private HashMap<Integer, String> hmPressureSensorDataList;
    private HashMap<Integer, String> hmTemperatureSensorDataList;  
    
    //The static local reference to the MainLogic object
    private static MainLogic mlMainLogic;
	private static SensorPanelLogic splSensorPanelLogic;     
    
	//The object for setting sensor colours
    private SetSensorColor setSensorColor;	
	
    //The ProfileLog object to work with
    //private ProfileLog plProfileLog;
    
    //Local variables for the maximum columns and rows, and maximum number of
    //grid cells
    private int iMaxColumns;
    private int iMaxRows;    
    private int iMaxCells;
       
    //true for a replay, false for a recorder
    private static boolean bReplay = false;

	/**
     * Constructor for SensorPanel
     * 
     * @param mlMainLogic : a reference to the MainLogic object
     */
	private SensorPanel(MainLogic mlMainLogic, boolean bReplay)
	{
		//mlMainLogic.setStop(false);
		
		SensorPanel.bReplay = bReplay;
		
		//Set the local MainLogic reference
		SensorPanel.mlMainLogic = mlMainLogic;
		splSensorPanelLogic = mlMainLogic.getSensorPanelLogic();
		
		if(mlMainLogic.getProfileLog() != null) {
			mlMainLogic.getProfileLog().setPressureSensors(mlMainLogic.getProfileLog().getPressureSensors());		
			mlMainLogic.getProfileLog().setTemperatureSensors(mlMainLogic.getProfileLog().getTemperatureSensors());
		}
        
		//Set the Profile Log object
		//this.plProfileLog = plProfileLog;
		
		//Determine max columns and max rows from the values in MainLogic
		iMaxColumns = mlMainLogic.getMaxColumns();
		iMaxRows = mlMainLogic.getMaxRows();
		iMaxCells = iMaxColumns*iMaxRows;		
		//System.out.println("Columns: "+iMaxColumns+" Rows: "+iMaxRows+" Cells: "+iMaxCells);		
		
		//Initialise the hash maps of JLabels and Sensor names
		hmDataLabelList = new HashMap<Integer, JLabel>();
		hmPressureSensorDataList = new HashMap<Integer, String>();
		hmTemperatureSensorDataList = new HashMap<Integer, String>(); 		
		
		//Create object with default value (to be overwritten)
		setSensorColor = new SetSensorColor();
		
		if(bReplay == false) {
			//Set to 'recording'			
		    mlMainLogic.setStopPlaying(mlMainLogic.getProfileLog().getProfileId(),true);
		    mlMainLogic.setStopRecording(false);		    
		    splSensorPanelLogic.getGetSensor().addObserver(this);
		    splSensorPanelLogic.initialiseRecording();		
		    splSensorPanelLogic.getReadArduino().start();
	        //State.getInstance().updateState(State.States.RECORDING);		
		}
		else {
			//Set to 'playing'				
		    mlMainLogic.setStopRecording(true);
			mlMainLogic.setStopPlaying(mlMainLogic.getProfileLog().getProfileId(),false);
	        //splSensorPanelLogic.setReplaySensor(plProfileLog);
	        splSensorPanelLogic.getReplaySensor().getDataGenerator().addObserver(this);	
	        splSensorPanelLogic.initialisePlaying();
	        //State.getInstance().updateState(State.States.PLAYING);
	        
		}
        
		//Use a grid layout with the same amount of columns and rows as sensors.
		//This is for the simple test representation.
		setLayout(new GridLayout(iMaxRows,iMaxColumns));
		
		//Adds labels 1 to MaxCells to the grid layout and array list of labels,
		//default is 1 to 4.  Also fill the data hashmap with default values
		//corresponding to the labels.
		for(int i=1;i<=iMaxCells;i++) {
			String tempData1 = "W"+i;
			String tempData2 = "T"+i;
			JLabel tempLabel = new JLabel(tempData1+tempData2);		
			tempLabel.setOpaque(true);	
			tempLabel.setBackground(Color.green);			
			hmDataLabelList.put(i,tempLabel);
			hmPressureSensorDataList.put(i, tempData1);
			hmTemperatureSensorDataList.put(i, tempData2);
			add(tempLabel);
		}		
		
		//Set the SensorPanel size and make visible
		this.setSize(500, 500);
		setVisible(true);
		revalidate();
		repaint();
	}
	
	/**
	 * 
	 * @return
	 */
    public static boolean isReplay()
    {
		return bReplay;
	}	
	
	/**
	 * This is part of the singleton pattern, ensuring that only one instance
	 * of SensorPanel is running at once.  This may need to be changed if multiple
	 * sessions are allowed to be open (but not running) at once.
	 * 
	 * @param mlMainLogic : a reference to the MainLogic object
	 * @return : the single allowable instance of the SensorPanel object
	 */
	public static SensorPanel getInstance(MainLogic mlMainLogic, boolean bReplay){
		if(instance == null)
			instance = new SensorPanel(mlMainLogic, bReplay);
		return instance;
	}
	
	/**
	 * The getExistance method is used to check if an instance of SensorPanel exists.
	 * This is important for parts of the application that need to interact with
	 * the SensorPanel.
	 * 
	 * @return: true if an instance of SensorPanel exists, otherwise false 
	 */
	public static boolean getExistance() {
		if(instance == null) return false;
		else return true;
	}
	
	/**
	 * The setNonExistance sets the SensorPanel instance to null, right before it is
	 * removed.
	 */
	public static void setNonExistance() {
		if(instance != null) instance = null;
	}	
	
	/**
	 * The setValue method cycles through the various sensors by number (for some
	 * reason I'm having problems using name instead which would allow ALL sensors to
	 * be read in one loop).  The sensor is acquired using the GetSensor object and
	 * then the data is retrieved from the returned sensor object. 
	 * It is used when recording profile logs.
	 */
    private void setValue() {  	
    	if(mlMainLogic.getStopPlaying(mlMainLogic.getProfileLog().getProfileId()) &&
       	     !mlMainLogic.getStopRecording()) { 
    		
	    	for(int i=1;i<=iMaxCells;i++) {
	    		//Pressure
	    		Sensor sPSensor = splSensorPanelLogic.getGetSensor().getSensor("W"+i);    		
	    		
				ArrayList<SensorData> alPSensorData = sPSensor.getSensorData();
			    int value1 = 0;
				
				if(alPSensorData != null) {
				    SensorData sdPSensorData = alPSensorData.get(alPSensorData.size()-1);
				    value1 = (int)sdPSensorData.getValue();
				}
				
				//Temperature
	    		Sensor sTSensor = splSensorPanelLogic.getGetSensor().getSensor("T"+i);    		
	    		
	    		ArrayList<SensorData> alTSensorData = sTSensor.getSensorData();
			    int value2 = 0;
				
				if(alTSensorData != null) {
				    SensorData sdTSensorData = alTSensorData.get(alTSensorData.size()-1);
				    value2 = (int)sdTSensorData.getValue();
				} 				
				
				hmPressureSensorDataList.put(sPSensor.getNumber(), ""+value1);
				hmTemperatureSensorDataList.put(sPSensor.getNumber(), ""+value2);
				JLabel tempLabel = hmDataLabelList.get(sPSensor.getNumber());
				    
				//Set the background based on the deviation value
				/*tempLabel.setBackground
				    (setSensorColor.getcolorForSensorPanel(value1*resolutionEnlarger, sPSensor.getInitialValueForColor()));
				tempLabel.setText(tempData);*/
				
				//Set the background based on the deviation value
				tempLabel.setBackground
				    (setSensorColor.getcolorForSensorPanel(value1*resolutionEnlarger, sPSensor.getInitialValueForColor()));
				tempLabel.setText(value1+" ("+value2+")");				
	    	}
    	}
    }    
    
	/**
	 * This setValue method retrieves data from the SensorData object indata.
	 * It is used for replaying profile logs.
	 */
    private void setValue(SensorData sdSensorData)
    {  	
    	if(!mlMainLogic.getStopPlaying(mlMainLogic.getProfileLog().getProfileId()) &&
    	     mlMainLogic.getStopRecording()) {
	    	int value = 0;
	    	
	    	if(sdSensorData != null) {
	    		
	    		System.out.println("Sensor Data: "+sdSensorData.toString());
	    		
				value = (int)sdSensorData.getValue();    		
				
				Sensor sSensor = mlMainLogic.getProfileLog().getSensorMap().get(sdSensorData.getSensorName());
								
				if(sSensor != null) {
					JLabel tempLabel = hmDataLabelList.get(sSensor.getNumber());
					
					if(sSensor.getName().charAt(0) == 'W') {				
						hmPressureSensorDataList.put(sSensor.getNumber(), ""+value);
					}
					else if(sSensor.getName().charAt(0) == 'T') {				
						hmTemperatureSensorDataList.put(sSensor.getNumber(), ""+value);						
					}	
					
				    int iPValue = Integer.parseInt(hmPressureSensorDataList.get(sSensor.getNumber()));
					
					//Set the background based on the deviation value
					tempLabel.setBackground
						(setSensorColor.getcolorForSensorPanel
								(iPValue*resolutionEnlarger, sSensor.getInitialValueForColor()));
					
					
					tempLabel.setText(hmPressureSensorDataList.get(sSensor.getNumber())
							+" ("+hmTemperatureSensorDataList.get(sSensor.getNumber())+")");					
				}
				//end inner if
	    	}
	    	//end outer if
    	}
    }   
	
	/**
	 * The update method reacts to an notification from an observed object.
	 * It is used to monitor an observed GetSensor object and change the view
	 * based on new data input from the sensors.
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		//For receiving sensor data from GetSensor
		if (arg0 instanceof GetSensor) {
    		mlMainLogic.adjustProfilesChanged(mlMainLogic.getCurrentProfile(), true);
            setValue();
        }
		
		//For receiving sensor data from ReplaySensor
		if (arg0 instanceof DataGenerator) {
			SensorData sdSensorData = (SensorData)arg1;
            setValue(sdSensorData);
        }		
	}
}
