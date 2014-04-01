package Model;

import Controller.DataGenerator;
import Controller.MainLogic;

/**
 * The ReplaySensor class is used to generate data using the DataGenerator
 * class.  It was the intention that data would be received by ReplaySensor
 * and relayed to SensorPanel, but at the moment, SensorPanel observes
 * DataGenerator and receives the data directly.
 * 
 * @authors: KB Group - Robin Osborne
 */
public class ReplaySensor
{	
	//local reference to the MainLogic object
	private static MainLogic mlMainLogic;

	//The data generator object, and its corresponding thread
	private DataGenerator dgDataGenerator;
	private Thread tGeneratorThread;
	
	/**
	 * Constructor for ReplaySensor.
	 * 
	 * @param mlMainLogic : a reference to the MainLogic object
	 * @param plProfileLog : the profile log to use for the replay
	 */
	public ReplaySensor(MainLogic mlMainLogic, ProfileLog plProfileLog)
	{		
		//initialise the connection to the MainLogic object
		ReplaySensor.mlMainLogic = mlMainLogic;
		
        //Initialise the DataGenerator object and Thread
		dgDataGenerator = new DataGenerator(mlMainLogic);		
		tGeneratorThread = new Thread(dgDataGenerator);  
	}	
	
	/**
	 * The getPressureGenerator method returns the DataGenerator object.
	 * 
	 * @return : the DataGenerator object
	 */
	public DataGenerator getDataGenerator() {
		return dgDataGenerator;
	}
	
	/**
	 * The startReplay method starts the thread for the data generator.
	 */
	public void startReplay() {
		//Start the data generator
		tGeneratorThread.start();		
	}	
	
	/**
	 * The reset method 'resets' data generation by creating a new
	 * data generator and associated thread.
	 */
	public void reset() {
		//Start a new data generator
        //Initialise the DataGenerator object and Thread
		dgDataGenerator = new DataGenerator(mlMainLogic);		
		tGeneratorThread = new Thread(dgDataGenerator);				
	}	

	/**
	 * The getSensor method compares the sensor name (a unique identifier
	 * among sensors, such as "W1" or "T2" etc.) against known sensors.  On a
	 * match, the sensor object is returned.
	 * 
	 * @param sSensorName : a string representing sensor name 
	 * @return : the Sensor with that number
	 *
	public Sensor getSensor(String sSensorName)
	{	
		if(!mlMainLogic.getStopPlaying(mlMainLogic.getProfileLog().getProfileId())) {		
			
			//Sensors
			for(String sName : mlMainLogic.getProfileLog().getSensorMap().keySet()) {
				if(sName.equals(sSensorName)) {
                    return mlMainLogic.getProfileLog().getSensorMap().get(sName);
				}
			}		
		}
		
		return null;
	}*/	
		
}
