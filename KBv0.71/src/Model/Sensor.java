package Model;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Sensor class represents a Sensor with a cell position [1,1], a name
 * (eg. "W1" for weight 1, "T3" for temperature 3), a unique number for logic use
 * (corresponding with the name), and an array of SensorData.
 * 
 * Note that while the Cell Position is set, it isn't really used for anything at
 * the moment besides a nicer looking toString method.
 * The DataSensorTimeMap isn't used either at the moment, but could be implemented
 * for examining the data over time for one specific sensor (rather than filtering the
 * main time map in ProfileLog).
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
 *
 */
@XmlRootElement
public class Sensor implements Cloneable
{
	//Instance Variables with defaults
	private CellPosition cpCellPosition; //position in the grid
	private String sName = "Unknown"; //sensor name
	private int iNumber = 0; //sensor number
	private int iInitialValueForColor = 0; //default value for 0-state

	private ArrayList<SensorData> alSensorData; //array of sensor data objects
	private TreeMap<String, ArrayList<SensorData>> tmSensorDataTimeMap; //time map
	
    /**
     * A no-parameter constructor
     */
	public Sensor()
	{
		super();
	}	
	
	
	//Accessor methods
    /**
     * The getCellPosition method returns a CellPosition object representing
     * cell position.
     * 
     * @return : a CellPosition object
     */
    public CellPosition getCellPosition()
    {
    	if(cpCellPosition == null) cpCellPosition = new CellPosition();
    	return cpCellPosition;
    } 
    
    
    /**
     * The getName method returns the sensor name (eg. "W1" or "T3").
     * 
     * @return : a String name
     */
    public String getName()
    {
    	return sName;
    }    
    
    
    /**
     * The getNumber method returns the sensor numbering, used in logic.
     * 
     * @return : an integer number
     */
    public int getNumber()
    {
    	return iNumber;
    }    
    
    
    /**
     * The getInitialValueForColor method returns the base-line value for the sensor,
     * used in colour comparison.
     * 
     * @return : an integer representing a base-line sensor value for comparison 
     */
    public int getInitialValueForColor()
    {
    	return iInitialValueForColor;
    }    
    
    
    /**
     * The getSensorData method returns the array list of SensorData objects.
     * If it does not yet exist, an empty array is created and returned.
     * 
     * @return : an array list of sensor data objects
     */
    public ArrayList<SensorData> getSensorData()
    {
    	if(alSensorData == null) alSensorData = new ArrayList<SensorData>();
    	return alSensorData;
    }     
    
    
    /**
     * The getSensorDataTimeMap method returns a tree map of SensorData object
     * ordered by a String representing time as HH:MM:SS:SSS.  This is meant to aid
     * replay reconstruction of data over time.
     * 
     * @return : a tree map of SensorData objects ordered by string time HH:MM:SS:SSS
     */
    public TreeMap<String,ArrayList<SensorData>> getSensorDataTimeMap()
    {
    	if(tmSensorDataTimeMap == null) tmSensorDataTimeMap = new TreeMap<String,ArrayList<SensorData>>();
    	return tmSensorDataTimeMap;
    }    
	
    
	//Manipulator methods
    
    /**
     * The setCellPosition method sets cell position with a CellPosition
     * object.
     * 
     * @param cpCellPosition : a CellPosition object
     */
    @XmlElement
    public void setCellPosition(CellPosition cpCellPosition)
    {
    	this.cpCellPosition = cpCellPosition;
    }
    
    
    /**
     * This setCellPosition method takes an x and y position as
     * integers and constructs a CellPosition object.  It is made for
     * test data construction, and is not used for the xml code.
     * 
     * @param iXPos : an integer representing x-position in a grid
     * @param iYPos : an integer representing y-position in a grid
     */
    public void setCellPosition(int iXPos, int iYPos)
    {
    	cpCellPosition = new CellPosition();
    	cpCellPosition.setXPos(iXPos);
    	cpCellPosition.setYPos(iYPos);    	
    }    
    
    
    /**
     * The setName method sets a name for the sensor (ex. "W1")
     * 
     * @param sName : a String name
     */
    @XmlElement
    public void setName(String sName)
    {
    	this.sName = sName;
    }    
    
    
    /**
     * The setNumber method sets a unique number for the sensor.
     * 
     * @param iNumber : an integer unique number
     */
    @XmlElement
    public void setNumber(int iNumber)
    {
    	this.iNumber = iNumber;
    }        

