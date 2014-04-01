package View;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import Controller.MainLogic;
import Model.Lang;
import Model.ProfileLog;

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
 * MainPanel -
 * 
 * @author Stefan Arvidsson
 *
 */
public class MainPanel extends JPanel implements Observer {

	//required when extending JPanel
    private static final long serialVersionUID = 1L;	
	
	private static MainPanel instance = null; // part of singleton pattern
	
	private static MainLogic mlMainLogic;
	
	private static DecoratingPanel scalingPanel = new DecoratingPanel();
	
	
	/**
	 * MainPanel() - is a private constructor to match the singleton pattern.
	 */
	private MainPanel(MainLogic mlMainLogic){
		MainPanel.mlMainLogic = mlMainLogic;
		Lang.getInstance().addObserver(this);
		setLayout(new BorderLayout());
		

		
		add(new SensorMenu(mlMainLogic,scalingPanel), BorderLayout.NORTH);
		add(new JScrollPane(scalingPanel,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.CENTER);
		add(StatusBar.getInstance(), BorderLayout.SOUTH);
		
		setVisible(true);
	}

	/**
	 * getInstance - part of singleton pattern, returns the only instance of MainPanel.
	 * 
	 * @return Lang - the only instance of lang
	 */
	public static synchronized MainPanel getInstance(MainLogic mlMainLogic) { // part of singleton pattern
		if ( instance == null ){
			instance = new MainPanel(mlMainLogic);
		}
		return instance;
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

		}
	}

	/**
	 * loadSensorPanel starts an instance a sensor panel with the iMaxColumns and iMaxRows
	 * values from Main.
	 */
	public static void loadSensorPanel(boolean bReplay){
		//MainPanel.getInstance(mlMainLogic).add(SensorPanel.getInstance(mlMainLogic, bReplay), BorderLayout.CENTER);
		scalingPanel.addComponent(SensorPanel.getInstance(mlMainLogic, bReplay));
//		MainPanel.getInstance(mlMainLogic).add(	scalingPanel , BorderLayout.CENTER);
		MainPanel.getInstance(mlMainLogic).correctScalingPanel();
//		scalingPanel.setPreferedDimension(scalingPanel.getParent().getSize());
//		scalingPanel.centerZoom();
	}
	
	// NOTE: UNLOAD SENSOR PANEL MIGHT BE UNNECESSARY: just add a new to replace the old ...
	
	/**
	 * unloadSensorPanel unloads the active sensor panel.
	 */
	public static void unloadSensorPanel() {
		if(SensorPanel.getExistance()) {
			//The parameters here are unimportant
			//MainPanel.getInstance(mlMainLogic).remove(SensorPanel.getInstance(mlMainLogic, true));			
			scalingPanel.remove(SensorPanel.getInstance(mlMainLogic, true));
			SensorPanel.setNonExistance();
		}
	}

	/**
	 * correctScalingPanel - used to adjust the panel decorating the sensor 
	 * panel when the frame is resized.
	 */
	public void correctScalingPanel(){
    	// to get desired placement behavior
		if(scalingPanel.getParent() == null)return;
//		try {
//		    Thread.sleep(20);  // does not need to update directly all the time
//		} catch(InterruptedException ex) {
//		    Thread.currentThread().interrupt();
//		}
    	scalingPanel.setPreferedDimension(scalingPanel.getParent().getSize());
    	scalingPanel.centerZoom();
    	scalingPanel.revalidate();
    	scalingPanel.repaint();
	}
	
}