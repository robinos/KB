package Model;

import java.util.Observable;
import java.util.TreeMap;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import Controller.MainLogic;

/**
 * The GetSensor class is used to receive data from the ReadArduino class and
 * update any observers when new input is received.  It also stores data in
 * the ProfileLog class for later analysis and saving.
 * 
 **IMPORTANT NOTE: ArrayLists are used to hold the indata.  When a pure TreeMap
 **implementation was in place (no array lists in ProfileLog), it caused a terrible
 **slowdown of reading data.  Our theory is that since we cycle through ALL elements
 **(avoiding using individual Threads) it was a very inefficient use of the TreeMap
 **lookup, whereas the Array Lists are much simpler, less big O.
 * 
 * Note that values from Arduino are currently read in as integers, despite Arduino
 * being capable of sending floats, and despite the SensorData class using doubles.
 * Future implementation may want values to be read in as floats in order to maintain
 * more precise data values.
 * 
 * @authors : Mei Ha and Christer Evervall
 *  altered by Robin Osborne - dynamic grids (columns & rows), changed to
 *  use my Sensor class
 */
public class GetSensor extends Observable {
	
	//Changed to use my Sensor class, Robin 2013-02-08		
	
	//local reference to the MainLogic object
	private static MainLogic mlMainLogic;
	
	//default values
	private int iMaxColumns = 2;
	private int iMaxRows = 2;
	
	private final int startValueOfData = 2;
	
