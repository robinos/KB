package View.Dialogs;



import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.HashMap;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;


import Controller.IOConfig;
import Model.Lang;
import View.Main.Main;

import javax.swing.JPanel;;


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
 * ConfigDialog - extends JDialog, implements Observer and ActionListener 
 * 
 * The configuration dialog, here the user will be able to change some of the 
 * static configuration values that are used on application startup.
 * One exception is language that will be updated immediately, thanks to the 
 * language solution(See Lang class).
 * 
 * This class is an self-made variant of a singleton, meaning only one instance 
 * of this object can exist.
 * 
 * This class has an exception from the language distribution solution, has three cases 
 * when an observer must be added.
 * 
 * 
 * @author Stefan Arvidsson
 *
 */
public class ConfigDialog extends JDialog implements Observer, ActionListener{

	private static ConfigDialog instance = null;  // part of singleton pattern
	
	// Declare dialog frame component
	private String dialogTitle = new String();
	// The supported languages
	private static HashMap<String, Locale> langMap = supportedLang(); 
	private  JComboBox<String> langBox = supportedLang(supportedLang()); // Declare dialog components	
	

	private JLabel selectLanguageLabel = new JLabel();
	private JCheckBox fullScreenCheckBox = new JCheckBox();
	
	private JButton setDefaultValues = new JButton();
	private JButton confirmButton = new JButton();
	
	
	// An adapter for the configuration dialog shutdown(X-button)
	private static final WindowAdapter w = new WindowAdapter(){
		public void windowClosing(WindowEvent e) { 
			if(((JDialog)e.getSource()).isModal()){
				(IOConfig.getInstance()).writeToProp("Configured", "false");
				System.exit(0);
				
			}else{
				instance.dispose();
			}
		}
	};
	
