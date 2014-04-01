package View.Dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import Controller.MainLogic;
import Controller.ProfileLogic;
import Model.Lang;
import View.MainPanel;
import View.SensorPanel;

/**
 * 
 * @author Stefan
 *
 */
public class NewProfileDialog extends JDialog implements Observer{

	private static NewProfileDialog instance = null;
	
	private static MainLogic mlMainLogic;
	
	private JLabel idLabel = new JLabel();
	
	private JTextField idTextField = new JTextField(30);
	
	private JLabel selectSensoLabel = new JLabel();
	
	private JButton confirm = new JButton();
	private JButton cancel = new JButton();
	

	private JList<String> sensors = new JList<String>(getSensorPanelID());
	
	private JScrollPane sensorsScroll = new JScrollPane(sensors, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	
//	private String sensorType;
	
	private NewProfileDialog(MainLogic mlMainLogic, JFrame frame, boolean modal){
		super();
		NewProfileDialog.mlMainLogic = mlMainLogic;

		
//		sensors.setVisibleRowCount(10);
//		sensorsScroll.add(selectSensoLabel);
		
		
		Lang.getInstance().addObserver(this);
		
		init();
		
		 
		
		JPanel idPanel = new JPanel();
		
		idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.Y_AXIS));
		
		idPanel.add(idLabel);
		
		idPanel.add(Box.createRigidArea(new Dimension(0,5)));
		
		idPanel.add(idTextField);
		
		idPanel.add(Box.createRigidArea(new Dimension(0,5)));

		idPanel.add(selectSensoLabel);
		
		idPanel.add(Box.createRigidArea(new Dimension(0,5)));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(cancel);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(confirm);
		
//		sensors.setSize(100,100);
		setLayout( new BorderLayout() );
		add(idPanel, BorderLayout.NORTH);
		add(sensorsScroll, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.PAGE_END);

		pack();

		
		confirm.setEnabled(false);
		confirm.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				// if another profile is open ask if you want to
				// save the 
				
//				MainLogic.getProfileLogic().
				
				//The ProfileLogic in MainLogic was created with the instance method
				MainLogic.getProfileLogic().createProfile(idTextField.getText());
				String s = sensors.getSelectedValue();
				if(s==null)return;
				switch(s){
					case "newprofile.prototype":
						
						NewProfileDialog.mlMainLogic.getSensorPanelLogic().record();
						break;
					default:
						; break;
				}
				
				// add profile to database 
				// set to currently active profile
				dispose();
			}
			
		});
		
		cancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				dispose();
			}		
		});

	
		sensors.addListSelectionListener(lsl);
	}
	
	
	
	private ListSelectionListener lsl = new ListSelectionListener(){

		@Override
		public void valueChanged(ListSelectionEvent e) {
			// TODO Auto-generated method stub
			// if both ..
//			System.out.println("here");
			confirm.setEnabled(true);
//			sensorType = sensors.getSelectedValue();
			
		}
	};
	
	public static synchronized NewProfileDialog getInstance(MainLogic mlMainLogic){
		if(instance == null)
			instance = new NewProfileDialog(mlMainLogic,null,true);
		instance.setVisible(true);
		return instance;
	}
	
	private void init(){
		setTitle(Lang.rb.getString("newprofile.title"));
		idLabel.setText(Lang.rb.getString("newprofile.jlabelidtitle"));		
		confirm.setText(Lang.rb.getString("newprofile.jbuttonconfirm"));
		cancel.setText(Lang.rb.getString("newprofile.jbuttoncancel"));
	
		selectSensoLabel.setText(Lang.rb.getString("newprofile.selectSensoLabel"));
		
		sensors.removeAll();
		sensors.setListData(getSensorPanelID());
		pack();
	}


	
	
	private String[] getSensorPanelID(){
		ArrayList<String> sensorList =  getSensorPanels();
		return getSensorIDArray(sensorList.size(), sensorList);
	}
	
	private String[] getSensorIDArray(int size, ArrayList<String> tmp){
		String[] sensorKeys = new String[size];
		int i = 0;
		for(String s:tmp){
			try{
				sensorKeys[i] = Lang.rb.getString(s);
			}catch(Exception e){
				sensorKeys[i]  = s;
			}
			i++;
		}
		return sensorKeys;
	}
	
	
	/**
	 * getSensorPanels - constructs the different sensor states
	 * @return HashMap<String, SensorPanel> - the String is a key for a resource String value in Lang.rb
	 */
	private ArrayList<String> getSensorPanels(){
		ArrayList<String> tmp = new  ArrayList<String>();
		 tmp.add("newprofile.prototype");
		 tmp.add("test");
		 return tmp;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		if(arg0 instanceof Lang){
			init();
		}
	}


	
	
	
	
}
