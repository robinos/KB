package Model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * SensorData represents a certain Value at a certain point in time.
 * It is used by the Sensor class to hold data.
 * 
 * Since this is used with the xml reader, there should either NOT be a constructor,
 * or there should only be a non-parameter constructor.  It also cannot inherit from
 * another class.
 * The get and set methods should never be removed (unless the variable is removed),
 * and if the get method is renamed, the set method must also be renamed accordingly.
 * 
 * Note: Use of this class with the XmlReader and XmlWriter classes requires use of
 * the jdom external jar file.
 * 
 * @author: KB Group - Robin Osborne
 */
@XmlRootElement
public class SensorData implements Cloneable
{
	//Instance Variables with defaults
	private String sSensorName = "";
	private String sTimeStamp = "00:00:00:000"; //time stamp
	private double dValue = 0.0d; //sensor value
	
    /**
     * A no-parameter constructor
     */
	public SensorData()
	{
		super();
	}	
	
	//Accessor methods
    /**
     * The name of the sensor associated with this sensor data (ie. W1 for
     * pressure sensor 1, T2 for temperature sensor 2). 
     * 
     * @return : a String name for the sensor
     */
    public String getSensorName()
    {
    	return sSensorName;
    }	
	
    /**
     * The getTimeStamp method returns the time stamp for this sensor data.
     * 
     * @return : a String representing time as HH:MM:SS:SSS
     */
    public String getTimeStamp()
    {
    	return sTimeStamp;
    }    
    
    /**
     * The getValue method returns the sensor value.
     * 
     * @return : a double value
     */
    public double getValue()
    {
    	return dValue;
    }    
    
    
	//Manipulator methods        
    /**
     * The setTimeStamp method sets the time stamp string to represent
     * the moment the sensor reading was taken.
     * 
     * @param sTimeStamp : a String representing time as HH:MM:SS:SSS
     */
    @XmlElement
    public void setTimeStamp(String sTimeStamp)
    {
    	this.sTimeStamp = sTimeStamp;
    }    
    
    
    /**
     * The setValue method sets the sensor data value. 
     * 
     * @param dValue : a double value
     */
    @XmlElement
    public void setValue(double dValue)
    {
    	this.dValue = dValue;
    }    
    
    
    /**
     * The name of the sensor associated with this sensor data (ie. W1 for
     * pressure sensor 1, T2 for temperature sensor 2). 
     * 
     * @return : a String name for the sensor
     */
    @XmlElement
    public void setSensorName(String sSensorName)
    {
    	this.sSensorName = sSensorName;
    }    
    
    
    //Other methods
    /**
     * The toString method for SensorData.
     */
	public String toString()
	{
		StringBuffer sbBuffer = new StringBuffer(100);

		sbBuffer.append(", From Sensor: "+sSensorName);
		sbBuffer.append(", Time Stamp: "+sTimeStamp);		
		sbBuffer.append(", Value: "+dValue);
		
		return sbBuffer.toString();
	}   
	
    /**
     *  This is the clone method for SensorData
     *  
     *  @return : a deep copy of the SensorData object
     */
    @Override      
    public SensorData clone()
    {
       try
       {
    	  //The super method should copy the primitive double dValue
    	  SensorData copy = (SensorData)super.clone();

    	  copy.sSensorName = new String(sSensorName);    	  
    	  
    	  copy.sTimeStamp = new String(sTimeStamp);    	  
    	  
          return copy;
       }
       catch(CloneNotSupportedException e)
       {
          throw new InternalError();
       }
    }	
	
    
	/**
     *  An equals method for SensorData
     *  
     *  @param oObj : the object for comparison 
     *  @return    : a boolean value true or false 
	 */
	public boolean equals(Object oObj)
	{
    	if(this == oObj) {
    		return true;  //The same object
    	}
    	if(!(oObj instanceof SensorData)) {
    		return false;  //Not of the same type
    	}
    	
    	SensorData sdOther = (SensorData)oObj;
    	
    	//Compare the String sSensorName, double dValue and String sTimeStamp
    	//in SensorData
    	return( ( sdOther.sSensorName == this.sSensorName ) &&
    			( sdOther.dValue == this.dValue ) &&
    			( sdOther.sTimeStamp.equals(this.sTimeStamp) ) );
	}
	
	/**
     *  A basic hashCode method for SensorData.
     *  
     *  @return : an integer value
	 */
	public int hashCode()
	{
		int iResult = 37;
		
		iResult = iResult*sSensorName.hashCode();
		iResult = iResult*sTimeStamp.hashCode();				
		iResult = iResult*(new Double(dValue)).hashCode();
		
	    return iResult;
	}	
}
