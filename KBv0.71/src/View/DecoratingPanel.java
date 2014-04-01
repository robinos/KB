package View;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JViewport;

public class DecoratingPanel extends JPanel   
{   

	private static final long serialVersionUID = 5L;	
	
	private static final String NO_SENSOR = "Icons/no_sensor_selected.png"; // set to Sakib
    private BufferedImage image ;
    private double scale, zoomInc;   
    private int currentScaled,inc, scaleFactor = 10; 
    private AffineTransform at;
    private String currentPath; // used to determine scroll capability
    
    private JPanel content = new JPanel();  // Decorator pattern 
   
    private float transparency =1.0f; 
    
    private Dimension preferedSize = new Dimension(0,0); // init
    
    private MouseWheelListener me = new MouseWheelListener(){
		@Override
		public void mouseWheelMoved(MouseWheelEvent me) {
			// TODO Auto-generated method stub
			zoomMethod(me.getWheelRotation(), me.getPoint());
		}
	};
	
	private MouseMotionListener  m = new MouseMotionAdapter() {
	     public void mouseDragged(MouseEvent e) {
	        Rectangle r = new Rectangle(e.getX(), e.getY(), -20, -20);
	        ((JPanel)e.getSource()).scrollRectToVisible(r);
	    } 
	 };

	/**
	 * ImageScalingPanel - The constructor
	 */
    public DecoratingPanel()   
    {   
    	content.setOpaque(false);
    	content.setVisible(true);
    	
    	add(content, new BorderLayout());
    	
    	setAutoscrolls(true);
    	this.addMouseMotionListener(m);

        setBackground(Color.white);   

        loadImage(NO_SENSOR); // init image

    }   
    
	public void zoomMethod(int notches, Point loc){
		if(notches<0 && (scaleFactor+currentScaled)<500 && currentScaled>=0){ // up
			inc = scaleFactor; 
		}else if(notches>0 && (currentScaled-scaleFactor) >0 ){ //down
			inc = -scaleFactor;

		}else inc = 0;

		currentScaled += inc;
		setScale(inc); 
		
		// CHECK IF ZOOM IS MADE INSIDE IMAGE and 
		// if the picture is bigger than the parent size -> center the screen
		
		if(imgStartDim.width <loc.x && loc.x <imgEndDim.width
				&& imgStartDim.height <loc.y && loc.y < imgEndDim.height
				&& getParent().getSize().width < (imgEndDim.width - imgStartDim.width) && 
				getParent().getSize().height < (imgEndDim.height-imgStartDim.height)){ // is inside picture && picture bigger than field

				setPreferedDimension(new Dimension((imgEndDim.width - imgStartDim.width),(imgEndDim.height-imgStartDim.height)));
				centerZoom();
				
		}else if(!(getParent().getSize().width < (imgEndDim.width - imgStartDim.width) && 
				getParent().getSize().height < (imgEndDim.height-imgStartDim.height)))
		
			setPreferedDimension(new Dimension(getParent().getSize()));
		StatusBar.printZoom(currentScaled);
      	revalidate(); 
      	repaint();
	}
    
    
	public void centerZoom(){
	    Container container = getParent();
        if (container instanceof JViewport) {

            JViewport port = (JViewport) container;
            Rectangle viewRect = port.getViewRect();

            int width = getWidth();
            int height = getHeight();

            viewRect.x = (width - viewRect.width) / 2;
            viewRect.y = (height - viewRect.height) / 2;

            scrollRectToVisible(viewRect);

        }
		
	}
	
	public void setScaleFactor(int scaleFactor){
		this.scaleFactor = scaleFactor;
	}
    
    public void setPreferedDimension(Dimension newPreferredSize){
    	this.preferedSize.setSize(newPreferredSize);
    }
    
    public Dimension getPreferredSize(){
    	return preferedSize;
    }
    
    private Dimension imgStartDim = new Dimension(0,0);
    private Dimension imgEndDim = new Dimension(0,0); // set in reset
    

    protected void paintComponent(Graphics g)   
    {   
    	super.paintComponent(g);   
    	Graphics2D g2 = (Graphics2D)g;  
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,   
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);   
      
        int w = getPreferredSize().width;   
        int h = getPreferredSize().height;   
      
        double imageWidth = scale * image.getWidth();   
        double imageHeight = scale * image.getHeight(); 
        
