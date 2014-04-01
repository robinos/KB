package View.Dialogs;

import java.awt.Component;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalFileChooserUI;

import Model.Lang;
/**
 * FileSelector - extends JFileChooser and implements Observer.
 * 
 * Main purpose is to provide the JFileChooser with correct language and properties
 * depending on type.
 * 
 * @author Stefan
 *
 */
public class FileSelector extends JFileChooser implements Observer{
	// To remember where the user was located last time.
	private static String currentFileSelectorDirectory="";
	/**
	 * Type - advice which language and settings to use.
	 * @author Stefan Arvidsson
	 *
	 */
	public enum Type{
		IMAGE_OPENER, 
		IMPORT, 
		EXPORT // Export is preceded by a list dialog where the user may choose which file to export
//		SAVE_AS
	}
	// The type of the created JFileChooser.
	private FileSelector.Type currentType;
	/**
	 * FileSelector - extends JFileChooser and implements Observer. Current available types are:
	 * 
	 * IMAGE_OPENER, IMPORT, EXPORT, SAVE_AS
	 * 
	 * @param t - FileSelector.Type, to determine the language and properties of the JFileChooser.
	 */
	public FileSelector(FileSelector.Type t){
		currentType = t;
		Lang.getInstance().addObserver(this);
		init();
		setCurrentDirectory(new File(currentFileSelectorDirectory));

	}

	private void init(){
		generalLang(); // first since the language settings might be changed depending on the type
		switch(currentType){
		case EXPORT:exportFileLang();
			break;
		case IMAGE_OPENER:imgSelectorLang();
			break;
		case IMPORT:importFileLang();
			break;
//		case SAVE_AS:saveAsLang();
//			break;
		default:
			return; // there is no such type 
		}
		SwingUtilities.updateComponentTreeUI(this);
	}
	/**
	 * generalLang - used by the init method, is used to update the language in general for the FileSelector.
	 */
	private void generalLang(){
//		http://beradrian.wordpress.com/2007/07/30/internationalization-for-swing-standard-components/
//  	reference to numbering system
		//1
//		UIManager.put("FileChooser.lookInLabelText", Lang.rb.getString(""));	// set individually later
		//2
		UIManager.put("FileChooser.upFolderToolTipText", Lang.rb.getString("fileSelector.upFolderToolTipText")); 	
		//3
		UIManager.put("FileChooser.homeFolderToolTipText", Lang.rb.getString("fileSelector.homeFolderToolTipText")); 
		//4 
		UIManager.put("FileChooser.newFolderToolTipText", Lang.rb.getString("fileSelector.newFolderToolTipText")); 
		//5 
		UIManager.put("FileChooser.listViewButtonToolTipText", Lang.rb.getString("fileSelector.listViewButtonToolTipText")); 
		//6  
		UIManager.put("FileChooser.detailsViewButtonToolTipText", Lang.rb.getString("fileSelector.detailsViewButtonToolTipText")); 
		//7 
		UIManager.put("FileChooser.fileNameHeaderText", Lang.rb.getString("fileSelector.fileNameHeaderText")); 
		//8 
		UIManager.put("FileChooser.fileSizeHeaderText", Lang.rb.getString("fileSelector.fileSizeHeaderText")); 
		//9 
		UIManager.put("FileChooser.fileTypeHeaderText", Lang.rb.getString("fileSelector.fileTypeHeaderText")); 
		//10 
		UIManager.put("FileChooser.fileDateHeaderText", Lang.rb.getString("fileSelector.fileDateHeaderText")); 		
		//11
		UIManager.put("FileChooser.fileNameLabelText", Lang.rb.getString("fileSelector.fileNameLabelText")); 
		//12 
		UIManager.put("FileChooser.filesOfTypeLabelText", Lang.rb.getString("fileSelector.filesOfTypeLabelText")); 
		//13 individually
		//14 individually
		
		// other
		UIManager.put("FileChooser.acceptAllFileFilterText", Lang.rb.getString("fileSelector.acceptAllFileFilterText")); // not necessary, only allows certain files?
//		UIManager.put("FileChooser.fileAttrHeaderText", Lang.rb.getString(""));  // what is this?
		UIManager.put("FileChooser.newFolderErrorText", Lang.rb.getString("fileSelector.newFolderErrorText"));
		
		UIManager.put("OptionPane.yesButtonText", Lang.rb.getString("fileSelectorOptionPane.yesButtonText"));
		UIManager.put("OptionPane.noButtonText", Lang.rb.getString("fileSelectorOptionPane.noButtonText"));
		UIManager.put("OptionPane.cancelButtonText", Lang.rb.getString("fileSelectorOptionPane.cancelButtonText"));	
		
		UIManager.put("ProgressMonitor.progressText", Lang.rb.getString("fileSelectorProgressMonitor.progressText"));	
	}
	
	
	