    /**
     * The setInitialValueForColor method sets the base-line value for the sensor,
     * used in colour comparison.
     * 
     * @param : an integer representing a base-line sensor value for comparison 
     */
    @XmlElement
    public void setInitialValueForColor(int iInitialValueForColor)
    {
    	this.iInitialValueForColor = iInitialValueForColor;
    }        
    
    /**
     * The setSensorData method sets the sensor data array.
     * 
     * @param alSensorData : an array list of SensorData objects
     */
    @XmlElement
    public void setSensorData(ArrayList<SensorData> alSensorData)
    {
    	this.alSensorData = alSensorData;
    	
    	for(SensorData sdSensorData : alSensorData) {
    		adjustSensorDataTimeMap(sdSensorData, true);
    	}
    }    
    
    
    /**
     * The adjustSensorData method adds or removes a SensorData object from
     * the array.   
     * 
     * @param sdSensorData : a SensorData object
     * @param bAdd : a boolean value, true for adding, false for removing 
     */
    public void adjustSensorData(SensorData sdSensorData, boolean bAdd)
    {
    	if(alSensorData == null) alSensorData = new ArrayList<SensorData>();
    	
    	if(sdSensorData != null) {
    		//If adding
	    	if(bAdd) {
	    		//If the SensorData object doesn't already exist, add it
	    		if(!alSensorData.contains(sdSensorData)) {
		    		alSensorData.add(sdSensorData);
		    		adjustSensorDataTimeMap(sdSensorData, true);
	    		}
	    	}
	    	//If removing
	    	else {
	    		//If the SensorData object exists, remove it
	    		if(alSensorData.contains(sdSensorData)) {
	    			alSensorData.remove(sdSensorData);
		    		adjustSensorDataTimeMap(sdSensorData, false);	    			
	    		}
	    	}
    	}
    }    
    
    
    /**
     * The adjustSensorDataTimeMap allows adding or removing sensor data from the
     * map, given a particular Sensor object and a boolean true (to add) or false
     * (to remove).
     * This time map isn't actually used with current logic, but could be to
     * track a particular sensor over time.
     * 
     * @param psPressureSensor : a Sensor object
     * @param bAdd : a boolean value, true to add, false to remove
     */
    public void adjustSensorDataTimeMap(SensorData sdSensorData, boolean bAdd)
    {
    	//initialise tree map if not already initialised
    	if(tmSensorDataTimeMap == null) tmSensorDataTimeMap = new TreeMap<String, ArrayList<SensorData>>();
    	
    	ArrayList<SensorData> tempList;
    	
    	if(sdSensorData != null) {
    		//If adding
	    	if(bAdd) {
	    		//If the entry already exists, add the SensorData object to the array list
	    		//of the time map
	    		if(tmSensorDataTimeMap.containsKey(sdSensorData.getTimeStamp())) {
	    			tempList = tmSensorDataTimeMap.get(sdSensorData.getTimeStamp());
	    			tempList.add(sdSensorData);
	    			tmSensorDataTimeMap.put(sdSensorData.getTimeStamp(), tempList);
	    		}
	    		//If the entry doesn't already exists, create it
	    		else {
	    			tempList = new ArrayList<SensorData>();
	    			tempList.add(sdSensorData);
	    			tmSensorDataTimeMap.put(sdSensorData.getTimeStamp(), tempList);
	    		}
	    	}
	    	//If removing
	    	else {
	    		//If the entry exists
	    		if(tmSensorDataTimeMap.containsKey(sdSensorData.getTimeStamp())) {
	    	        tempList = tmSensorDataTimeMap.get(sdSensorData.getTimeStamp());
	    	        //If the array list of the time map contains the object, remove it
	    	        if(tempList.contains(sdSensorData)) {
	    	        	tempList.remove(sdSensorData);
	    	        	tmSensorDataTimeMap.put(sdSensorData.getTimeStamp(), tempList);
	    	        }
	    	        
	    		}
	    	}
    	}    	
    }
    
	
    //Other methods
    /**
     * A toString method.
     */
	public String toString()
	{
		StringBuffer sbBuffer = new StringBuffer(100);
		
		if(sName != null)
		    sbBuffer.append(sName+": ");		
		
		if(cpCellPosition != null)
		    sbBuffer.append(cpCellPosition.toString());
		
		sbBuffer.append(", Sensor Type: "+iNumber);
		
		sbBuffer.append(", Base-line value: "+iInitialValueForColor+", ");
		
		//Display data for that sensor
		if(alSensorData != null) {
			for(SensorData sdSensorData : alSensorData) {
				if(sdSensorData != null)
		            sbBuffer.append(sdSensorData.toString());
			}
		}
		 
		//the time map is not included in the toString
		
		return sbBuffer.toString();
	} 
	
