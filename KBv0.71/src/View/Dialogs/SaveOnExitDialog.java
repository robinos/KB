package View.Dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Controller.MainLogic;
import Controller.ProfileLogic;
import Model.Lang;
import View.SensorPanel;

/**
 * 
 * @author Stefan
 *
 */
public class SaveOnExitDialog extends JDialog implements ActionListener,Observer{
	private static SaveOnExitDialog instance = null;
	
	private static MainLogic mlMainLogic;
	
	private JLabel exitMessage = new JLabel();
	
	private JButton saveAndExit = new JButton();
	private JButton exitWithoutSaving = new JButton();
	private JButton cancel = new JButton();
	
	private SaveOnExitDialog(MainLogic mlMainLogic, JFrame frame, boolean modal){
		super(frame, modal);
		
		SaveOnExitDialog.mlMainLogic = mlMainLogic;
		
		Lang.getInstance().addObserver(this);
		JPanel messagePanel = new JPanel();
		
		messagePanel.add(exitMessage);
		JPanel buttonPanel = new JPanel();
		
		buttonPanel.add(saveAndExit);
		buttonPanel.add(exitWithoutSaving);
		buttonPanel.add(cancel);

		add(messagePanel, BorderLayout.NORTH);
		add(buttonPanel, BorderLayout.PAGE_END);
		saveAndExit.addActionListener(this);
		exitWithoutSaving.addActionListener(this);
		cancel.addActionListener(this);
		init();
		
	}
	
	public static SaveOnExitDialog getInstance(MainLogic mlMainLogic){
		if(instance==null)
			instance= new SaveOnExitDialog(mlMainLogic, null, false);
		return instance;
	}

	private void init(){
		setTitle(Lang.rb.getString("saveAndExit.title"));
		exitMessage.setText(Lang.rb.getString("saveAndExit.jlabelmessage"));
		saveAndExit.setText(Lang.rb.getString("saveAndExit.jbuttonsaveandexit"));
		exitWithoutSaving.setText(Lang.rb.getString("saveAndExit.jbuttonexitwithoutsaving"));
		cancel.setText(Lang.rb.getString("saveAndExit.jbuttoncancel"));
		pack();
	}
	
	
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		if(arg0 instanceof Lang){
			init();
		}
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		// TODO Auto-generated method stub
		Object src = a.getSource();
		if(src.equals(saveAndExit)){
			//stop recording with SensorPanel
			if(SensorPanel.getExistance()) mlMainLogic.getSensorPanelLogic().stopRecording();
			mlMainLogic.setStopRecording(true);
			//stop playing the current profile
			mlMainLogic.getSensorPanelLogic().stopPlaying(mlMainLogic.getCurrentProfile());
			//save
			MainLogic.getProfileLogic().saveCurrentProfile();
			//exit
			System.exit(0);
		}else if(src.equals(exitWithoutSaving)){
			//stop recording with SensorPanel
			if(SensorPanel.getExistance()) mlMainLogic.getSensorPanelLogic().stopRecording();
			mlMainLogic.setStopRecording(true);
			//stop playing the current profile
			mlMainLogic.getSensorPanelLogic().stopPlaying(mlMainLogic.getCurrentProfile());			
			//exit
			System.exit(0);
		}else if(src.equals(cancel)){
			dispose();
		}
	}
	
	
}
