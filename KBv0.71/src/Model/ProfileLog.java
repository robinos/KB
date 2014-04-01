package Model;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ProfileLog holds two arrays of Sensor objects as data for an operation session,
 * one of pressure sensors and one of temperature sensors.  It also holds an array
 * of Message objects for Log History (important session events), a reference to the
 * profile id for the Profile used with this ProfileLog, and the filename of the
 * picture used with it. They can be written to file, or read back into ProfileLog for
 * manipulation in a program.
 * There are three additional tree maps for data manipulation.  The SensorMap is a
 * map of all Sensor objects keyed by sensor name, the TimeMap is a map of all
 * SensorData objects keyed by time stamp String, and the LogHistoryTimeMap is a map
 * of all messages keyed by time stamp String.
 * 
 **IMPORTANT NOTE: It does not work to use the TreeMaps directly for reading in data
 **in the current implementation, because this slows the handling of data to a crawl.
 **Using the array lists instead allows the data to be handled quickly, and so far
 **avoids the need for additional Thread objects.
 * 
 * Since this is used with the xml reader, there should either NOT be a constructor,
 * or there should only be a non-parameter constructor.  It also cannot inherit from
 * another class.
 * The get and set methods should never be removed (unless the variable is removed),
 * and if the get method is renamed, the set method must also be renamed accordingly.
 * 
 * Note: Use of this class with the XmlReader and XmlWriter classes requires used of
 * the jdom external jar file.
 * 
 * @author: KB Group - Robin Osborne
 */
@XmlRootElement
public class ProfileLog implements Cloneable
{
	//Instance Variables
	//XML elements
    private ArrayList<Sensor> alPressureSensors; 
    private ArrayList<Sensor> alTemperatureSensors;
    //add log history    
    private ArrayList<Message> alLogHistory;
    
    //Other variables
    private String sProfileId = ""; //The ProfileId associated with this log
    private String sPicture = ""; //The picture for this profile log
    
    //A tree map of Sensors, keyed by sensor names
    private TreeMap<String, Sensor> tmSensorMap;
    
    //A time map for all sensor data
    private TreeMap<String, SensorData> tmTimeMap;
    
    //A Tree map of array lists of Log messages, sorted by time stamp
    private TreeMap<String, Message> tmLogHistoryTimeMap;
    
    /**
     * A no-parameter constructor
     */
	public ProfileLog()
	{
		super();
	}
	
	//Accessor methods    
    /**
     * The getProfileId method returns the profile Id associated with this
     * ProfileLog.  If for some reason there is none (which should not happen)
     * it will use default.
     * 
     * @return : a String representing profile id
     */
    public String getProfileId()
    {
    	if(sProfileId == null) sProfileId = new String("default");
    	return sProfileId;
    }    
    
    
	/**
	 * The getPicture method return the filename of the picture related to the
	 * ProfileLog.
	 * 
	 * @return : a String representing picture filename
	 */
	public String getPicture()
	{
		if(sPicture == null) sPicture = new String("");
		return sPicture;
	}
	
	
    /**
     * The getPressureSensors method returns the array list of PressureSensors.
     * 
     * @return : an array list of pressure Sensor objects
     */
    public ArrayList<Sensor> getPressureSensors()
    {
    	if(alPressureSensors == null) alPressureSensors = new ArrayList<Sensor>();
    	return alPressureSensors;
    }
 
    
    /**
     * The getTemperature method returns the array of Temperature sensors.
     * 
     * @return : an array list of temperature Sensor objects
     */
    public ArrayList<Sensor> getTemperatureSensors()
    {
    	if(alTemperatureSensors == null) alTemperatureSensors = new ArrayList<Sensor>();
    	return alTemperatureSensors;
    }       

    
    /**
     * The getLogHistory method returns the array of Message objects.
     * For important messages (such as extreme sensor values) that have occurred
     * during a session.
     * This is not currently implemented.
     * 
     * @return : an array list of Message objects
     */
    public ArrayList<Message> getLogHistory()
    {
    	if(alLogHistory == null) alLogHistory = new ArrayList<Message>();
    	return alLogHistory;
    }    
    
    
    /**
     * The getSensorMap method returns the tree map of all sensors, ordered by
     * name.
     * 
     * @return : a tree map of Sensor objects
     */
    public TreeMap<String, Sensor> getSensorMap()
    {
    	if(tmSensorMap == null) tmSensorMap = new TreeMap<String, Sensor>();
    	return tmSensorMap;
    }    
    
    
    /**
     * The getTimeMap method returns the tree map of all sensors data, ordered by
     * time stamp HH:MM:SS:SSS.
     * 
     * @return : a tree map of Sensor objects
     */
    public TreeMap<String, SensorData> getTimeMap()
    {
    	if(tmTimeMap == null) tmTimeMap = new TreeMap<String, SensorData>();
    	return tmTimeMap;
    }
    

