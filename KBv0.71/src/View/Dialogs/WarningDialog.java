package View.Dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JDialog;



/**
 * 
 * @author Stefan
 *
 */
public class WarningDialog extends JDialog implements ActionListener, Observer {

	
	
	
	//..................
	// warn for something 
	
	// proceed or cancel
	
	public static boolean message(String warning){
		/* Warnings could be:
		 * 
		 * Still measuring
		 * 
		 * 
		 */
		
		
		
		return false;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
}