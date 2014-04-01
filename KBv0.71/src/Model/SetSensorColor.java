package Model;
import java.awt.Color;

/**
 * A helper class
 * Are used for set setting the correct color depending on what value a read 
 * from the sensor after a reference value are set.
 * The methods getSumOfAllSensors and getSumOfAllSensorsAtTime are meant to be
 * used to account for weight due to shifting of the body instead of new weight
 * being applied.  These are unfortunately not implemented elsewhere in the code
 * at this time.
 * 
 * @author Kirurgisk brits
 *
 */
public class SetSensorColor {
	
	//private int initialValue = 0;  //the initial value when a session starts	
	private int sumOfAllSensorValues = 0;
	
	private int lowLimitValue = -200;
	private int highLimitValue = 200;
	
	public SetSensorColor () {}
	
	/**
	 * Get the color for coloring the sensor panel depending
	 * on the value from the sensor.
	 * 
	 * @param sensorValue, initialValue
	 * @return null or Color
	 */
	public Color getcolorForSensorPanel (int sensorValue, int initialValue ) {
		if (sensorValue <= initialValue) {
			return ColorUtil.GtoBColor(Math.abs(initialValue - sensorValue));
		}else if(sensorValue > initialValue) {
			return ColorUtil.GtoRColor(Math.abs(sensorValue - initialValue));
		}else {
			return null;
		}
	}
	

	/**
	 * @return the sumOfAllSensors
	 */
	public int getSumOfAllSensors() {
		return sumOfAllSensorValues;
	}

	/**
	 * @param sumOfAllSensors the sumOfAllSensors to set
	 */
	public void setSumOfAllSensorsOnSensorAtTime(int valueOfOneSensor) {
		this.sumOfAllSensorValues = this.sumOfAllSensorValues + valueOfOneSensor;
	}	
	
	/**
	 * Not in use yet
	 * 
	 * @param lowLimitValue
	 * @param highLimitValue
	 */
	public SetSensorColor (int lowLimitValue, int highLimitValue) {
		this.lowLimitValue = lowLimitValue;
		this.highLimitValue = highLimitValue;
	}

	public int getLowLimitValue() {
		return lowLimitValue;
	}

	public void setLowLimitValue(int lowLimitValue) {
		this.lowLimitValue = lowLimitValue;
	}

	public int getHighLimitValue() {
		return highLimitValue;
	}

	public void setHighLimitValue(int highLimitValue) {
		this.highLimitValue = highLimitValue;
	}
	
}
