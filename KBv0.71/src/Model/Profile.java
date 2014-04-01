package Model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
* The Profile class is meant to hold information important for a particular profile
* session - that is, the profile id, along with any information about the patient
* and the operation.
* At the moment, it holds a unique Profile id (meant to be Operation1245Johnson or
* similar).
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
* @author: KB Group
*/
@XmlRootElement
public class Profile implements Cloneable
{
	//The id of the Profile.  The name "default" is used when none is given.
	private String sId = "default";	
	
    /**
     * A no-parameter constructor
     */
	public Profile()
	{
		super();
	}
	
	//Accessor methods
	/**
	 * The getId method returns the Profile id.
	 * 
	 * @return : a String representing profile id
	 */
	public String getId()
	{
		return sId;
	}	
	
	
	//Manipulator methods
	/**
	 * The setId method sets the Profile sId to the given String.
	 * 
	 * @param sId : a String representing profile sId (used as filename)
	 */
	@XmlElement
	public void setId(String sId)
	{
		this.sId = sId; 
	}	
	
	//Other methods
    /**
     * The toString method for Profile. 
     */
	public String toString()
	{
		StringBuffer sbBuffer = new StringBuffer(100);
		
		sbBuffer.append(", "+sId+" ");
		
		return sbBuffer.toString();
	}
	
    /**
     *  This is the clone method for Profile
     *  
     *  @return : a deep copy of the Profile object
     */
    @Override      
    public Profile clone()
    {
       try
       {
    	  //The super method should copy the primitive double dValue
    	   Profile copy = (Profile)super.clone();
    	  
    	  copy.sId = new String(sId);    	  
    	  
          return copy;
       }
       catch(CloneNotSupportedException e)
       {
          throw new InternalError();
       }
    }	
	
    
	/**
     *  An equals method for Profile
     *  
     *  @param oObj : the object for comparison 
     *  @return    : a boolean value true or false 
	 */
	public boolean equals(Object oObj)
	{
    	if(this == oObj) {
    		return true;  //The same object
    	}
    	if(!(oObj instanceof Profile)) {
    		return false;  //Not of the same type
    	}
    	
    	Profile pOther = (Profile)oObj;
    	
    	//Compare the String sID in Profile
    	return( pOther.sId.equals(this.sId) );
	}
	
	/**
     *  A basic hashCode method for Profile
     *  
     *  @return : an integer value
	 */
	public int hashCode()
	{
		int iResult = 37;
		
		iResult = iResult*sId.hashCode();				
		
	    return iResult;
	}	

}