package View;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import Controller.Items;
import Controller.Items.ButtonItem;
import Controller.MainLogic;
import Model.Lang;
import View.Dialogs.FileSelector;



public class SensorMenu extends JToolBar implements Observer, ActionListener{
	
	
	private static final long serialVersionUID = 3L;
	
	private static final int ZMIN=0, ZMAX=15, ZINTI=10,TMIN=0, TMAX=100,TINIT=100;
	

	private static MainLogic mlMainLogic;
	
	private DecoratingPanel scalingPanel;
	private boolean hasImage = false; //
	
	private ButtonItem backward, playPause, forward, stop,startStopRec, loadRemoveImg;// = items.get(Items.Type.PLAY_PAUSE);

	private JSlider zoomSlider = new JSlider(JSlider.HORIZONTAL,ZMIN, ZMAX,ZINTI);
	private JSlider tSlider = new JSlider(JSlider.HORIZONTAL,TMIN, TMAX,TINIT);
	private JLabel zoomFactorLabel = new JLabel();
	private JLabel transparencyFactorLabel = new JLabel();
	
	public SensorMenu(MainLogic mlMainLogic, DecoratingPanel scalingPanel){
		this.scalingPanel = scalingPanel;
		State.getInstance().addObserver(this);
		Lang.getInstance().addObserver(this);
		SensorMenu.mlMainLogic = mlMainLogic;
		
		
		setLayout(new FlowLayout(FlowLayout.CENTER, 0,0));
		setFloatable(false); // if JToolBar
		HashMap<Items.Type, Items.ButtonItem> items = Items.getInstance().constructItems();
		
		backward = items.get(Items.Type.BACKWARD);
		playPause = items.get(Items.Type.PLAY_PAUSE);
		forward = items.get(Items.Type.FORWARD);
		stop = items.get(Items.Type.STOP);
		startStopRec = items.get(Items.Type.START_STOP_REC);
		loadRemoveImg = items.get(Items.Type.LOAD_REMOVE_IMAGE);
		langInit();
		
		State.getInstance().updateState(State.States.INIT);
		setVisible(true);

	}
	
	
	private void langInit(){
		// don't forget the tooltips ....
		zoomFactorLabel.setText(Lang.rb.getString("sensemenu.zoomFactorLabel"));
		transparencyFactorLabel.setText(Lang.rb.getString("sensemenu.transparencyFactorLabel"));
	}
	
	
	/**
	 * Adds items and their ActionListeners to the sensor menu and then sets stop state.
	 * meaning it will be unnecessary to set images here.   
	 * <pre>
	 * States in stop state(initial values):
	 * Enabled buttons: 
	 * 		PLAY_PAUSE
	 * 		START_STOP_REC
	 * 		LOADSENSORS
	 * Disabled buttons: 
	 * 		STOP
	 * </pre>
	 * the load sensor button is enable
	 */
	private void init(){ // add the init tooltips... later

//		backward.addActionListener(this);
//		add(backward);
		

		playPause.addActionListener(this);
		add(playPause);

//		forward.addActionListener(this);
//		add(forward);
		
		stop.setImage(Items.Images.stop);
		stop.addActionListener(this);
		add(stop);	
		

		startStopRec.addActionListener(this);
		add(startStopRec);	
		
		loadRemoveImg.addActionListener(this);
		add(loadRemoveImg);
		
		
		addSeparator();
		
		add(zoomFactorLabel);
		

		zoomSlider.setOpaque(false);
    	zoomSlider.setMajorTickSpacing(10);
    	zoomSlider.setPaintTicks(true);
		
    	zoomSlider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				JSlider s = (JSlider) e.getSource() ;
				scalingPanel.setScaleFactor(s.getValue());
				StatusBar.printZoomFactor(s.getValue());
			}	
    	});
  
		add(zoomSlider);
		
		add(transparencyFactorLabel);
		

		tSlider.setOpaque(false);
    	tSlider.setMajorTickSpacing(10);
    	tSlider.setPaintTicks(true);
		
    	tSlider.addChangeListener(new ChangeListener(){
    		@Override
			public void stateChanged(ChangeEvent e) {
				// TODO Auto-generated method stub
				JSlider s = (JSlider) e.getSource() ;
				scalingPanel.setTransparency(s.getValue());
				StatusBar.printTransparency(s.getValue());
			}	
		});
    	
    	
		add(tSlider);
		
		

		
		
