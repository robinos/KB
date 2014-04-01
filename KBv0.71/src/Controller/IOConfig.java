package Controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Properties;


/**
 *  Copyright [2013] [Stefan Arvidsson]
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *      
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


/**
 * IOConfig // could do-> - extends Observable and notifies the observers that the property (prop) has been loaded/updated.
 *   
 * This class Provides the static default values at the start of the application.
 * This class is a singleton, meaning only one instance of this object can exist.
 * 
 * NOTE: Only the ConfigDialog may use the writeToProp, loadProp and setDefaultStartConfig.
 * ConfigDialog also sets the language and it is to keep everything synchronized.
 * 
 * ALLOWED: It is allowed for all instances to use the getTypedValue method
 * Example, key is a String: IOConfig.getInstance().getTypedValue(key); | 
 * Be careful of using getTypedValue since this method can throw many different errors if 
 * implemented incorrect.
 * 
 * 
 * ::::::::::::::::::::::::::::::
 * When adding a static default value, add it in the methods setDefaultStartConfig and getTypedValue.
 * In setDefaultStartConfig you write a String with the default value. In getTypedValue be certain that
 * it is the correct type and is valid where it has been retrieved.
 * 
 * |||
 * 
 * This class 
 * 
 * @author Stefan Arvidsson
 *
 */
public class IOConfig {
	
	private static final String CONFIG_PATH = "src/config.properties"; // the configuration source path
	
	private static IOConfig instance = null; // part of singleton pattern
	
	
	private Properties prop = new Properties();  // has the properties but to retrieve them use getTypedValue method
	/**
	 * IOConfig() -  is an private constructor to match the singleton pattern. 
	 * It calls the loads method which loads the data into this instance's 
	 * Properties instance.  
	 */
	private IOConfig(){loadProp();}

	/**
	 * getInstance - part of singleton pattern, returns the only instance of IOConfig.
	 * 
	 * @return IOConfig - the only instance of IOConfig
	 */
	public static synchronized IOConfig getInstance() {
		if ( instance == null ) {		
			instance = new IOConfig();
		}
		return instance;
	}
	
	
	//*****************************************************************************
	//************** - Read Section - *********************************************	
	//*****************************************************************************	
		/**
		 * loadProp - loads from the config.properties file to the property to 
		 * "private Properties prop;" 
		 * 
		 * if the value of the key was either "" or non existent in getTypedValue, 
		 * it will attempt to load the default value.
		 * 
		 *  Note: Make sure that values that do not have a default value exist in the
		 *  config.properties file! Also, this method cannot determine if the key is
		 *  correctly spelled etc. and therefore it will be loaded anyway.
		 *  
		 *  
		 *  This method notifies the observers that the property (prop) has been loaded/updated
		 */
		public void loadProp() {
			try {
				prop.load(new FileInputStream(CONFIG_PATH));
				// check if it exist? load property
				for (String key : prop.stringPropertyNames()) {

					if ((((String) prop.get(key)).length() == 0)
							|| getTypedValue(key) == null) {
						if (setDefaultStartConfig(key)) {
							loadProp();
							break;
						}else{
							// the default value was not found?
						}
					}
					
					
				}
				
			} catch (IOException ex) {
				ex.printStackTrace();
			}catch (Exception e){
				e.printStackTrace();
			}
			
			// When done if setDefaultStartConfig was called it will recurse 
			// back before truly exiting this method
			

		}

		
		
		/**
		 * setDefaultConfig- Resets all default configurations values.
		 * Used by the ConfigDialog button "setDefaultValues" button.
		 * NOTE:  
		 * It iterates through the "prop" keys and set them individually.
		 * It needs that the value to be set default is made so in the 
		 * setDefaultStartConfig(String key) method.
		 *
		 */
		public void setDefaultStartConfig() {
			for (String key : prop.stringPropertyNames()) {
				setDefaultStartConfig(key);
			}
		}

		
		/**
		 * setDefaultStartConfig - Uses a string key to write the default values to
		 * config.properties.
		 *  
		 *  |NOTE: If the default value is not added, it will return false.|
		 *    
		 *  Make sure that keys in config.properties that do not have a default value
		 *  are set to the correct values from start, here is a list of them:
		 *  |"Configured" -> boolean|,
		 * 
		 * 
		 * @param key - a String with the key to set the default value for.
		 * @return boolean - true if it managed to restore the default value
		 */
		private boolean setDefaultStartConfig(String key) {
			if (key != null) {
				switch (key) {
					case "Language":
						// set lang
						writeToProp(key, "English (US)");
						break;
					case "FullScreen":
						writeToProp(key, "false");
						break;
					case "FrameScaleAtStartX":
						writeToProp(key, "3");
						break;
					case "FrameScaleAtStartY":
						writeToProp(key, "3");
						break;
					case "Configured":
						// Must be true or false at start will only be set true once has therefore no default value
						return false; // break;
					default: 
						;
						return false;
				}
			}
			
			return true;
		}
		/**
		 * getTypedValue - Returns an object that is defined by what the key is set to return.
		 * In other words it returns an item depending on what the key is. 
		 * |
		 * NOTE: If the key is not defined here or the method fails to form the object to 
		 * return it will throw an exception. Other errors that might occur are on the 
		 * receiver side. Make sure that everything is correct in the config.properties file
		 * and default value is managed. 
		 * 
		 * 
		 * 
		 * @param key - a String, with the property key
		 * @return Object - the typed property value, make sure to cast it correctly.
		 * @throws Exception - if there were no such value it will throw a null pointer
		 * exception, or if something else goes wrong with formatting the return object 
		 * (could be many things) it will throw a general exception
		 */
		public Object getTypedValue(String key) throws Exception {
			try {
				switch (key) {
				case "Language":
					return prop.getProperty(key);
				case "FullScreen":
					return new Boolean(prop.getProperty(key));
				case "FrameScaleAtStartX":
					return new Integer(prop.getProperty(key));
				case "FrameScaleAtStartY":
					return new Integer(prop.getProperty(key));
				case "Configured":
					return new Boolean(prop.getProperty(key));
				default:
					break;
				}
			} catch (Exception e) {
				throw new Exception(e+", tip check getTypedValue in the IOConfig class");			
			}
			throw new NullPointerException(
				key + 
				": Retrived No Value, tip: check if it is added in getTypedValue in the IOConfig class");
		}

//**********************************************************************************************
//************** - Write Section - *************************************************************
//**********************************************************************************************

		
		/**
		 * writeToProp - Does not add if the key does not exist, but be careful to 
		 * write configurations during run time. If the value is wrong the application 
		 * will most certainly crash sooner or later.
		 * If the value is null, the value is set to the default value, if there is any.
		 * 
		 * @param key - a String with the property key
		 * @param value - a String with the property value.
		 */
		public void writeToProp(String key, String value) {
			if (!prop.containsKey(key)){
				return;
			}else if(value == null){ // this section is untested
				if(setDefaultStartConfig(key)){
					loadProp();
					return;
				}
			}else{
				try {
					prop.setProperty(key, value);
					prop.store(new FileOutputStream(CONFIG_PATH), null);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}
		}
		

}