package Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import Model.Lang;
import Model.Profile;
import View.Dialogs.ConfigDialog;
import View.Dialogs.DialogDisplay;
import View.Dialogs.ExportFileDialog;
import View.Dialogs.FileSelector;
import View.Dialogs.OpenDialog;
import View.Dialogs.SaveOnExitDialog;
import View.Main.Main;
import View.SensorMenu.State;
import View.MainPanel;
import View.SensorPanel;
import View.Dialogs.NewProfileDialog;

public class MenuFactory {

    //instance variables
	private static MainLogic mlMainLogic;
	
// No need to extend observer
	
	public static JMenuBar buildMenu(MainLogic mlMainLogic){
		MenuFactory.mlMainLogic = mlMainLogic;
		
		JMenuBar bar = new JMenuBar();

		JMenu m,s;
		JMenuItem i;
		JCheckBoxMenuItem c;
		// different methods setToolTipText("hello"); menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		
		// Options
		m = new JMenu(Lang.rb.getString("m1"));
	
			// Profile
			s = new JMenu(Lang.rb.getString("m1.s1"));
				// New Profile
				i = new JMenuItem(Lang.rb.getString("m1.s1.i1"));
				i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK)); // , ActionEvent.ALT_MASK
				i.addActionListener(al);
				s.add(i);
				// Open Profile
				i = new JMenuItem(Lang.rb.getString("m1.s1.i2"));
				i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK)); 
				i.addActionListener(al);
				s.add(i);
				// Save Profile As
				i = new JMenuItem(Lang.rb.getString("m1.s1.i3"));
				//i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK)); 
				i.addActionListener(al);
				s.add(i);				
	
				// Save Profile
				i = new JMenuItem(Lang.rb.getString("m1.s1.i4"));
				i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK)); 
				i.addActionListener(al);
				s.add(i);
					
			m.add(s);
		
			m.addSeparator();
			// Import
			i = new JMenuItem(Lang.rb.getString("m1.i2")); 
			i.addActionListener(al);
			m.add(i);
			// Export
			i = new JMenuItem(Lang.rb.getString("m1.i3")); 
			i.addActionListener(al);
			m.add(i);
			
			m.addSeparator();
			// EXIT
			i = new JMenuItem(Lang.rb.getString("m1.i4")); 
			i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK)); 
			i.addActionListener(al);
			m.add(i);		
			
			
		bar.add(m);
		// Edit
		m = new JMenu(Lang.rb.getString("m2"));
			// Undo
			i = new JMenuItem(Lang.rb.getString("m2.i1"));
			i.addActionListener(al);
			m.add(i);
			// Redo
			i = new JMenuItem(Lang.rb.getString("m2.i2"));
			i.addActionListener(al);
			m.add(i);
		bar.add(m);
		// View
		m = new JMenu(Lang.rb.getString("m3"));
			// Full screen check box
			c	= new JCheckBoxMenuItem(Lang.rb.getString("m3.i1"), Main.fullscreen); 
			c.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
			c.addActionListener(al);
			m.add(c);
			
			m.addSeparator();
			// Configuration-dialog
			i = new JMenuItem(Lang.rb.getString("m3.i2"));
			i.addActionListener(al);
			m.add(i);
		bar.add(m);

		// Help
		m = new JMenu(Lang.rb.getString("m4")); 
			
			// About
			i = new JMenuItem(Lang.rb.getString("m4.i1"));
			i.addActionListener(al);
			m.add(i);
			
			// Help
			i = new JMenuItem(Lang.rb.getString("m4.i2"));
			i.addActionListener(al);
			m.add(i);
		bar.add(m);	
			

		return bar;
	}
	
	//
	private static ActionListener al = new ActionListener(){
		/**
		 * 
		 * @param a - an ActionEvent from the menu.
		 */
		@Override
		public void actionPerformed(ActionEvent a) {
		// TODO Auto-generated method stub
			
			if(a.getActionCommand().equals(Lang.rb.getString("m1.s1.i1"))){ // New Profile
				// if there is a old profile, launch close old profile dialog with save options 
				
				DialogDisplay.display(NewProfileDialog.getInstance(mlMainLogic));
			}
			
			else if(a.getActionCommand().equals(Lang.rb.getString("m1.s1.i2"))){ //Open profile?
				DialogDisplay.display(OpenDialog.getInstance(mlMainLogic, getValidProfileNames()));
				
				/*final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(null);

				fc.setVisible(true);*/
			}
			
			else if(a.getActionCommand().equals(Lang.rb.getString("m1.s1.i3"))){ // Save Profile As
//				FileSelector saveAsChooser = new FileSelector(FileSelector.Type.SAVE_AS);
//				int choice = saveAsChooser.showSaveDialog(null);
//				if(choice == JFileChooser.APPROVE_OPTION){   
//					if(saveAsChooser.getSelectedFile()!=null){   
//						File theFileToSave = saveAsChooser.getSelectedFile(); 
//						
//						// SAVE HERE .... 
//					}
//				}
			}
			
			else if(a.getActionCommand().equals(Lang.rb.getString("m1.s1.i4"))){ // Save Profile
				//Currently only written for saving the active profile,
				//not all open unsaved profiles
				
				//Set the current profile to 'saved'
				mlMainLogic.adjustProfilesChanged(mlMainLogic.getCurrentProfile(), false);
				
				//If the active profile is stopped, just save
				if(mlMainLogic.getStopRecording()) {
					if(!MainLogic.getProfileLogic().saveCurrentProfile()) {
						//If the save fails, notification					
					}
				} 
				else { //otherwise temporarily stop the data flow for saving
					//stop the data flow to SensorPanel
					
					//mlMainLogic.setStop(true);										
					mlMainLogic.getSensorPanelLogic().stopRecording();
					
					boolean bWait = true;
					
					// do save
					while(bWait) {
						if(!MainLogic.getProfileLogic().saveCurrentProfile()) {
					         //If the save fails, notification	
							bWait = false;
						}
						else bWait = false;
					}
					
		            //State.getInstance().updateState(State.States.STOP);					
					
					//restart the data flow to SensorPanel
					mlMainLogic.getSensorPanelLogic().record();										
				}
			}
			
			else if(a.getActionCommand().equals(Lang.rb.getString("m1.i2"))){ // Import
				if(DialogDisplay.hasDialog())DialogDisplay.display(null);
				FileSelector importFileChooser = new FileSelector(FileSelector.Type.IMPORT);
				int choice = importFileChooser.showOpenDialog(null);
				if(choice == JFileChooser.APPROVE_OPTION){  
					if(importFileChooser.getSelectedFile()!=null){  // check also if is directory
						File theFileToImport = importFileChooser.getSelectedFile(); 
						// move directory to xml....
					}
				}
			}
			
			else if(a.getActionCommand().equals(Lang.rb.getString("m1.i3"))){ // Export
				// Check if saved, if so ...
				DialogDisplay.display(ExportFileDialog.getInstance());
			}
			
			else if(a.getActionCommand().equals(Lang.rb.getString("m1.i4"))){ // Exit
			    //Save the ProfileLog on exit for now (temporary solution)
				
				// check if the profile is saved 
				DialogDisplay.display(SaveOnExitDialog.getInstance(mlMainLogic));
				// The saving part bellow goes to SaveOnExitDialog , save and exit in the actionlistener's method
			}
			
			else if(a.getActionCommand().equals(Lang.rb.getString("m3.i1"))){ // Full screen
				if(Main.fullscreen){
					Main.setWindowed();
				}else{
					Main.setFullScreen();
				}
			}
				
			else if(a.getActionCommand().equals(Lang.rb.getString("m3.i2"))){ // Configurations
				DialogDisplay.display(ConfigDialog.getInstance());
//				ConfigDialog.getInstance().setVisible(true);
				
			}
			
			
//			else if(a.getActionCommand().equals(Lang.rb.getString("m4.i1"))){  // Load Sensors
//				mlMainLogic.getSensorPanelLogic().record();
//				State.getInstance().updateState(State.States.RECORDING);
//			}
			

		}
	};
	
	/**
	 * 
	 * @return
	 */
	public static String[] getValidProfileNames()
	{ 	
		ArrayList<Profile> alProfileList = mlMainLogic.getProfiles().getProfileList();
		String[] sNames = new String[alProfileList.size()];
		
	    for(int i=0;i<alProfileList.size();i++) {
	    	sNames[i] = alProfileList.get(i).getId();	
	    }
	    
	    return sNames;
	}	
	
}
