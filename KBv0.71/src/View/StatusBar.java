package View;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import Model.Lang;


public class StatusBar extends JPanel implements Observer{
	private static StatusBar instance = null;

	// instance variables 

    // The components of the status bar
    private static JLabel statusLeft  = new JLabel();
    private static JLabel statusMiddle  = new JLabel();
    private static JLabel statusRight = new JLabel();

    // The layout for the panel
    private GroupLayout layout = new GroupLayout(this);
    // The Border of the Status Bar
    private Border panelBorder = LineBorder.createBlackLineBorder();
	private static int zoomF=10, transp=100, zo=100;
    /**
     * Constructor for objects of class StatusBarField
     */
    private StatusBar()
    {
    	Lang.getInstance().addObserver(this);
    	init();

        
        // Sets the layout of the panel
        this.setLayout(layout);
        // Sets the background color of the panel
        this.setBackground(Color.WHITE);
        // Sets the border of the panel
        this.setBorder(panelBorder);
        

        // Enabels gaps
        this.layout.setAutoCreateGaps(true);
        // layout.setAutoCreateContainerGaps(true);    // not used is for creating a gap between the edge and the specific component

        // If not both horizontal and vertical results in error.
        // This adds the components to the layout of the Status bar
        this.layout.setHorizontalGroup(
            this.layout.createSequentialGroup()
            .addComponent(statusLeft)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(statusMiddle)
            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(statusRight)
        );

        this.layout.setVerticalGroup(
            this.layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(statusLeft)
                .addComponent(statusMiddle)
                .addComponent(statusRight))
        );       

        
    }

    
    public static StatusBar getInstance(){
    	if(instance == null)
    		instance = new StatusBar();
    	return instance;
    }
    
    public void init(){
    	statusLeft.setText(Lang.rb.getString("statusbar.zoomFactor")+zoomF);
    	statusMiddle.setText(Lang.rb.getString("statusbar.transperancy")+transp); 
    	statusRight.setText(Lang.rb.getString("statusbar.zoom")+zo +" %");
    }
    
    
    
    
    
    public JPanel getStatusPanel() 
    { 
        return this;
    } 



	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		if(arg0 instanceof Lang){
			init();
		}
	} 


	
	
	public static void printZoomFactor(int zoomFactor){
		  statusLeft.setText(Lang.rb.getString("statusbar.zoomFactor")+zoomFactor);
		  zoomF = zoomFactor;
	}
	
	public static void printTransparency(int transparency){
		statusMiddle.setText(Lang.rb.getString("statusbar.transperancy")+transparency); 
		transp= transparency;
	}


	
	public static void printZoom(int zoom){
		statusRight.setText(Lang.rb.getString("statusbar.zoom")+zoom +" %");
		zo = zoom;
	}
	
	

	
	
}