//		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		State.getInstance().updateState(State.States.STOP);
		
		
	}
	

	@Override
	public void actionPerformed(ActionEvent a) {
		Items.ButtonItem b =(Items.ButtonItem) a.getSource();
		Items.Type t = b.getType();
		
		switch(t){
		case PLAY_PAUSE:playPauseButton();
			break;
		case START_STOP_REC: startStopRecButton();
			break;
		case STOP:stopButton();
			break;
		case FORWARD:
			break;
		case BACKWARD:
			break;
		case LOAD_REMOVE_IMAGE: loadRemoveImageButton();
			break;
		default:
			break;
		
		}
	
	}
	
	
// ***************************************************************************************************************************
//	ADD STUFF ON BUTTON ACTION HERE!!!!!
// ***************************************************************************************************************************
	
	private void playPauseButton(){
		if(State.getInstance().getCurrent().equals(State.States.PLAYING)){
			// What happens when playing -> pause
			// ACTION CODE
			//*****************************
			mlMainLogic.getSensorPanelLogic().stopPlaying(mlMainLogic.getCurrentProfile());
			//*****************************
			State.getInstance().updateState(State.States.PAUSED);
		}else if(State.getInstance().getCurrent().equals(State.States.PAUSED)){
			// What happens when pause -> playing
			// ACTION CODE
			//*****************************
			mlMainLogic.getSensorPanelLogic().startPlaying(mlMainLogic.getCurrentProfile());
			//*****************************
			State.getInstance().updateState(State.States.PLAYING);
		}else if(State.getInstance().getCurrent().equals(State.States.STOP)){
			// What happens when stop -> playing
			// ACTION CODE
			//*****************************
			mlMainLogic.getSensorPanelLogic().startPlaying(mlMainLogic.getCurrentProfile());
			//*****************************
			State.getInstance().updateState(State.States.PLAYING);
		}else
			System.out.println("Err: unidentified state reached in playPauseButton method");
		
	}
	
	private void stopButton(){
		if(State.getInstance().getCurrent().equals(State.States.PLAYING)){
			// What happens when playing -> stop
			// ACTION CODE
			//*****************************
			mlMainLogic.getSensorPanelLogic().stopPlaying(mlMainLogic.getCurrentProfile());
			//*****************************
			State.getInstance().updateState(State.States.STOP);
		}else if(State.getInstance().getCurrent().equals(State.States.PAUSED)){
			// What happens when pause -> stop
			// ACTION CODE
			//*****************************			
			//*****************************
			State.getInstance().updateState(State.States.STOP);
		}else System.out.println("Err: unidentified state reached in stopButton method");
	}
	
	private void startStopRecButton(){
//		Items.ButtonItem b = items.get(Items.Type.STOP);
//		stop.setEnabled(true);
		if(State.getInstance().getCurrent().equals(State.States.RECORDING)){
			// What happens when recording -> stop
			// ACTION CODE
			//*****************************
		    mlMainLogic.getSensorPanelLogic().stopRecording();
			//*****************************
			State.getInstance().updateState(State.States.STOP);
		}else if(State.getInstance().getCurrent().equals(State.States.STOP)){
			// What happens when stop -> recording
			// ACTION CODE
			//*****************************
			//If starting the recording actually succeeded, change state
			if(mlMainLogic.getSensorPanelLogic().record()) {			
			//*****************************
				stop.setImage(Items.Images.unavailable, true);
//				stop.setEnabled(false);
				State.getInstance().updateState(State.States.RECORDING);
			}
		}else System.out.println("Err: unidentified state reached in startStopRecButton method");
	}
	
	private void loadRemoveImageButton(){
		if(hasImage){
			scalingPanel.removeImage();
			// Remove image in the profile folder ??  load an image from the profile folder somewhere ??
			loadRemoveImg.setImage(Items.Images.load_remove_image);
			// Set tooltips
			hasImage = false;
		}else{
			
			FileSelector imgChooser = new FileSelector(FileSelector.Type.IMAGE_OPENER);

			int returnVal = JFileChooser.APPROVE_OPTION; // 0
			
			while(  (hasImage!=true)  ){
				returnVal = imgChooser.showOpenDialog(this);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
						if(scalingPanel.loadImage(imgChooser.getSelectedFile().getAbsolutePath())){
							loadRemoveImg.setImage(Items.Images.unavailable, true);
							// place/replace image in the profile folder ?? 
							// Set tooltips
							hasImage = true;
						}
				}else break;
			}
		}


	}
	