    /**
     * The getLogHistoryTimeMap method returns the tree map of Message objects, ordered by
     * time stamp HH:MM:SS:SSS.
     * 
     * @return : a tree map of MEssage objects
     */
    public TreeMap<String, Message> getLogHistoryTimeMap()
    {
    	if(tmLogHistoryTimeMap == null) tmLogHistoryTimeMap = new TreeMap<String, Message>();
    	return tmLogHistoryTimeMap;
    }     
    
    
	//Manipulator methods     
    /**
     * The setProfileId method sets the profile Id associated with this
     * ProfileLog.
     * 
     * @param sProfileId : a String representing profile id
     */
    @XmlElement	
    public void setProfileId(String sProfileId)
    {
    	this.sProfileId = sProfileId;
    }    
  
    
	/**
	 * The setPicture method sets the filename of the picture related to the Profile.
	 *  
	 * @param sPicture : a String representing picture filename
	 */
	@XmlElement
	public void setPicture(String sPicture)
	{
		this.sPicture = sPicture; 
	}
    
	
    /**
     * The setPressure method sets the array list of Pressure Sensors.
     * *It will also be used to order given sensors into the pressure
     * time map.
     * 
     * @param alPressure : the array list of pressure sensors
     */
    @XmlElement
    public void setPressureSensors(ArrayList<Sensor> alPressureSensors)
    {
    	this.alPressureSensors = alPressureSensors;
    	
    	for(Sensor sSensor : alPressureSensors) {
    		//add to the name-sorted sensor map
    		adjustSensorMap(sSensor, true);
    		
    		ArrayList<SensorData> alData = sSensor.getSensorData();
    		
    		//Add the data to the total time map
    		if(alData != null) {
	    		for(SensorData sdData : alData) {
	    			adjustTimeMap(sdData, true);
	    		}
    		}
    		
    	}
    	
    }    
    
    
    /**
     * The adjustPressure method adjusts the array list of pressure sensors
     * by adding or removing a sensor.
     * *It will also be used to add or remove that sensor to/from the time
     * map.
     * 
     * @param psPressureSensor : a Sensor object, representing a pressure sensor
     * @param bAdd : true to add, false to remove
     */
    public void adjustPressureSensors(Sensor psPressureSensor, boolean bAdd)
    {
    	if(alPressureSensors == null) alPressureSensors = new ArrayList<Sensor>();
    	
    	if(psPressureSensor != null) {
    		//If adding a sensor
	    	if(bAdd) {
	    		//If the sensor doesn't already exist, add it
	    		if(!alPressureSensors.contains(psPressureSensor)) {
		    		alPressureSensors.add(psPressureSensor);
		    		adjustSensorMap(psPressureSensor, true);
		    		
		    		ArrayList<SensorData> alData = psPressureSensor.getSensorData();
		    		
		    		//Add the data to the total time map
		    		if(alData != null) {
			    		for(SensorData sdData : alData) {
			    			adjustTimeMap(sdData, true);
			    		}
		    		}
	    		}
	    	}
	    	//If removing a sensor
	    	else {
	    		//If the sensor exists, remove it
	    		if(alPressureSensors.contains(psPressureSensor)) {
	    			alPressureSensors.remove(psPressureSensor);
		    		adjustSensorMap(psPressureSensor, false);
		    		
		    		ArrayList<SensorData> alData = psPressureSensor.getSensorData();
		    		
		    		//Add the data to the total time map
		    		if(alData != null) {
			    		for(SensorData sdData : alData) {
			    			adjustTimeMap(sdData, false);
			    		}
		    		}		    		
	    		}
	    	}
    	}
    }   
    
    
    /**
     * The setTemperatureSensors methods sets the array list of temperature sensors.
     * *It will also be used to add temperature sensors to the time map.
     * 
     * @param alTemperatureSensors : an array list of Sensor object, representing
     *                               temperature sensors.
     */
    @XmlElement
    public void setTemperatureSensors(ArrayList<Sensor> alTemperatureSensors)
    {
    	this.alTemperatureSensors = alTemperatureSensors;
    	
    	for(Sensor sSensor : alTemperatureSensors) {
    	    //add to the name-sorted sensor map 
    		adjustSensorMap(sSensor, true);       
    		
    		ArrayList<SensorData> alData = sSensor.getSensorData();
    		
    		//Add the data to the total time map
    		if(alData != null) {
	    		for(SensorData sdData : alData) {
	    			adjustTimeMap(sdData, true);
	    		}
    		}    		
    	}    	
    }    
    
    
    /**
     * The adjustTemperatureSensors method allows adding or removing a Sensor object
     * from the array list of temperature sensor objects.
     * 
     * @param tsTemperatureSensor : a Sensor object, representing a temperature sensor
     * @param bAdd : true to add, false to remove
     */
    public void adjustTemperatureSensors(Sensor tsTemperatureSensor, boolean bAdd)
	{
		if(alTemperatureSensors == null) alTemperatureSensors = new ArrayList<Sensor>();
		
		if(tsTemperatureSensor != null) {
			//If adding
	    	if(bAdd) {
	    		//If the sensor doesn't already exist, add it
	    		if(!alTemperatureSensors.contains(tsTemperatureSensor)) {
	    			alTemperatureSensors.add(tsTemperatureSensor);
		    		adjustSensorMap(tsTemperatureSensor, true);
		    		
		    		ArrayList<SensorData> alData = tsTemperatureSensor.getSensorData();
		    		
		    		//Add the data to the total time map
		    		if(alData != null) {
			    		for(SensorData sdData : alData) {
			    			adjustTimeMap(sdData, true);
			    		}
		    		}
	    		}
	    	}
	    	//If removing
	    	else {
	    		//If the sensor exists, remove it
	    		if(alTemperatureSensors.contains(tsTemperatureSensor)) {
	    			alTemperatureSensors.remove(tsTemperatureSensor);
		    		adjustSensorMap(tsTemperatureSensor, false);
		    		
		    		ArrayList<SensorData> alData = tsTemperatureSensor.getSensorData();
		    		
		    		//Add the data to the total time map
		    		if(alData != null) {
			    		for(SensorData sdData : alData) {
			    			adjustTimeMap(sdData, false);
			    		}
		    		}		    		
	    		}
	    	}
		}
	}                  

    
    /**
     * The setSensorMap method sets the tree map of all sensors, ordered by
     * name.
     * 
     * @param tmSensorMap : a tree map of Sensor objects ordered by String name
     */
    @XmlElement	        
    public void setSensorMap(TreeMap<String, Sensor> tmSensorMap)
    {
    	if(tmSensorMap == null) tmSensorMap = new TreeMap<String, Sensor>();
    	this.tmSensorMap = tmSensorMap;
    }
    
    
    /**
     * The adjustSensorMap method adds or removes a Sensor from the tree map of
     * Sensors, ordered by name.
     * 
     * @param sensor : the Sensor object
     * @param bAdd : true to add, false to remove
     */
    public void adjustSensorMap(Sensor sensor, boolean bAdd)
    {
    	if(tmSensorMap == null) tmSensorMap = new TreeMap<String, Sensor>();  
    	
    	if(sensor != null) {
    		if(bAdd) {
    			//add or overwrite sensor
	    		tmSensorMap.put(sensor.getName(), sensor);
    		}
    		else {
    			//remove only if the key exists
    			if(tmSensorMap.containsKey(sensor.getName()))    			
    				tmSensorMap.remove(sensor.getName());    			
    		}
    	}
    }     
    
    
    /**
     * The setTimeMap method sets the tree map of all sensors data, ordered by
     * time stamp HH:MM:SS:SSS.
     * 
     * @param tmTimeMap : a tree map of Sensor objects, ordered by String timestamps
     */
    @XmlElement	    
    public void setTimeMap(TreeMap<String, SensorData> tmTimeMap)
    {
    	if(tmTimeMap == null) tmTimeMap = new TreeMap<String, SensorData>();
    	this.tmTimeMap = tmTimeMap;
    }
    
    
    /**
     * The adjustTimeMap allows adding or removing SensorData object from the
     * time map, given a particular SensorDAta object and a boolean true
     * (to add) or false (to remove).
     * 
     * @param sdSensorData : a SensorData object
     * @param bAdd : a boolean value, true to add, false to remove
     */
    public void adjustTimeMap(SensorData sdSensorData, boolean bAdd)
    {
    	//initialise tree map if not already initialised
    	if(tmTimeMap == null) tmTimeMap =
    			new TreeMap<String, SensorData>();
    	
    	if(sdSensorData != null) {
    		//If adding, add or overwrite
	    	if(bAdd) {
	    		tmTimeMap.put(sdSensorData.getTimeStamp(), sdSensorData);
	    	}
	    	//If removing
	    	else {
	    		//If it exists, remove it
	    	    if(tmTimeMap.containsKey(sdSensorData.getTimeStamp())) {
	    	    	tmTimeMap.remove(sdSensorData.getTimeStamp());
	    	    }
	    	}
    	}
    }    
    
    
    /**
     * The setLogHistory method sets the array of Message objects.
     * For application messages (such as File saved.) that have occured during a session.
     * 
     * @param alLogHistory : an array list of Message objects
     */
    @XmlElement
    public void setLogHistory(ArrayList<Message> alLogHistory)
    {
    	this.alLogHistory = alLogHistory;
    	
    	//Add to the LogHistoryTimeMap
    	for(Message mMessage : alLogHistory) {
    		adjustLogHistoryTimeMap(mMessage, true);           
    	}    	
    }    
    
    
    /**
     * The adjustLogHistory method adjusts the array list of messages
     * by adding or removing a message.
     * 
     * @param mMessage : a Message object, representing a time-stamped String
     * message
     * @param bAdd : true to add, false to remove
     */
    public void adjustLogHistory(Message mMessage, boolean bAdd)
    {
    	if(alLogHistory == null) alLogHistory = new ArrayList<Message>();
    	
    	if(mMessage != null) {
    		//If adding
	    	if(bAdd) {
	    		//Add or overwrite the message
		    	alLogHistory.add(mMessage);	
		    	adjustLogHistoryTimeMap(mMessage, true);
	    	}
	    	//If removing
	    	else {
	    		//If it exists, remove it
	    		if(alLogHistory.contains(mMessage)) {
	    			alLogHistory.remove(mMessage);	
		    		adjustLogHistoryTimeMap(mMessage, false);
	    		}
	    	}
    	}
    }    
    
    
    /**
     * The setLogHistoryTimeMap method sets the tree map of Message objects, ordered by
     * time stamp HH:MM:SS:SSS.
     * 
     * @param tmLogHistoryTimeMap: a tree map of Message objects
     */
    @XmlElement    
    public void setLogHistoryTimeMap(TreeMap<String, Message> tmLogHistoryTimeMap)
    {
    	if(tmLogHistoryTimeMap == null) tmLogHistoryTimeMap = new TreeMap<String, Message>();
    	this.tmLogHistoryTimeMap = tmLogHistoryTimeMap;
    }    
    
