package View.Dialogs;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

/**
 * 
 * @author Stefan
 *
 */
public class DialogDisplay {

	private static JDialog current = new JDialog();
	private static boolean haveDialog = false;
	
	public static void display(JDialog other){
		if(other == null){
			current.dispose();
			return;
		}
		if(current.isVisible()) current.dispose();
		

		
		// set position to start 		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		
		int x = (dim.width - other.getWidth()) / 2; // X-position
		int y = (dim.height - other.getHeight()) / 2; // Y-position		
		
		other.setBounds(x, y, other.getWidth(), other.getHeight());	
		other.setAlwaysOnTop(true);
		other.setResizable(false);

		other.setVisible(true);
		current = other;
		haveDialog = true;
		
		other.addWindowListener(new WindowAdapter() {
			
			
		    @Override
			public void windowClosing(WindowEvent arg0) {
				// TODO Auto-generated method stub
				super.windowClosing(arg0);
				haveDialog = false;
			}

		});
		
	}
	
	// used by Main when minimizing window, etc..
	public static void hide(){
		current.setVisible(false);
	}
	
	public static void show(){
		if(hasDialog())
		current.setVisible(true);
	}
	
	public static boolean hasDialog(){
		return haveDialog;
	}
	
	
	
	
	
	
}