// ***************************************************************************************************************************
//	END OF BUTTON ACTION
// ***************************************************************************************************************************	
	
	
//	******************************************************************************************************************************************************************
	
	
	

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
		if(arg0 instanceof State){
			State.States state = (View.SensorMenu.State.States) arg1;
			// in these cases do what needs to be done in the state
			// Disable buttons
			// Set images on buttons
			switch(state){
			case PAUSED:statePause();
				break;
			case PLAYING:statePlaying();
				break;
			case RECORDING:stateRecording();
				break;
			case STOP:stateStop();
				break;
			case FORWARD:
				break;
			case BACKWARD:
				break;
			case LOAD_REMOVE_IMAGE:loadRemoveImage(); // make sure to savc
				break;
			case INIT:init();
				break;
			default:
				break;

			}

		}else if(arg0 instanceof Lang){
			langInit();
		}
		// there should be another thing to observe here, that will notify when stopped playing....
	}
	
	private void statePause(){
		playPause.setImage(Items.Images.play);
		
		stop.setImage(Items.Images.stop);
		stop.setEnabled(true);
//		b = items.get(Items.Type.FORWARD);
//			
//		b = items.get(Items.Type.BACKWARD);
	}
	
	private void statePlaying(){
		playPause.setImage(Items.Images.pause);

		stop.setImage(Items.Images.stop);
		stop.setEnabled(true);

		startStopRec.setEnabled(false);
		startStopRec.setImage(Items.Images.unavailable, true); // note should only be used on standard size images, else the position is wrong, need to fix this

		loadRemoveImg.setEnabled(false);
//		b = items.get(Items.Type.FORWARD);
			
//		b = items.get(Items.Type.BACKWARD);

	}
	
	private void stateRecording(){
		playPause.setEnabled(false);
		playPause.setImage(Items.Images.unavailable, true);

		stop.setEnabled(false);
		stop.setImage(Items.Images.unavailable, true);

		startStopRec.setImage(Items.Images.stop_recording);

		loadRemoveImg.setEnabled(false);
		
//		b = items.get(Items.Type.FORWARD);
//			b.setEnabled(false);
//			b.setImage(Items.Images.unavailable, true);
//		b = items.get(Items.Type.BACKWARD);
//			b.setEnabled(false);
//			b.setImage(Items.Images.unavailable, true);
	}
	
	private void stateStop(){ // The default state
		playPause.setImage(Items.Images.play);
		playPause.setEnabled(true);

		stop.setImage(Items.Images.stop);
		stop.setImage(Items.Images.unavailable, true);
		stop.setEnabled(false);

		startStopRec.setImage(Items.Images.start_recording);
		startStopRec.setEnabled(true);

		loadRemoveImg.setImage(Items.Images.load_remove_image);
		loadRemoveImg.setEnabled(true);
//		if(there is a track){	
//			b = items.get(Items.Type.FORWARD);
//
//			b = items.get(Items.Type.BACKWARD);
//		}
	}
	
	private void loadRemoveImage(){
		
		// Don't block if not necessary, if the modal problem occurs then block....
		// Might be unnecessary

		playPause.setEnabled(false);
		playPause.setImage(Items.Images.unavailable, true);

		stop.setEnabled(false);
		stop.setImage(Items.Images.unavailable, true);

		startStopRec.setEnabled(false);
		startStopRec.setImage(Items.Images.unavailable, true);

		loadRemoveImg.setEnabled(false);
			
			
//		b = items.get(Items.Type.FORWARD);
//			b.setEnabled(false);
//			b.setImage(Items.Images.unavailable, true);
//		b = items.get(Items.Type.BACKWARD);
//			b.setEnabled(false);
//			b.setImage(Items.Images.unavailable, true);
	}
	
	private void forward(Items.ButtonItem b){
//		b = items.get(Items.Type.FORWARD);
//		// same state as play?
	}
//	
	private void backward(Items.ButtonItem b){
//		b = items.get(Items.Type.BACKWARD);
//		// same state as play?
	}
	
	
//	***********************************************************************************************************************************************************
	
	
	public static class State extends Observable{
		
		public enum States{
			INIT, // init state 
			STOP, 
			RECORDING,
			PLAYING,
			PAUSED,
			LOAD_REMOVE_IMAGE,
			FORWARD,
			BACKWARD
		};
		private State.States current = null;
		
		private static State instance=null; 
		
		private State(){}
		
		public static State getInstance(){
			if (instance == null){
				instance = new State();
			}
			return instance;
		}
		
		
		public void updateState(States newState){
			setCurrent(newState);
			instance.setChanged();
			instance.notifyObservers(newState);
		}

		public State.States getCurrent() {
			return current;
		}

		private void setCurrent(State.States current) {
			this.current = current;
		}
		
	}


	
	
	
	
}