    /**
     * The adjustLogHistoryTimeMap allows adding or removing message data from the
     * log history time map, given a particular Message object and a boolean true
     * (to add) or false (to remove).
     * 
     * @param mMessage : a Message object
     * @param bAdd : a boolean value, true to add, false to remove
     */
    public void adjustLogHistoryTimeMap(Message mMessage, boolean bAdd)
    {
    	//initialise tree map if not already initialised
    	if(tmLogHistoryTimeMap == null) tmLogHistoryTimeMap =
    			new TreeMap<String, Message>();
    	
    	if(mMessage != null) {
    		//If adding, add or overwrite
	    	if(bAdd) {
	    		tmLogHistoryTimeMap.put(mMessage.getTimeStamp(), mMessage);
	    	}
	    	//If removing
	    	else {
	    		//If it exists, remove it
	    	    if(tmLogHistoryTimeMap.containsKey(mMessage.getTimeStamp())) {
	    	    	tmLogHistoryTimeMap.remove(mMessage.getTimeStamp());
	    	    }
	    	}
    	}    	
    }                          
    
    
    //Other methods
    /**
     * The toString method for ProfileLog.
     */
	public String toString()
	{
		StringBuffer sbBuffer = new StringBuffer(100);
		
		if(alPressureSensors != null) {
		    for(Sensor sSensor : alPressureSensors) {			    	
				sbBuffer.append(sSensor.toString() + ", ");			
		    }
		}
		else {
			sbBuffer.append("No values for pressure. ");
		}
		
		if(alTemperatureSensors != null) {	
		    for(Sensor sSensor : alTemperatureSensors) {			    	
				sbBuffer.append(sSensor.toString() + " ");			
		    }			
		}
		else {
			sbBuffer.append("No values for temperature. ");
		}
		
		if(tmSensorMap != null) {
		    for(String sName : tmSensorMap.keySet()) {
		    	Sensor sSensor = tmSensorMap.get(sName);
		    	if(sSensor != null) {
					sbBuffer.append(sSensor.toString() + ", ");
		    	}
		    }
		}
		else {
			sbBuffer.append("No sensor map values. ");
		}
		
		if(tmTimeMap != null) {
		    for(String sTime : tmTimeMap.keySet()) {
		        sbBuffer.append(tmTimeMap.get(sTime).toString() + ", ");
		    }
		}
		else {
			sbBuffer.append("No time map values. ");
		}		
		
		if(alLogHistory != null) {	
		    for(Message mMessage : alLogHistory) {			    	
				sbBuffer.append(mMessage.toString() + " ");			
		    }			
		}
		else {
			sbBuffer.append("No log history. ");
		}		
		
		return sbBuffer.toString();
	}  
	
