package Model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Profiles holds an array list with a list of all previously created profiles.  It
 * is meant for keeping track of valid profile for loading purposes and can be
 * adjusted using the adjustProfileList method to add or remove a Profile object.
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
public class Profiles implements Cloneable
{
	//The array list of Profile objects
	private ArrayList<Profile> alProfiles = new ArrayList<Profile>();
	
    /**
     * A no-parameter constructor
     */
	public Profiles()
	{
		super();
	}	
	
	//Accessor Methods
	/**
	 * The getProfileList method is used by the xml reader to read a list of profiles
	 * from the ProfileList xml file.
	 * 
	 * @return : the array list of Profile objects
	 */
	public ArrayList<Profile> getProfileList()
	{
	    return alProfiles;	
	}
	
	
	//Manipulator methods
	/**
	 * The setProfileList method is used by the xml writer to write a list of profiles
	 * to the ProfileList xml file.
	 * 
	 * @param alProfiles : an arraylist of Profile objects
	 */
	@XmlElement
	public void setProfileList(ArrayList<Profile> alProfiles)
	{
		this.alProfiles = alProfiles;	
	}
	
    /**
     * The adjustProfileList method is used to add or remove a Profile object
     * from the array list.  This is used to add (or import) new Profile objects,
     * and to delete unwanted Profile objects.
     * 
     * @param pProfile : a Profile object 
     * @param bAdd : true to add, false to remove
     */
    public void adjustProfileList(Profile pProfile, boolean bAdd)
    {
    	if(alProfiles == null) alProfiles = new ArrayList<Profile>();
    	
    	if(pProfile != null) {
    		//If adding
	    	if(bAdd) {
	    		//If it doesn't already exist, add it
	    		if(!alProfiles.contains(pProfile)) {	    		
	    			alProfiles.add(pProfile);
	    		}
	    	}
	    	//If removing
	    	else {
	    		//If it exists, remove it
	    		if(alProfiles.contains(pProfile)) {
	    			alProfiles.remove(pProfile);	    			
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
		
		//Display data for that sensor
		if(alProfiles != null) {
			for(Profile pProfile : alProfiles) {
				if(pProfile != null)
		            sbBuffer.append(pProfile.toString()+", ");
			}
		}
		
		return sbBuffer.toString();
	}    
    
    /**
     *  This is the clone method for Profiles
     *  
     *  @return : a deep copy of the Profiles object
     */
    @Override      
    public Profiles clone()
    {
       try
       {
    	  //The super method should copy the primitive int iNumber
    	   Profiles copy = (Profiles)super.clone();
           
    	   copy.alProfiles = new ArrayList<Profile>();
    	   
           //copy clones of the array list data
           for(Profile pProfile : alProfiles) {
        	   copy.alProfiles.add(pProfile.clone());
           }           
          
          return copy;
       }
       catch(CloneNotSupportedException e)
       {
          throw new InternalError();
       }
    }
    
	/**
     *  An equals method for Profiles
     *  
     *  @param oObj : the object for comparison 
     *  @return    : a boolean value true or false 
	 */
	public boolean equals(Object oObj)
	{
    	if(this == oObj) {
    		return true;  //The same object
    	}
    	if(!(oObj instanceof Profiles)) {
    		return false;  //Not of the same type
    	}
    	
    	Profiles pOther = (Profiles)oObj;
    	
    	//Compare the name, number and array list of data
    	return( pOther.alProfiles.equals(this.alProfiles) );
	}
	
	/**
     *  A basic hashCode method for Profiles
     *  
     *  @return : an integer value
	 */
	public int hashCode()
	{
		int iResult = 37;
					
		iResult = iResult*alProfiles.hashCode();
		
	    return iResult;
	}	
    
}
