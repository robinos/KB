package Model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Message class consists of a String message and a String timestamp.  This allows
 * messages  under a session to be replayed at the times they occurred.
 * It is meant to be used with log messages and the status bar, though it is not
 * currently fully implemented.
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
public class Message implements Cloneable
{
	//instance variables
	private String sMessage;
	private String sTimeStamp;
	
    /**
     * A no-parameter constructor
     */
	public Message()
	{
		super();
	}
	
	//Accessor methods
	/**
	 * The getMessage method returns the stored String message.
	 * 
	 * @return: a String representing a message
	 */
	public String getMessage()
	{
		return sMessage;
	}
	
	/**
	 * The getTimeStamp method returns the timestamp of the message in
	 * hour, minute, second, millisecond format.
	 * 
	 * @return: a String representing a timestamp in format HH:MM:SS:SSS.
	 */
	public String getTimeStamp()
	{
		return sTimeStamp;
	}
	
	//Manipulator methods
	/**
	 * The setMessage methods sets the stored message.
	 * 
	 * @param sMessage : a String representing a message
	 */
	public void setMessage(String sMessage)
	{
		this.sMessage = sMessage;
	}
	
	/**
	 * The setTimeStamp method sets the time stamp of the message in
	 * hour, minute, second, millisecond format.
	 * 
	 * @param sTimeStamp: a String representing a timestamp in format HH:MM:SS:SSS
	 */
	public void setTimeStamp(String sTimeStamp)
	{
		this.sTimeStamp = sTimeStamp;
	}	
	
	//Other methods
    /**
     * The toString method for Message.
     */
	public String toString()
	{
		StringBuffer sbBuffer = new StringBuffer(100);
		
		sbBuffer.append(" (Time: "+sTimeStamp);
		sbBuffer.append(", Message: "+sMessage+") ");		
		
		return sbBuffer.toString();
	} 	
	
    /**
     *  This is the clone method for Message
     *  
     *  @return : a deep copy of the Message object
     */
    @Override      
    public Message clone()
    {
       try
       {
    	  Message copy = (Message)super.clone();
    	  
    	  copy.sMessage = new String(sMessage);
    	  copy.sTimeStamp = new String(sTimeStamp);
    	  
          return copy;
       }
       catch(CloneNotSupportedException e)
       {
          throw new InternalError();
       }
    }	
	
    
	/**
     *  An equals method for Message
     *  
     *  @param oObj : the object for comparison 
     *  @return    : a boolean value true or false 
	 */
	public boolean equals(Object oObj)
	{
    	if(this == oObj) {
    		return true;  //The same object
    	}
    	if(!(oObj instanceof Message)) {
    		return false;  //Not of the same type
    	}
    	
    	Message mOther = (Message)oObj;
    	
    	//Compare the Strings sMessage and sTimeStamp in Message
    	return( ( mOther.sMessage == this.sMessage ) &&
    			( mOther.sTimeStamp == this.sTimeStamp ));
	}
	
	/**
     *  A basic hashCode method for Message
     *  
     *  @return : an integer value
	 */
	public int hashCode()
	{
		int iResult = 37;
		
		iResult = iResult*sMessage.hashCode();
		iResult = iResult*sTimeStamp.hashCode();				
		
	    return iResult;
	}	
}