	/**
	 * ConfigDialog - a private constructor 
	 * 
	 * @param frame - a JFrame, will always be null
	 * @param modal - a boolean to set if this 
	 */
	private ConfigDialog(JFrame frame, boolean modal){
		super(frame,modal);
		
		setTitle(dialogTitle);

		// set components
		langBox.addActionListener(this);
		
		// get initial values from IOConfig
		try {
			langBox.setSelectedItem( (String) IOConfig.getInstance().getTypedValue("Language"));
			fullScreenCheckBox.setSelected((Boolean) IOConfig.getInstance().getTypedValue("FullScreen"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		fullScreenCheckBox.addActionListener(this);
		
		// add components
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.add(add(selectLanguageLabel), BorderLayout.WEST);
		contentPanel.add(Box.createRigidArea(new Dimension(0,5)));
		contentPanel.add(langBox);
		contentPanel.add(Box.createRigidArea(new Dimension(0,5)));
		contentPanel.add(fullScreenCheckBox);
		
		// Set buttons
		setDefaultValues.addActionListener(this);
		confirmButton.addActionListener(this);
		
		// add buttons
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(setDefaultValues);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(confirmButton);
		
		add(contentPanel, BorderLayout.NORTH);
		add(buttonPanel, BorderLayout.PAGE_END);
		//
		//					|
		// UNFINSHED WORK   v

		init();
//		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//		
//		int x = (dim.width - getWidth()) / 2; // X-position
//		int y = (dim.height - getHeight()) / 2; // Y-position		
//		
//		setBounds(x, y, getWidth(), getHeight());	
		//setUndecorated(false);	
//		setResizable(false);
//		setAlwaysOnTop(true);
		
		
		
	}
	
	
	
	/**
	 * init - part of the language solution, set component text here!
	 */
	private void init(){
		setTitle(Lang.rb.getString("con.title"));
		selectLanguageLabel.setText(Lang.rb.getString("con.jlabellang"));
		fullScreenCheckBox.setText(Lang.rb.getString("con.jcheckboxfullscreen"));
		setDefaultValues.setText(Lang.rb.getString("con.jbuttonsetDefault"));
		confirmButton.setText(Lang.rb.getString("con.jbuttonconfirm"));
		pack();
	}
	
	
	
	/**
	 * supportedLang - Generates the key (String) and value (Locale) to be
	 * used for initializing and updating the language.
	 * 
	 * The key (String) is the value to be set for the key 'Language' in config.properties,
	 * 
	 * The value (Locale) is the used with the Lang class update(Locale lang) method.
	 * 
	 * 
	 * @return HashMap<String, Locale> - The supported languages 
	 */
	private static HashMap<String, Locale> supportedLang(){
		HashMap<String, Locale> temp = new HashMap<String, Locale>();
		temp.put("English (US)", Locale.US);
		temp.put("Svenska (SV)", new Locale("sv","SE"));
		return temp;
	}
	/**
	 * supportedLang - 
	 * 
	 * Uses supportedLang() (without parameters) to construct the JComboBox, to
	 * make sure that this method does not effect the HashMap 'langMap'.
	 * The JComboBox is used to select languages.
	 * 
	 * @param languages - HashMap<String, Locale> retrieved from the private method
	 * 					  supportedLang().
	 * 
	 * @return JComboBox<String> - the supported languages, sorted
	 */
	private JComboBox<String> supportedLang(HashMap<String, Locale> languages){
		// Get sort String[] for langBox
		 JComboBox<String> temp = new JComboBox<String>();
		String[] lang = new String[languages.size()];
		int i = 0;
		for(String s:languages.keySet()){
			lang[i] = s ;
			i++;
		}
		java.util.Arrays.sort(lang,String.CASE_INSENSITIVE_ORDER);
		for(String s:lang)
			temp.addItem(s);
		return temp;
	}
	
	
	
	
	/**
	 * getInstance - A static synchronized method that is part of the singleton pattern.
	 * 
	 * 	If the application has not been launched before, then "Configured" in config.properties
	 *  should be set to false. 
	 * 	Handles four cases: 
	 *  Instance null and not configured: makes the configuration dialog modal
	 *  Instance null and configured: makes the configuration dialog not modal
	 *  Instance not null and configured: if modal set the instance to a new non modal dialog
	 *  Instance not null and not configured: should not happen, means corruption 				 
	 * 
	 * Note: it loads the content of the instance but does not set it visible unless it is
	 * 		 the first time the application execute.
	 * 
	 * @return ConfigDialog the instance of this JDialog 
	 */
	public static synchronized ConfigDialog getInstance() {
		boolean configured = false;
		try {
			configured = (Boolean) (IOConfig.getInstance()).getTypedValue("Configured");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Must set the value of the key 'Configured' (in config.properties) " +
										"to either true or false before launching the application", e);
		}
		// At start application not executed before
		if ( instance == null && !configured){
			// null and not configured 		
			// This first initialize IOConfig and then Lang (recursively...)


			(IOConfig.getInstance()).writeToProp("Configured", "true");
			(IOConfig.getInstance()).loadProp();
			instance = new ConfigDialog(null, true);
			getInstanceLangInit();
			instance.addWindowListener(w);
			instance.setVisible(true);
			// At start application executed before
		}else if(instance == null && configured){
			// null and configured - initialize it
			instance = new ConfigDialog(null, false);
			
			getInstanceLangInit();
			
			instance.dispose();
		}else if(instance != null && configured){
			// not null and configured
			if(instance.isModal()){
				instance = new ConfigDialog(null, false);	
				getInstanceLangInit();
			}	
			// not modal means has the correct instance
		}else{
			
			// not null and not configured means something has gone wrong
			throw new RuntimeException("The key 'Configured' (in config.properties) is " +
										"corrupted during runtime, should be true");
		}
		return instance;
	}
	
	/**
	 * getInstanceLangInit - is a support method for getInstance that 
	 * adds this instance as an observer and initialize IOConfig and Lang.
	 * 
	 * Is part of the language solution
	 */
	private static void getInstanceLangInit(){
		
		Lang.getInstance().addObserver(instance); // adds this instance as an observer of Lang (and rb) 
		try {
			// This first initialize IOConfig and then Lang (recursively...)
			Lang.getInstance().update((Locale)langMap.get(IOConfig.getInstance().getTypedValue("Language")));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	


	
	/**
	 * update - Overrides the update method in Observer. 
	 * 
	 * Is part of the language solution.
	 * ->
	 * Observes Lang (arg0) and gets the notification about Lang.rb arg1
	 * 	
	 * @param  arg0 - an Observable class that is observed by this class
	 * @param  arg1 - an Object that has been updated and passed on to be observed by this class
	 * 
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		if(arg0 instanceof Lang){
			init();
		}

	}




	/**
	 * actionPerformed - Overrides the action performed method in ActionListener.
	 * 
	 * @param a - the ActionEvent from a component in ConfigDialog (that has triggered the event). 
	 */
	@Override
	public void actionPerformed(ActionEvent a) {
		// TODO Auto-generated method stub
		Object src = a.getSource();

		if(src.equals(langBox)){
			JComboBox<?> cb = (JComboBox<?>)a.getSource();
			String lang = (String)cb.getSelectedItem();
			
			IOConfig.getInstance().writeToProp("Language", lang); // writes lang
			
			try {
				Lang.getInstance().update((Locale)langMap.get(IOConfig.getInstance().getTypedValue("Language")));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(src.equals(fullScreenCheckBox)){
			if(fullScreenCheckBox.isSelected()){
			// set when application start go full screen	
				IOConfig.getInstance().writeToProp("FullScreen", "true"); 
			}else{
				IOConfig.getInstance().writeToProp("FullScreen", "false"); 
			}
		}else if(src.equals(setDefaultValues)){
			IOConfig.getInstance().setDefaultStartConfig();
			// Set start lang
			IOConfig.getInstance().loadProp();

			try {
				// Loads the default language
				Lang.getInstance().update((Locale)langMap.get(IOConfig.getInstance().getTypedValue("Language"))); 
				
				
				langBox.setSelectedItem((String) IOConfig.getInstance().getTypedValue("Language"));
				fullScreenCheckBox.setSelected((Boolean) IOConfig.getInstance().getTypedValue("FullScreen"));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			

		}else if(src.equals(confirmButton)){
			IOConfig.getInstance().loadProp();
			dispose();
		}
		
		
	}	
		
	
	
	
}