    /**
     *  This is the clone method for ProfileLog.
     *  
     *  @return : a deep copy of the ProfileLog object
     */
    @Override      
    public ProfileLog clone()
    {
       try
       {
    	  //The super method should copy the primitive int iNumber
    	   ProfileLog copy = (ProfileLog)super.clone();
    	   
    	   //copy the profile id String
    	   copy.sProfileId = new String(sProfileId);
    	   
    	   //copy all the array lists
    	   
    	   //copy alPressureSensors
           copy.alPressureSensors = new ArrayList<Sensor>();
           
           //copy clones of the array list data
           for(Sensor sSensor : alPressureSensors) {
        	   copy.alPressureSensors.add(sSensor.clone());
           }

    	   //copy alTemperatureSensors           
           copy.alTemperatureSensors = new ArrayList<Sensor>();
           
           //copy clones of the array list data
           for(Sensor sSensor : alTemperatureSensors) {
        	   copy.alTemperatureSensors.add(sSensor.clone());
           }
           
    	   //copy alLogHistory
           copy.alLogHistory = new ArrayList<Message>();
           
           //copy clones of the array list data
           for(Message mMessage : alLogHistory) {
        	   copy.alLogHistory.add(mMessage.clone());
           }           
           
           //copy all the tree maps          
           
           //The Sensor Map
           copy.tmSensorMap = new TreeMap<String, Sensor>();   	  

           Set<String> tsNameKeySet = tmSensorMap.keySet();

           for(String sSensorName : tsNameKeySet) {
        	   Sensor sSensor = tmSensorMap.get(sSensorName);
        	   
        	   if(sSensor != null) copy.tmSensorMap.put(sSensorName, sSensor);
           }                      
           
           //The Time Map
           copy.tmTimeMap = new TreeMap<String, SensorData>();   	  

           Set<String> tsTimeKeySet = tmTimeMap.keySet();
           
           for(String sTime : tsTimeKeySet) {
        	   copy.tmTimeMap.put(new String(sTime), tmTimeMap.get(sTime).clone());                      
           }
           
           //The Log History Time Map   	 
           copy.tmLogHistoryTimeMap = new TreeMap<String, Message>();   	  

           tsTimeKeySet = tmLogHistoryTimeMap.keySet();
           
           for(String sTime : tsTimeKeySet) {
        	   copy.tmLogHistoryTimeMap.put(new String(sTime), tmLogHistoryTimeMap.get(sTime).clone());                      
           }           
           
          //Return the copy 
          return copy;
       }
       catch(CloneNotSupportedException e)
       {
          throw new InternalError();
       }
    }
    
    
	/**
     *  An equals method for ProfileLog.  Since profile id is unique, it is
     *  used for the equals method.
     *  
     *  @param oObj : the object for comparison 
     *  @return    : a boolean value true or false 
	 */
	public boolean equals(Object oObj)
	{
    	if(this == oObj) {
    		return true;  //The same object
    	}
    	if(!(oObj instanceof ProfileLog)) {
    		return false;  //Not of the same type
    	}
    	
    	ProfileLog pOther = (ProfileLog)oObj;
    	
    	//Compare the profile ids
    	return( pOther.sProfileId.equals(this.sProfileId) );
	}
	
	
	/**
     *  A basic hashCode method for ProfileLog using the profile id also
     *  used in the equals method.
     *  
     *  @return : an integer value
	 */
	public int hashCode()
	{
		int iResult = 37;
		
		iResult = iResult*sProfileId.hashCode();
		
	    return iResult;
	}	
}
