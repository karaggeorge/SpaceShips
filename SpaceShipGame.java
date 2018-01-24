//package animation;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * This program displays an animation of a moving car on a jpanel. 
 * Main concepts : painting graphics and using a timer
 * @author Ruby El Kharboulty 
 * @version 1.0
 * @date 11/15/2013
 **/
@SuppressWarnings("serial")
public class SpaceShipGame extends JFrame
{
  /**
    Construct an AnimationTester object.
    @param title is the title of the JFrame
  */
   public SpaceShipGame(String title){
	   super(title);
	   
	   /**Create a ShapePanel and ControlPanel objects and add it to the frame.
	    */

	   ShapePanel sPanel = new ShapePanel();
	   ControlPanel cPanel = new ControlPanel(sPanel);
	   sPanel.setControlPanel(cPanel);
	   
	   //Create Custom Cursor and addsd it to the ShapePanel
	   ImageIcon crosshair = resizeImage(createImageIcon("/img/cursor.png"),16,16);
	   Dimension size = Toolkit.getDefaultToolkit().getBestCursorSize(16,16);
	   Cursor targetCursor = Toolkit.getDefaultToolkit().createCustomCursor(crosshair.getImage(),new Point(size.width/2,size.height/2), "target cursor");

	   sPanel.setCursor(targetCursor);


	   this.add(sPanel, BorderLayout.CENTER);
	   this.add(cPanel,BorderLayout.NORTH);
	  
	    

	    /** Standard code for all graphical apps. 
	     */
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.pack();
	    this.setVisible(true);
              

   }
   	
   ImageIcon resizeImage(ImageIcon img, int x, int y){
      return new ImageIcon(img.getImage().getScaledInstance(x,y,Image.SCALE_SMOOTH));
   	}

   /** Returns an ImageIcon, or null if the path was invalid. 
    * A good encapsulation for the validation and retrival of the image file. error */
   private static ImageIcon createImageIcon(String path) {
       java.net.URL imgURL = ControlPanel.class.getResource(path);
       if (imgURL != null) {
           return new ImageIcon(imgURL);
       } else {
           System.err.println("Couldn't find file: " + path);
           return null;
       }
   }

   public static void main(String[] args)
    {
        new SpaceShipGame("SpaceShips");
 
    }
 
 
}