	/**
	 * Constructor for GetSensor.
	 * 
	 * This creates a grid of expected sensors (with unique names) for
	 * a configuration with the given columns and rows.  One sensor per
	 * grid square is assumed.
	 * 
	 * @param mlMainLogic : a reference to the MainLogic object
	 */
	public GetSensor(MainLogic mlMainLogic)
	{	
		//initialise the connection to the MainLogic object
		GetSensor.mlMainLogic = mlMainLogic;
		
		//Get the rows and columns from MainLogic
		iMaxColumns = mlMainLogic.getMaxColumns();
		iMaxRows = mlMainLogic.getMaxRows();
		
		//Retrieve the map of all sensors for the current ProfileLog
        TreeMap<String, Sensor> tmTempMap = mlMainLogic.getProfileLog().getSensorMap();
        
		int iCounter = 1;
		
		//Pressure sensors
		for(int iC = 1;iC <= iMaxColumns;iC++) {
			for(int iR = 1;iR <= iMaxRows;iR++) {
				//If the sensor doesn't already exist, create it
				if(!tmTempMap.containsKey("W"+iCounter)) {
		    	    Sensor tempSensor = new Sensor();
		    	    tempSensor.setName("W"+iCounter);
		    	    tempSensor.setNumber(iCounter);
		    	    tempSensor.setCellPosition(iC, iR);
		    	    //Adjusting the pressure sensor array also adjusts the sensor map
					mlMainLogic.getProfileLog().adjustPressureSensors(tempSensor, true);	    	    
					iCounter++;
				}
			}
	    }
		
		iCounter = 1;
		
		//Temperature sensors
		for(int iC = 1;iC <= iMaxColumns;iC++) {
			for(int iR = 1;iR <= iMaxRows;iR++) {
				//If the sensor doesn't already exist, create it
				if(!tmTempMap.containsKey("T"+iCounter)) {
		    	    Sensor tempSensor = new Sensor();
		    	    tempSensor.setName("T"+iCounter);
		    	    tempSensor.setNumber(iCounter);
		    	    tempSensor.setCellPosition(iC, iR);
		    	    //Adjusting the temperature sensor array also adjusts the sensor map		    	    
					mlMainLogic.getProfileLog().adjustTemperatureSensors(tempSensor, true);	    	    
					iCounter++;
				}
			}
	    }			
	}
	
	
	/**
	 * The setSensorData method cycles through known sensor names and compares them
	 * against in-data received from Arduino.  If there is a match, the data is
	 * recorded.
	 * This could potentially be optimised with local variables if the number of
	 * sensors actually begin to slow the program.
	 * 
	 * @param s : a String value from in-data
	 */
	public void setSensorData (String s)
	{
		//If the stop recording flag hasn't been set
		if(!mlMainLogic.getStopRecording()) {
			
			SensorData sdSensorData;
			//Get the first two characters of the in-string (this is our sensor name
			//such as W1 or T3)
			String sSubName = s.substring(0, 2);
			
			//Testing
			//System.out.println(s);
			//System.out.println(sSubName);
			
			//Pressure readings
			for(Sensor tempSensor : mlMainLogic.getProfileLog().getPressureSensors()) {
				//If the input name is the same as a pressure sensor, add data
				if(sSubName.equals(tempSensor.getName())) {
					//create a new SensorData object and fill it with data
				    sdSensorData = new SensorData();
					sdSensorData.setSensorName(tempSensor.getName());
					Calendar cal = Calendar.getInstance();
			    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
					sdSensorData.setTimeStamp(sdf.format(cal.getTime())); //get system time
					sdSensorData.setValue(Integer.parseInt(s.substring(startValueOfData)));
					//Add the sensor data to the sensor
					tempSensor.adjustSensorData(sdSensorData, true);
					//Add the sensor data to the overall time map
					mlMainLogic.getProfileLog().adjustTimeMap(sdSensorData, true);					

					//notify the observers (in this case SensorPanel)					
				    setChanged();
				    notifyObservers(tempSensor);					
				}
			}
				
			//Temperature readings
			for(Sensor tempSensor : mlMainLogic.getProfileLog().getTemperatureSensors()) {
				//If the input name is the same as a temperature sensor, add data
				if(sSubName.equals(tempSensor.getName())) {
					//create a new SensorData object and fill it with data					
				    sdSensorData = new SensorData();
					sdSensorData.setSensorName(tempSensor.getName());
					Calendar cal = Calendar.getInstance();
			    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
					sdSensorData.setTimeStamp(sdf.format(cal.getTime())); //get system time
					//"nan" is a value Arduino sends when the temperature sensor sends no
					//data or null data
					if(s.substring(startValueOfData).equals("nan")) {
						sdSensorData.setValue(0);
					}
					else {
					    sdSensorData.setValue(Integer.parseInt(s.substring(startValueOfData)));
					}
					//Add the sensor data to the sensor
					tempSensor.adjustSensorData(sdSensorData, true);
					//Add the sensor data to the overall time map
					mlMainLogic.getProfileLog().adjustTimeMap(sdSensorData, true);
					
					//notify the observers (in this case SensorPanel)
				    setChanged();
				    notifyObservers(tempSensor);					
				}
			}			
		}
	}	
	

	/**
	 * The getSensor method compares the sensor name (a unique identifier
	 * among sensors, such as "W1" or "T2" etc.) against known sensors.  On a
	 * match, the sensor object is returned.
	 * This could potentially be optimised with local variables if the number of
	 * sensors actually begin to slow the program.
	 * 
	 * @param sSensorName : a string representing sensor name 
	 * @return : the Sensor with that number
	 */
	public Sensor getSensor(String sSensorName)
	{	
		//If the stop recording flag hasn't been set
		if(!mlMainLogic.getStopRecording()) {		
			
			//Pressure sensors - names start with 'W' followed by position number
			if(sSensorName.charAt(0) == 'W') {
				//For each pressure sensor, check for one matching sSensorName.
				//On a match, return it
				for(Sensor tempSensor : mlMainLogic.getProfileLog().getPressureSensors()) {
					if(tempSensor.getName().equals(sSensorName)) {
                        return tempSensor;
					}
				}
			}
			
			//Temperature sensors - names start with 'T' followed by position number
			if(sSensorName.charAt(0) == 'T') {
				//For each temperature sensor, check for one matching sSensorName.
				//On a match, return it
				for(Sensor tempSensor : mlMainLogic.getProfileLog().getTemperatureSensors()) {
					if(tempSensor.getName().equals(sSensorName)) {
                        return tempSensor;
					}
				}
			}			
		}
		
		return null;
	}	
}