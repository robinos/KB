package Controller;

import java.util.Observable;
import java.util.Set;
import java.util.TreeMap;

import Model.SensorData;

/**
 * The DataGenerator class is used to generate data as read from the ProfileLog
 * class and updates any observers as if the data was live.  It has some
 * similarities with the ReadArduino class in this way, though the source of the
 * data is quite different.
 * Note that this class is implemented to provide basic information for a slider
 * bar to determine current playback time with start and stop, but there is no
 * graphical bar connected to this logic at this time.  An actual implementation
 * may require tweaking the code according to needs.
 * 
 * @authors: KB Group - Robin Osborne
 */
public class DataGenerator extends Observable implements Runnable 
{
	//For a slider bar to display time (not yet implemented graphically)
	private int iSliderTotalTime = 0;
	private int iSliderMaxTime = 0;
	
	//For the reference to the MainLogic object
	private static MainLogic mlMainLogic;
	
	public DataGenerator(MainLogic mlMainLogic)
	{
		DataGenerator.mlMainLogic = mlMainLogic;
	}
		        
	@Override
	public void run()
	{
		//While this profile is not set to stop playing
        while(!mlMainLogic.getStopPlaying(mlMainLogic.getProfileLog().getProfileId())) {
				
		    try {
		    	
		    	//System.out.println(mlMainLogic.getProfileLog().getTimeMap().toString());
		    	
		    	//Retrieve the time map for the current profile
		        TreeMap<String, SensorData> tmTimeMap
		    		= mlMainLogic.getProfileLog().getTimeMap();
		        
		        //Get the time map set to help iterate over it
		    	Set<String> sTimeMapSet = tmTimeMap.keySet();

				//A first time value used for determining the last value of the
				//slider bar
				String sFirstTime = tmTimeMap.firstKey();
				
				//Parse the last time and convert it to a numerical value
				int iSliderHours = Integer.parseInt(sFirstTime.substring(0,2));
				int iSliderMinutes = Integer.parseInt(sFirstTime.substring(3,5));
				int iSliderSeconds = Integer.parseInt(sFirstTime.substring(6,8));
				int iSliderMilliseconds = Integer.parseInt(sFirstTime.substring(9,12));
				//Set the reference total (the actual value of 0 on the slider)
				int iReferenceTotal = iSliderMilliseconds + iSliderSeconds*1000
						+ iSliderMinutes*60*1000 + iSliderHours*60*60*1000;				
				
				//A last time value used for determining the last value of the
				//slider bar
				String sLastTime = tmTimeMap.lastKey();
				
				//Parse the last time and convert it to a numerical value
				iSliderHours = Integer.parseInt(sLastTime.substring(0,2));
				iSliderMinutes = Integer.parseInt(sLastTime.substring(3,5));
				iSliderSeconds = Integer.parseInt(sLastTime.substring(6,8));
				iSliderMilliseconds = Integer.parseInt(sLastTime.substring(9,12));					
				iSliderMaxTime = iSliderMilliseconds + iSliderSeconds*1000
						+ iSliderMinutes*60*1000 + iSliderHours*60*60*1000;				
				
				//The slider max value is minus the starting reference value
				iSliderMaxTime -= iReferenceTotal;
				
				//For each time value in the time map set (in order since the time
				//map is a tree map)
				for(String sTime : sTimeMapSet) {
					//Another check that the stop playing flag has not been set
					if(!mlMainLogic.getStopPlaying(mlMainLogic.getProfileLog().getProfileId())) {
						
						//Read in the time as hours, minutes, and seconds
						int iReadHours = Integer.parseInt(sTime.substring(0,2));
						int iReadMinutes = Integer.parseInt(sTime.substring(3,5));
						int iReadSeconds = Integer.parseInt(sTime.substring(6,8));
						int iReadMilliseconds = Integer.parseInt(sTime.substring(9,12));
						int iReadTotal = iReadMilliseconds + iReadSeconds*1000
								+ iReadMinutes*60*1000 + iReadHours*60*60*1000; 
										
						//The total time is the read total minus the reference point
						int iTotalTime = iReadTotal - iReferenceTotal;		
						
						int iTimeDifference;
						
						//Wait for appropriate time to pass (before sending
						//notification to observers)
						if(iSliderTotalTime <= iTotalTime) {
							//The time difference from last point to this point
							iTimeDifference = iTotalTime - iSliderTotalTime;
							//Sleep for difference milliseconds
							long sleepTime = new Long(1*iTimeDifference);
							Thread.sleep(sleepTime);
											
							//Set a new slider position
							iSliderTotalTime = iTotalTime;
						}
						
						//Having the slider farther than the time is not
						//currently possible
						
						//Another check to make sure the stop flag hasn't been set
						if(!mlMainLogic.getStopPlaying(mlMainLogic.getProfileLog().getProfileId())) {
							//Set changed and notify observers (SensorPanel is this case)
							setChanged();
							notifyObservers(tmTimeMap.get(sTime));
						}
					}
				}
				//end outer for
				
				//All sensors done, end replay
				mlMainLogic.getSensorPanelLogic().stopPlaying(mlMainLogic.getProfileLog().getProfileId());
				
		    }
		    catch(InterruptedException ieEx) {
			
			}
        }
        //end while
	}
	//End run
	
	/**
	 * The getSliderMax method returns the maximum value of the replay time slider.
	 * 
	 * @return: an integer value representing max slider value
	 */
	public int getSliderMax()
	{
		return iSliderMaxTime;
	}
	
	/**
	 * The getSliderTotal method returns the current total of the replay time slider.
	 * 
	 * @return: an integer value representing current slider value
	 */
	public int getSliderTotal()
	{
		return iSliderTotalTime;
	}
	
}
