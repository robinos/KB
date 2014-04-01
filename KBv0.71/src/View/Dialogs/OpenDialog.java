package View.Dialogs;

import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import Controller.MainLogic;
import Controller.ProfileLogic;
import Model.Lang;

/**
 * The OpenDialog class is the pop-up dialog window for loading profiles.  It
 * uses a JList to list all valid profiles.
 * This code is used with the help of the Oracle Java Tutorials, particularly the
 * scrollbar workaround.
 * http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://docs.oracle.com
 * /javase/tutorial/uiswing/examples/components/ListDialogRunnerProject/src/components
 * /ListDialog.java
 * 
 * @author: KB Group - Robin Osborne and Stefan Arvidsson
 */
public class OpenDialog extends JDialog implements Observer
{
	private static final long serialVersionUID = 6L;
	
	private static OpenDialog instance = null;
	private JList<String> jProfileList;
	private static String sSelectedProfile = "";
	
	private static MainLogic mlMainLogic;
	
	private JLabel selectProfile = new JLabel();
	private JButton setButton = new JButton();
	private JButton cancelButton = new JButton();

	private OpenDialog(MainLogic mlMainLogic, String[] sNames, JFrame jfJFrame, boolean bModel)
	{	
		
	    JOptionPane.getFrameForComponent(jfJFrame);
	    
	    OpenDialog.mlMainLogic = mlMainLogic;
		
//		String labelText  = "Load Profile";
		String longValue = "VeryLongProfileNameProducingAReasonableBaseSize";
		
		OpenDialog.mlMainLogic = mlMainLogic;		
		Lang.getInstance().addObserver(this);
		init();
		JPanel idPanel = new JPanel();
		idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.Y_AXIS));

        //Create and initialize the buttons.
//        cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}		
		});
		
        //
        setButton = new JButton("Open");
        setButton.setActionCommand("Open");
        setButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
		        if ("Set".equals(e.getActionCommand())) {
		        	OpenDialog.sSelectedProfile = (String)(jProfileList.getSelectedValue());
		            ProfileLogic.getInstance(null).loadProfile(sSelectedProfile);
		        }
		        OpenDialog.instance.setVisible(false);
			}
		});
        getRootPane().setDefaultButton(setButton);
 
        /*
        final JButton confirmButton = new JButton("Confirm");
		confirmButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Load the profile from file
				MainLogic.getProfileLogic().loadProfile(id));
				dispose();
			}
		});*/        
        
        //main part of the dialog
        jProfileList = new JList<String>(sNames) {

			private static final long serialVersionUID = 5L;

			//Subclass JList to workaround bug 4832765, which can cause the
            //scroll pane to not let the user easily scroll up to the beginning
            //of the list.  An alternative would be to set the unitIncrement
            //of the JScrollBar to a fixed value. You wouldn't get the nice
            //aligned scrolling, but it should work.
            public int getScrollableUnitIncrement(Rectangle visibleRect,
                                                  int orientation,
                                                  int direction) {
                int row;
                if (orientation == SwingConstants.VERTICAL &&
                      direction < 0 && (row = getFirstVisibleIndex()) != -1) {
                    Rectangle r = getCellBounds(row, row);
                    if ((r.y == visibleRect.y) && (row != 0))  {
                        Point loc = r.getLocation();
                        loc.y--;
                        int prevIndex = locationToIndex(loc);
                        Rectangle prevR = getCellBounds(prevIndex, prevIndex);
 
                        if (prevR == null || prevR.y >= r.y) {
                            return 0;
                        }
                        return prevR.height;
                    }
                }
                return super.getScrollableUnitIncrement(
                                visibleRect, orientation, direction);
            }
        };
 
        jProfileList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (longValue != null) {
        	jProfileList.setPrototypeCellValue(longValue); //get extra space
        }
        jProfileList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        jProfileList.setVisibleRowCount(-1);
        jProfileList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    setButton.doClick(); //emulate button click
                }
            }
        });
        JScrollPane listScroller = new JScrollPane(jProfileList);
        listScroller.setPreferredSize(new Dimension(250, 80));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);
 
        //Create a container so that we can add a title around
        //the scroll pane.  Can't add a title directly to the
        //scroll pane because its background would be white.
        //Lay out the label and scroll pane from top to bottom.
        JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));

//        label.setLabelFor(jProfileList);
        listPane.add(selectProfile);
        listPane.add(Box.createRigidArea(new Dimension(0,5)));
        listPane.add(listScroller);
        listPane.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
 
        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPane.add(setButton);
 
        //Put everything together, using the content pane's BorderLayout.
        Container contentPane = getContentPane();
        contentPane.add(listPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.PAGE_END);
        
        init();
        
        //Initialize values.
        setValue("default");
        pack();
        //setLocationRelativeTo(locationComp);		
	}	
	
	public static synchronized OpenDialog getInstance(MainLogic mlMainLogic, String[] sNames){
		if(instance == null)
			instance = new OpenDialog(mlMainLogic, sNames, null, true);
		instance.setVisible(true);
		return instance;
	}
	
    private void setValue(String sNewSelectedProfile) {
    	sSelectedProfile = sNewSelectedProfile;
        jProfileList.setSelectedValue(sSelectedProfile, true);
    }	
	
	private void init(){
		setTitle(Lang.rb.getString("opendialog.title"));
		selectProfile.setText(Lang.rb.getString("opendialog.jlabelselectProfile"));
		setButton.setText(Lang.rb.getString("opendialog.jbuttonset"));		
		cancelButton.setText(Lang.rb.getString("opendialog.jbuttoncancel"));
		pack();
	}	
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		if(arg0 instanceof Lang){
			init();
		}
	}	
}
