package View.Main;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import Controller.IOConfig;
import Controller.MainLogic;
import Controller.MenuFactory;
import Model.Lang;
import Model.Profile;
import Model.ProfileLog;
import Model.Profiles;
import View.MainPanel;
import View.Dialogs.ConfigDialog;


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
 * Main - 
 * 
 * @author Stefan Arvidsson
 *
 */
public class Main implements Observer {

	private static JFrame f = new JFrame();
	private static JFrame fs = new JFrame();
	
	private static MainPanel mainPanel;
	// Position the window at center of screen
	// Get the size of the screen

	private static MainLogic mlMainLogic;
	
	// Used to correct the position of the decorating scaling panel
	private ComponentAdapter scalingPanelAdapter = new ComponentAdapter(){
        public void componentResized(ComponentEvent e) {
        	// to get desired placement behavior
        	mainPanel.correctScalingPanel();
        }
	};
	
	//Must exist until somewhere at top level for saving/loading
	//ugly temporary solution!
	//static public Profiles prProfiles;
	//static public String sCurrentProfile = "default";
    //static public ProfileLog plProfileLog;	
	
	
	public Main(MainLogic mlMainLogic) {
		
		//Set the MainLogic object
		Main.mlMainLogic = mlMainLogic;
		
		Lang.getInstance().addObserver(this); // order matter here 
		ConfigDialog.getInstance(); // Initialize the Lang and IOConfig (sends an update to the observer)
		
		mainPanel = MainPanel.getInstance(mlMainLogic);
		

		
		//					|
		// UNFINSHED WORK   v
		
		
		// f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // use this on
		// message dialog
		
		
		// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); give do you want to exit whithout saving?
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fs.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int scaleX, scaleY;

		try{

			// init f
			scaleX = (int) IOConfig.getInstance().getTypedValue("FrameScaleAtStartX");
			scaleY = (int) IOConfig.getInstance().getTypedValue("FrameScaleAtStartY");
			// window size
			int fw = (dim.width / scaleX) * 2; // Width
			int fh = (dim.height / scaleY) * 2; // Height
			// window position
			int x = (dim.width - fw) / 2; // X-position
			int y = (dim.height - fh) / 2; // Y-position		
			
			f.setBounds(x, y, fw, fh);
			//f.setMinimumSize(minimumSize);
			fs.setExtendedState(JFrame.MAXIMIZED_BOTH); 
			fs.setUndecorated(true);
			
			// init full screen
			f.add(mainPanel);
			if( (boolean) IOConfig.getInstance().getTypedValue("FullScreen")){ 
				setFullScreen();
			}else{
				setWindowed();
			}
			
		}catch(Exception e){System.out.println(e);}
				// Note be careful when using getTypedValue
		f.getRootPane().addComponentListener(scalingPanelAdapter);
		fs.getRootPane().addComponentListener(scalingPanelAdapter);
	}
	
	private static void init(){
		if(fullscreen){
			fs.setJMenuBar(MenuFactory.buildMenu(mlMainLogic));
			fs.revalidate();
		}else{
			f.setTitle(Lang.rb.getString("windowFrame.title"));	
			f.setJMenuBar(MenuFactory.buildMenu(mlMainLogic)); 	
			f.revalidate();
		}
	}
		
	/**
	 * The getMainLogic method returns the MainLogic object, holding overeaching
	 * variables and logic for the application.
	 * 
	 * @return : the MainLogic object for the application
	 */
	public MainLogic getMainLogic()
	{
		return mlMainLogic;
	}
	
	
	public static boolean fullscreen = initFullScreen();
	
	private static boolean initFullScreen(){
		boolean b = false; 
		
		try {
			b= (Boolean) IOConfig.getInstance().getTypedValue("FullScreen");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return b;
	}
	

	public static void setFullScreen(){
		fullscreen = true;
		f.dispose();
		fs.add(mainPanel);
		fs.setJMenuBar(MenuFactory.buildMenu(mlMainLogic));
		fs.setVisible(true);
		fs.revalidate();
		mainPanel.correctScalingPanel();
	}
	
	public static void setWindowed(){
		fullscreen = false;
		fs.dispose();
		f.add(mainPanel);
		f.setJMenuBar(MenuFactory.buildMenu(mlMainLogic));
		f.setVisible(true);	
		f.revalidate();
		mainPanel.correctScalingPanel();
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
	
	public void exitOperation(){
		
		// check if there is anything unsaved, if there is launch saveandexit dialog
		// else shut down....
		
	}
}

