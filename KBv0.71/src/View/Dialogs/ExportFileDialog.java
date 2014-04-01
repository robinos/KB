package View.Dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.metal.MetalFileChooserUI;

import Model.Lang;

public class ExportFileDialog  extends JDialog implements Observer{

	private static ExportFileDialog instance = null;
	
	private static final String profilePath = "xml"; 
	
	private JLabel selectProfileLabel = new JLabel();
	
	private HashMap<String, File> profilesMap = getProfiles(); 

	private JList<String> profileList = new JList<String>( profilesMap.keySet().toArray(new String[profilesMap.size()]) );
	private JScrollPane profileScroll = new JScrollPane(profileList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
	private JButton confirm = new JButton();
	private JButton cancel = new JButton();


	
	private ExportFileDialog(JFrame frame,boolean modal ){
		super(frame, modal);
		Lang.getInstance().addObserver(this);
		init();
		confirm.setEnabled(false);
		JPanel selectPanel = new JPanel();
		
		selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.Y_AXIS));

		selectPanel.add(selectProfileLabel);
		
		selectPanel.add(Box.createRigidArea(new Dimension(0,5)));
		
		selectPanel.add(profileScroll);
		
		selectPanel.add(Box.createRigidArea(new Dimension(0,5)));
		
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(cancel);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(confirm);
		
		setLayout( new BorderLayout() );
		add(selectPanel, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.PAGE_END);
		pack();

		
		confirm.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				// if valide
				// do export here ....
				
				String name = profileList.getSelectedValue();
				
				FileSelector locationChooser = new FileSelector(FileSelector.Type.EXPORT);

				while(true){
					
					MetalFileChooserUI ui = (MetalFileChooserUI)locationChooser.getUI();
					Field field;
					try {
						field = MetalFileChooserUI.class.getDeclaredField("fileNameTextField");
						  field.setAccessible(true);
						  JTextField tf = (JTextField) field.get(ui);
						  tf.setText(name);
					} catch (NoSuchFieldException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (SecurityException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					} catch (IllegalArgumentException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
					} catch (IllegalAccessException e4) {
						// TODO Auto-generated catch block
						e4.printStackTrace();
					}
					
					
					
					
					int choice = locationChooser.showOpenDialog(null);
					
					if(choice == JFileChooser.APPROVE_OPTION){ // save here 
						// do check before saving....
						name = locationChooser.getSelectedFile().getName();
						if(name == null)name = profileList.getSelectedValue();
						
						
						
						System.out.println("here:"+ name);
	//					profilesMap.get(profileList.getSelectedValue()).m
						try {
							File profile = profilesMap.get(profileList.getSelectedValue());
							// Does not work yet! not correct solution
							PrintStream ps = new PrintStream(profile);
							ps.print(locationChooser.getCurrentDirectory());
							ps.close();
	//						profile.delete();
							break;
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}		
					}else break;
				}
			}
			
		});
		
		
		cancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				dispose();
			}
			
		});
		
		
		
		ListSelectionListener lsl = new ListSelectionListener(){

			@Override
			public void valueChanged(ListSelectionEvent e) {
				confirm.setEnabled(true);
			}
		};
		profileList.addListSelectionListener(lsl);
		
	}
	
	public static synchronized ExportFileDialog getInstance(){
		if(instance==null)
			instance = new ExportFileDialog(null, false);
		return instance;
	}
	
	
	private void init(){
		setTitle(Lang.rb.getString("ExportFileDialog.title"));
		selectProfileLabel.setText(Lang.rb.getString("ExportFileDialog.selectLabel"));
		confirm.setText(Lang.rb.getString("ExportFileDialog.confirmButton"));
		cancel.setText(Lang.rb.getString("ExportFileDialog.cancelButton"));
		pack();
	}
	
	
	private HashMap<String,File> getProfiles(){
		File file = new File(profilePath);
		if(file==null); // Might happen bad s**t here...
		HashMap<String,File> tmp = new HashMap<String,File>();
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for(File f: files)
				if(f.isDirectory())// the profile is a directory???
					tmp.put(f.getName(), f);
			
		}
		return tmp;
	}
	
	private String[] getNames(ArrayList<File> list){
		ArrayList<String> tmp = new ArrayList<String>();
		for(File f:list)
			if(f.isDirectory())
				tmp.add(f.getName());
		
		return tmp.toArray(new String[tmp.size()]);
	}
	
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		if(arg0 instanceof Lang){
			init();
		}
	}



}