    /**
     *  This is the clone method for Sensor
     *  
     *  @return : a deep copy of the Sensor object
     */
    @Override      
    public Sensor clone()
    {
       try
       {
    	  //The super method should copy the primitive int iNumber
    	   Sensor copy = (Sensor)super.clone();
    	  
    	   copy.cpCellPosition = cpCellPosition.clone();    	  
           copy.sName = new String(sName);
    	   
           copy.alSensorData = new ArrayList<SensorData>();
           
           //copy clones of the array list data
           for(SensorData sdSensorData : alSensorData) {
        	   copy.alSensorData.add(sdSensorData.clone());
           }
           
           copy.tmSensorDataTimeMap = new TreeMap<String, ArrayList<SensorData>>();   	  

           Set<String> tsKeySet = tmSensorDataTimeMap.keySet();
           
           //copy clones of the tree map data
           for(String sTime : tsKeySet) {
               ArrayList<SensorData> alTempSensorData = new ArrayList<SensorData>();
        	   ArrayList<SensorData> alTemp = tmSensorDataTimeMap.get(sTime);
        	   
        	   if(alTemp != null) {
        	       for(SensorData sdSensorData : alTemp) {
        	    	   alTempSensorData.add(sdSensorData.clone());
        	       }
        	   }
        	   
        	   copy.tmSensorDataTimeMap.put(sTime, alTempSensorData);
           }           
          
          return copy;
       }
       catch(CloneNotSupportedException e)
       {
          throw new InternalError();
       }
    }
    
	/**
     *  An equals method for Sensor
     *  
     *  @param oObj : the object for comparison 
     *  @return    : a boolean value true or false 
	 */
	public boolean equals(Object oObj)
	{
    	if(this == oObj) {
    		return true;  //The same object
    	}
    	if(!(oObj instanceof Sensor)) {
    		return false;  //Not of the same type
    	}
    	
    	Sensor sOther = (Sensor)oObj;
    	
    	//Compare the name, number and array list of data
    	return( ( sOther.sName.equals(this.sName) ) &&
    			( sOther.iNumber == this.iNumber ) &&
    			( sOther.alSensorData.equals(this.alSensorData) ) );
	}
	
	/**
     *  A basic hashCode method for Sensor
     *  
     *  @return : an integer value
	 */
	public int hashCode()
	{
		int iResult = 37;
		
		iResult = iResult*37 + iNumber;
		iResult = iResult*sName.hashCode();				
		iResult = iResult*alSensorData.hashCode();
		
	    return iResult;
	}	
}