        double x = (w - imageWidth)/2;   
        double y = (h - imageHeight)/2;   
        imgStartDim = new Dimension((int)x,(int)y);
        imgEndDim = new Dimension((int)(imageWidth+x),(int)(imageHeight+y));

        
        at = AffineTransform.getTranslateInstance(x,y);   
        at.scale(scale, scale);   

        // might only be init value...
        content.setLocation((int)x,(int)y);
        content.setPreferredSize(new Dimension((int)imageWidth, (int)imageHeight));
//        content.setSize((int)imageWidth, (int)imageHeight);
        if(content.getComponentCount()>0){
        	content.getComponent(0).setLocation(0, 0);
        	content.getComponent(0).setSize(new Dimension((int)(imageWidth), (int)(imageHeight)));
    		content.getComponent(0).revalidate();
    		content.getComponent(0).repaint();
        }
        g2.drawRenderedImage(image, at); 
//        g2.dispose();
    }   
    
    

    public void setScale(int direction){         
        	scale += direction * zoomInc;    
        	revalidate();   
        	repaint();  
    }   

    
    /**
     * reset - private method to reset the start values of
     * 		   the zoom functionality and the preferred size 
     * 		   of this instance.
     */
    private void reset(){
        scale = 1.0;   
        zoomInc = 0.01;  
        currentScaled=100;
        StatusBar.printZoom(currentScaled);
        if(getParent()!= null){
        	preferedSize = getParent().getSize();
        }
    }
    
    
    public boolean loadImage(String path)
    {    
    	reset();

    	try{
            image = ImageIO.read(new File(path));   
		} catch (MalformedURLException mue) {
			System.out.println("url: " + mue.getMessage());
			
			return false;
		} catch (IOException ioe) {
			System.out.println("read: " + ioe.getMessage());
			return false;
		}

        if(!path.equals(NO_SENSOR))   	// Does not work if the user loads the image(gets the absolute path)...
        	addMouseWheelListener(me);       
        	
      	revalidate(); 
      	repaint();
      	currentPath = path;
      	return true;
    }   
    
    public void removeImage(){
		loadImage(NO_SENSOR);
		if (content.getComponentCount() == 0)
			removeMouseWheelListener(me);
    }
    
    
    public void loadSensors(JPanel sensors){
    	if(content.getComponentCount()>0)content.removeAll(); // if there is a component here remove it
    	addComponent(sensors);
      	revalidate(); 
      	repaint();
      	addMouseWheelListener(me);   // stand alone from image?
    }
    
    public void removeSensors(){
    	content.removeAll();
    	if(currentPath == null || currentPath.equals(NO_SENSOR))
    		removeMouseWheelListener(me);
    	
    }
    
    public boolean hasSensors(){
    	if(content.getComponentCount()>0) return true;
    	return false;
    }
    
    // Decorator pattern --------------------------------------------------
    
    
    public void addComponent(JComponent comp){
    	if(content.getComponentCount() == 0){
    		setPreferedDimension(new Dimension(comp.getWidth(),comp.getHeight()));
    		content.add(new SensorComponent(comp)); 
    	}else{
    		content.removeAll();
    		content.add(new SensorComponent(comp)); 
    	}
    }
    
    

    /**
     * setTransparancy - sets the transparency of the component
     * @param i - a value between 0 - 100. 100 is not transparent, while 0 is transparent
     */
    public void setTransparency(int i){
    	if(i >=0 || i<=100){
    		transparency = (float)(i/100f);
    	}else{
    		System.out.println("Index out of bounds in setTransparency, the ImageScalingPanel"); // for debugginf remove later
    	}
    }
    
    // Decorator pattern 
    private class SensorComponent extends JPanel{
    	
    	/**
		 * 
		 */
		private static final long serialVersionUID = 51L;
		private JComponent comp;
    	public SensorComponent(JComponent comp){
    		super();
    		
    		comp.setOpaque(false);

    		setLayout(new BorderLayout());
    		this.comp = comp;
    		this.comp.setLocation(0,0);
    		add(this.comp,BorderLayout.CENTER);
    	}
    	
    	@Override
    	public void paint(Graphics g){

    		this.getComponent(0).setPreferredSize(new Dimension(getWidth(),getHeight()));
    		this.revalidate();
    		Graphics2D g2 = (Graphics2D) g.create();
    		
    		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
    				transparency)); 
    		super.paint(g2);
    		g2.dispose();
	
    	}
    }
    
}   
