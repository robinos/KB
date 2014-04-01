package Model;

import java.util.Locale;
import java.util.Observable;
import java.util.ResourceBundle;

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
 * Lang - extends Observable, is a singleton, meaning only one instance of this
 * object can exist. This class is the source for the String distribution of the 
 * application. Meaning it supplies the visible text.
 * 
 * It works like this:
 * 
 * The class that needs to display text, ex: A needs a init method that sets the 
 * text to the components. It gets the text from this instance, key is a String.
 * 
 * | private void init(){
 *  // ex    
 *  aComponent.setText(Lang.rb.getString(key));
 * } |
 * 
 * When this is done make A implement Observer and override the 
 * update method.
 * 
 * | public void update(Observable arg0, Object arg1) {
 *  	if(arg0 instanceof Lang){
 *  		init();
 *  	}
 *  } |
 *  
 * and adds itself as an observer,(tip do this in the constructor): 
 * 
 * | Lang.getInstance().addObserver(instanceOfObjectToObserv); // this | 
 * 
 * Tip: call the init() method in the class constructor.
 * 
 * NOTE: It is only the ConfigDialog instance that should use the update method of Lang.
 * 
 * @author Stefan Arvidsson
 *
 */
public class Lang extends Observable {
	
	private static final String RESOURCE_LOCATION ="Languages.lang"; // the language source path

	private static Lang instance = null; // part of singleton pattern
	
	public static ResourceBundle rb; // Contains the languages
	/**
	 * Lang() - is a completely empty private constructor to match the singleton pattern.
	 */
	private Lang(){}// part of singleton pattern
	
	/**
	 * getInstance - part of singleton pattern, returns the only instance of Lang.
	 * 
	 * @return Lang - the only instance of lang
	 */
	public static synchronized Lang getInstance() { // part of singleton pattern
		if ( instance == null ){
			instance = new Lang();
		}
		return instance;
	}
	
	/**
	 * update - Sets the public static resource bundle (Lang.rb) 
	 * to contain the new language, sets Lang to changed and then 
	 * notifies the observers that Lang.rb has been changed. 
	 *  
	 * |NOTE: It should only be the configuration dialog (ConfigDialog class) that
	 * uses this method, even if it is accessible for others. Why? 
	 * ConfigDialog also updates the configuration property file (config.properties)
	 * with its write method and load IOConfig.prop with the key that determines the
	 * language. 
	 * 
	 * @param lang - is a Locale, with the language to be used. 
	 */
	 public void update(Locale lang){
		
		 rb = ResourceBundle.getBundle(RESOURCE_LOCATION, lang);
//		 ResourceBundle.clearCache(); // clears the   RESOURCE_LOCATION from rb might be useful much later
		setChanged();
		notifyObservers(rb);

		
	}
	

}