	private void imgSelectorLang(){



		
		UIManager.put("FileChooser.openDialogTitleText", Lang.rb.getString("imageSelector.openDialogTitleText")); 
		
		UIManager.put("FileChooser.lookInLabelText", Lang.rb.getString("imageSelector.lookInLabelText"));
		
		UIManager.put("FileChooser.openButtonText", Lang.rb.getString("imageSelector.openButtonText"));
		UIManager.put("FileChooser.openButtonToolTipText", Lang.rb.getString("imageSelector.openButtonToolTipText"));
		
		UIManager.put("FileChooser.cancelButtonText", Lang.rb.getString("imageSelector.cancelButtonText")); 
		UIManager.put("FileChooser.cancelButtonToolTipText", Lang.rb.getString("imageSelector.cancelButtonToolTipText"));

		

		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		
		setAcceptAllFileFilterUsed(false);
		addChoosableFileFilter(new FileNameExtensionFilter(
				Lang.rb.getString("imageSelector.fileTypes"), ImageIO.getReaderFileSuffixes())); 
		
	}

	
//	private void saveAsLang(){
////		UIManager.put("FileChooser.acceptAllFileFilterText", Lang.rb.getString("")); // not necessary, only allows XML
//		UIManager.put("FileChooser.saveDialogTitleText", Lang.rb.getString("saveAsSelector.saveDialogTitleText"));
//	
//		UIManager.put("FileChooser.saveInLabelText", Lang.rb.getString("saveAsSelector.saveInLabelText"));
//		
//		UIManager.put("FileChooser.saveButtonText", Lang.rb.getString("saveAsSelector.saveButtonText"));	
//		UIManager.put("FileChooser.saveButtonToolTipText", Lang.rb.getString("saveAsSelector.saveButtonToolTipText"));	
//		
//		// Might be duplicate but it says so in the source ...
////		UIManager.put("FileChooser.directoryOpenButtonText", Lang.rb.getString("saveAsSelector.saveButtonText"));	
////		UIManager.put("FileChooser.directoryOpenButtonToolTipTex", Lang.rb.getString("saveAsSelector.saveButtonToolTipText"));	
//		
//		UIManager.put("FileChooser.cancelButtonText", Lang.rb.getString("saveAsSelector.cancelButtonText"));	
//		UIManager.put("FileChooser.cancelButtonToolTipText", Lang.rb.getString("saveAsSelector.cancelButtonToolTipText"));	
//	
//		
////		setAcceptAllFileFilterUsed(false);
////		addChoosableFileFilter(new FileNameExtensionFilter(
////				Lang.rb.getString("imageSelector.fileTypes"), ImageIO.getReaderFileSuffixes())); 
//		
//		
//	}
	
	
	private void importFileLang(){ // should the profile open automatically on start?
		UIManager.put("FileChooser.openDialogTitleText", Lang.rb.getString("importFileSelector.openDialogTitleText")); 
		UIManager.put("FileChooser.lookInLabelText", Lang.rb.getString("importFileSelector.lookInLabelText"));
		
		UIManager.put("FileChooser.openButtonText", Lang.rb.getString("importFileSelector.openButtonText"));
		UIManager.put("FileChooser.openButtonToolTipText", Lang.rb.getString("importFileSelector.openButtonToolTipText"));
		
		UIManager.put("FileChooser.cancelButtonText", Lang.rb.getString("importFileSelector.cancelButtonText")); 
		UIManager.put("FileChooser.cancelButtonToolTipText", Lang.rb.getString("importFileSelector.cancelButtonToolTipText"));
		
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		
//		setAcceptAllFileFilterUsed(false);
//		addChoosableFileFilter(new FileNameExtensionFilter(
//				Lang.rb.getString("imageSelector.fileTypes"), ImageIO.getReaderFileSuffixes())); 
	}
	private void exportFileLang(){

		
		
		
		
		
		
		UIManager.put("FileChooser.openDialogTitleText", Lang.rb.getString("exportFileSelector.openDialogTitleText"));
		
		UIManager.put("FileChooser.lookInLabelText", Lang.rb.getString("exportFileSelector.openInLabelText"));
		
		UIManager.put("FileChooser.openButtonText", Lang.rb.getString("exportFileSelector.openButtonText"));	
		UIManager.put("FileChooser.openButtonToolTipText", Lang.rb.getString("exportFileSelector.openButtonToolTipText"));	
		
		
		
		
		// May be not save ....
//		
//		UIManager.put("FileChooser.saveDialogTitleText", Lang.rb.getString("exportFileSelector.saveDialogTitleText"));
//	
//		UIManager.put("FileChooser.saveInLabelText", Lang.rb.getString("exportFileSelector.saveInLabelText"));
//		
//		UIManager.put("FileChooser.saveButtonText", Lang.rb.getString("exportFileSelector.saveButtonText"));	
//		UIManager.put("FileChooser.saveButtonToolTipText", Lang.rb.getString("exportFileSelector.saveButtonToolTipText"));	
		
		
		
		
		// Might be duplicate but it says so in the source ...
//		UIManager.put("FileChooser.directoryOpenButtonText", Lang.rb.getString("exportFileSelector.saveButtonText"));	
//		UIManager.put("FileChooser.directoryOpenButtonToolTipTex", Lang.rb.getString("exportFileSelector.saveButtonToolTipText"));	
		
		UIManager.put("FileChooser.cancelButtonText", Lang.rb.getString("exportFileSelector.cancelButtonText"));	
		UIManager.put("FileChooser.cancelButtonToolTipText", Lang.rb.getString("exportFileSelector.cancelButtonToolTipText"));	
	
//		setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//		setAcceptAllFileFilterUsed(false);

		
		UIManager.put("FileChooser.fileNameLabelText", Lang.rb.getString("exportFileSelector.fileNameLabelText")); // Remove
		UIManager.put("FileChooser.acceptAllFileFilterText", Lang.rb.getString("exportFileSelector.fileTypes"));
		

		
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		if(arg0 instanceof Lang){
			init();
		}
	}
	/**
	 * setCurrentDirectory - Overriden from JFileChooser
	 * @param dir - is a File with the path way to the current directory.
	 */
	@Override
	public void setCurrentDirectory(File dir) {
		// TODO Auto-generated method stub
		super.setCurrentDirectory(dir);
		try{
			currentFileSelectorDirectory=dir.getAbsolutePath();
		}catch(Exception ne){
			// is expected to throw an exception.
		}
	}
}
