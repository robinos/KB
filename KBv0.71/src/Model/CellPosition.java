package Model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The CellPosition class is used to hold grid position information, and is used by
 * the Sensor class.
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
public class CellPosition implements Cloneable
{
	//Instance variables with default
    private int iXPos = 0;
    private int iYPos = 0;
    
    /**
     * A no-parameter constructor
     */
    public CellPosition()
    {
    	super();
    }
    
	//Accessor methods
    /**
     * The getXPos method returns the stored x-position (column) as an integer.
     * 
     * @return : an integer value representing x-position
     */
    public int getXPos()
    {
    	return iXPos;
    } 
    
    /**
     * The getYPos method returns the stored y-position (row) as an integer.
     * 
     * @return : an integer value representing y-position
     */
    public int getYPos()
    {
    	return iYPos;
    }     
    
    /**
     * The setXPos method sets the x-position (column).
     * 
     * @param iXPos : the x-position as an integer
     */
    @XmlElement
    public void setXPos(int iXPos)
    {
    	this.iXPos = iXPos;
    }
    
    /**
     * The setYPos method sets the y-position (row).
     * 
     * @param iYPos : the y-position as an integer
     */
    @XmlElement
    public void setYPos(int iYPos)
    {
    	this.iYPos = iYPos;
    }    
    
    /**
     * The toString method for CellPosition.
     */
	public String toString()
	{
		StringBuffer sbBuffer = new StringBuffer(100);
		
		sbBuffer.append(" Cell Position ["+iXPos+","+iYPos+"] ");
		
		return sbBuffer.toString();
	}
	
	
    /**
     *  This is the clone method for CellPosition
     *  
     *  @return : a deep copy of the CellPosition object
     */
    @Override      
    public CellPosition clone()
    {
       try
       {
    	  //The super clone handles copies iXPos and iYPos
    	   CellPosition copy = (CellPosition)super.clone();
    	  
          return copy;
       }
       catch(CloneNotSupportedException e)
       {
          throw new InternalError();
       }
    }	
	
    
	/**
     *  An equals method for CellPosition
     *  
     *  @param oObj : the object for comparison 
     *  @return    : a boolean value true or false 
	 */
	public boolean equals(Object oObj)
	{
    	if(this == oObj) {
    		return true;  //The same object
    	}
    	if(!(oObj instanceof CellPosition)) {
    		return false;  //Not of the same type
    	}
    	
    	CellPosition cpOther = (CellPosition)oObj;
    	
    	//Compare the x and y integer locations of the CellPosition
    	return( ( cpOther.getXPos() == this.getXPos() ) &&
    			( cpOther.getYPos() == this.getYPos() ));
	}
	
	/**
     *  A basic hashCode method for CellPosition
     *  
     *  @return : an integer value
	 */
	public int hashCode()
	{
		int iResult = 37;
		
		iResult = iResult*37 + iXPos;
		iResult = iResult*37 + iYPos;				
		
	    return iResult;
	}	
}
