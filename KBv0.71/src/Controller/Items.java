package Controller;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JButton;


public class Items {
	private static Items instance = null;
	
	private static final String ICONS_PATH = "Icons/Sensor Action Menu/";
	private static final String[] FORMAT = {"gif"};
	private HashMap<Items.Images, Image> imageMap = new HashMap<Items.Images, Image>();
	private HashMap<Items.Images, HashMap<Items.ScaleKey, Integer>> imgScaleMap = makeImages();

	public enum Type{
		PLAY_PAUSE,
		START_STOP_REC,
		STOP,
		FORWARD,
		BACKWARD,
		LOAD_REMOVE_IMAGE
	};
	
	public enum Images{
		play, // Plays a recorded session at the set starting point, pauses if playing and starting point is changed.
		pause,
		start_recording,  // Starts and stops the recording 
		stop_recording,
		stop,	// Goes back to absolute start position, enable is false if not playing ...
		load_remove_image, // Gives the user the load sensor dialog (when written)
		unavailable, 
		loading,
		forward,
		backward
	}
	
	private Items(){}
	
	public static Items getInstance(){
		if(instance == null)
			instance = new Items();
		return instance;
	}
	
	// reads in images, if image is in 
	private HashMap<Items.Images, HashMap<Items.ScaleKey, Integer>> makeImages() {
		HashMap<Items.Images, HashMap<Items.ScaleKey, Integer>> imgScaleMap = new HashMap<Items.Images, HashMap<Items.ScaleKey, Integer>>();
		HashMap<Items.ScaleKey, Integer> tmpScaleMap = null;
		File f = new File(ICONS_PATH);
		File[] fs = f.listFiles();
		
		
		for(File file :fs){
			String tmp = checkFormat(file);
			search:
			if(tmp != null ){
				
				for(Images s: Items.Images.values()){
					
					if(tmp.equals(s.toString())){
						// found image that is defined as an enum
						BufferedImage buff = null;
						try {
							buff = ImageIO.read(new File(new String(file.getPath())));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						tmpScaleMap = new HashMap<Items.ScaleKey, Integer>();
						
						
						switch(s){
//						case load_remove_image: 
//							tmpScaleMap = scaleImage(buff, new Dimension(30,25));	
//							break;
//						case pause:
//							tmpScaleMap = scaleImage(buff, new Dimension(10,10));
//							break;
//						case play:
//							tmpScaleMap = scaleImage(buff, new Dimension(10,10));
//							break;
//						case start_recording:
//							tmpScaleMap = scaleImage(buff, new Dimension(10,10));
//							break;
//						case stop:
//							tmpScaleMap = scaleImage(buff, new Dimension(10,10));
//							break;
//						case stop_recording:
//							tmpScaleMap = scaleImage(buff, new Dimension(10,10));
//							break;
						default:
							tmpScaleMap = scaleImage(buff, new Dimension(20,20));
							break;
						}
						if(tmpScaleMap!=null){
							imageMap.put(s, buff.getScaledInstance(tmpScaleMap.get(Items.ScaleKey.width),tmpScaleMap.get(Items.ScaleKey.height), Image.SCALE_SMOOTH)); 
							imgScaleMap.put(s, tmpScaleMap);
						}
						break search;
					}
					
				}
	
			}
		}
		return imgScaleMap;
	}
	/**
	 *  checkFormat - checks if the file is of supported type
	 * @param f - the file to check  
	 * @return String - the name of the file without format ending, null if not valid format
	 */
	private String checkFormat(File f){
		int pos = f.getName().lastIndexOf(".");
//		if(pos>0)return null;
		String check = f.getName().substring(pos+1);

		for(String format:FORMAT){
			if(format.equals(check)){
				return f.getName().substring(0, pos);
			}
		}
		return null;
	}

	
	
	
// later perhaps....
//	public void rescaleImg(Items.Images s, Dimension newDim){
//		// find image again 
//		
//		if(imgPathMap.containsKey(s)){
//			imgPathMap.get(s);
//			
//			
//			imageMap.put(s, buff.getScaledInstance(tmpScaleMap.get(Items.ScaleKey.width),tmpScaleMap.get(Items.ScaleKey.height), Image.SCALE_SMOOTH)); 
//			
//			
////			File f = new File(ICONS_PATH);
//			
//			
////			f.isFile()
////			for(String format :FORMAT){ 
////				if(.equals(ICONS_PATH+s+"."+format))
////			}
////			
////			ICONS_PATH+s.toString()+"."+checkFormat()
//			
//			
//		}
//		
//		File f = new File(ICONS_PATH);
//		
//		
//		
//		File[] fs = f.listFiles();
//		
//		
//		
//	}
//	
	
	
	public enum ScaleKey{
		width, height,x,y
	}
	
	private HashMap<Items.ScaleKey, Integer> scaleImage(BufferedImage bi, Dimension imgSize){
		
		// size of component 
		double scalex = (double) imgSize.width / bi.getWidth(); 
		double scaley = (double) imgSize.height / bi.getHeight();
		
		double scale = Math.min(scalex, scaley);
		// Component size
		int width =(int) (bi.getWidth()*scale);
		int height =(int) (bi.getHeight()*scale);

		// position of component: CENTER
		int x = (int) ((imgSize.width - width ) / 2); // X-position
		int y = (int) ((imgSize.height - height) / 2); // Y-position
		// store the values
		HashMap<Items.ScaleKey, Integer> tmp = new HashMap<Items.ScaleKey, Integer>();
		tmp.put(Items.ScaleKey.width, width);
		tmp.put(Items.ScaleKey.height, height);
		tmp.put(Items.ScaleKey.x, x);
		tmp.put(Items.ScaleKey.y, y);
		return tmp;
	}
	
	
	
	


	
//	ButtonItem test;
//	public JButton getTest(){return test;}
	
	HashMap<Items.Type, ButtonItem> itemMap = null;
	
	public HashMap<Items.Type, ButtonItem> constructItems(){
		HashMap<Items.Type, ButtonItem> tmp = new HashMap<Items.Type, ButtonItem>();
		ButtonItem item;
		for(Items.Type t : Items.Type.values()){

//			item.setSizeRePosImg(new Dimension(10,10)); Test later
			// Init values
			switch(t){
//			case LOAD_REMOVE_IMAGE:
//				item = new ButtonItem(t,
//						new Dimension(40,30),
//						new Dimension(30,25));
//				break;
//			case PLAY_PAUSE:
//				test = new ButtonItem(t, 
//						new Dimension(20,20), // BUTTON SIZE
//						new Dimension(10,10) // IMAGE SIZE or else it will probably be blurry or something, should be smaller than BUTTON SIZE
//						);
//				
//				test.setImage(Items.Images.play);
////				
////				test.setImage(Items.Images.pause);
//
//				break;
//			case START_STOP_REC:
//				break;
//			case STOP:
//				break;
			default:
				item = new ButtonItem(t,
						new Dimension(30,30),
						new Dimension(20,20));
				break;
			
			}
			tmp.put(t, item);
		}
		return tmp;
	}
	
	

	
	public class ButtonItem extends JButton {

		private BufferedImage offscreen; 
		private Dimension imgPos,imgSize;
		
		private Items.Type type;
		
		
		public ButtonItem(Items.Type type , Dimension buttonSize, Dimension imgSize){
			this.type = type;
			this.imgSize = imgSize;
			offscreen= new BufferedImage(imgSize.width,imgSize.height, BufferedImage.TRANSLUCENT); // initialize 
			
			imgPos = new Dimension(((buttonSize.width-imgSize.width)/2), ((buttonSize.height-imgSize.height)/2) );
			
			
			setPreferredSize(buttonSize);
			setVisible(true);
		}
		
		
		private void paintScaledImage(Items.Images s){ 
			Image i = imageMap.get(s);
			Graphics bufferGraphics = offscreen.getGraphics();
			bufferGraphics.drawImage(i,imgScaleMap.get(s).get(Items.ScaleKey.x),
										imgScaleMap.get(s).get(Items.ScaleKey.y),
										imgScaleMap.get(s).get(Items.ScaleKey.width),
										imgScaleMap.get(s).get(Items.ScaleKey.height), null); 
			bufferGraphics.dispose(); 
			
		}
		
//		private void paintOnTopScaledImage(Items.Images s){ 
//			Image i = imageMap.get(s);
//			Graphics bufferGraphics = offscreen.getGraphics();
//			bufferGraphics.drawImage(i,		((getWidth()- imgScaleMap.get(s).get(Items.ScaleKey.width))/2),
//					((getHeight() - imgScaleMap.get(s).get(Items.ScaleKey.height))/2),
//										imgScaleMap.get(s).get(Items.ScaleKey.width),
//										imgScaleMap.get(s).get(Items.ScaleKey.height), null); 
//			bufferGraphics.dispose(); 
//		}

		
		/**
		 * setImage - 
		 * 	The default value of onTop is false
		 * @param s - Items.Images is an enum element 
		 */
		public void setImage(Items.Images s){setImage(s,false);}
		
		public void setImage(Items.Images s, boolean onTop){ 
			
			if(onTop){
//				paintOnTopScaledImage(s);
				paintScaledImage(s);
			}else{

				offscreen= new BufferedImage(imgSize.width,imgSize.height, BufferedImage.TRANSLUCENT);
				paintScaledImage(s);

			}
			
		}
		
		public void setSizeRePosImg(Dimension buttonSize){
			setPreferredSize(buttonSize);
			imgPos = new Dimension(((buttonSize.width-imgSize.width)/2), ((buttonSize.height-imgSize.height)/2) );
		}
	
		
		public Items.Type getType(){return this.type;}
		
		
		@Override
		protected void paintComponent(Graphics g) {
			// TODO Auto-generated method stub
			super.paintComponent(g);	
			g.drawImage(offscreen,this.imgPos.width,this.imgPos.height,this); 
		}
	}
	

	
	
}

