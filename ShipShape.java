//package animation;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

/**
   A car that can be moved around.
* @author Ruby El Kharboulty 
 * @version 1.0
 * @date 11/15/2013
 */ 
public class ShipShape implements MoveableShape
{
   
	
    private double x; //the left of the bounding rectangle
	 private double y; //the top of the bounding rectangle  
    private double width; //the width of the bounding rectangle
    private double height;
    private boolean visible;
    /**
     * Car parts include body, frontTire, rearTire, and three lines to make the roof.
     */
    Ellipse2D.Double globe;
    Ellipse2D.Double body;
    Ellipse2D.Double head;
    Line2D.Double torso;
    Line2D.Double hand1;
    Line2D.Double hand2;
    ShapePanel panel;
    Color mainColor;
    Color alienColor;
    double xDir,yDir,vx,vy; //vx: speed in x direction | vy: speed in y direction

    /**
    Constructs a car item.
    @param x the left of the bounding rectangle
    @param y the top of the bounding rectangle
    @param width the width of the bounding rectangle
    */
    public ShipShape(double x, double y, double width, double height, double vx, double vy, Color mainColor, Color alienColor, ShapePanel panel)
   {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.panel = panel;
      this.vx = vx;
      this.vy = vy;
      
      visible = true;

      this.mainColor = mainColor;
      this.alienColor = alienColor;

     drawShip();

   }

   public void drawShip(){
      //Draw spaceship components
	   globe = new Ellipse2D.Double(x + width/2 - height/3 , y , 2*height/3, 2*height/3);
     body = new Ellipse2D.Double(x,5+ y + height/4, width , 3*height/4);
     torso = new Line2D.Double(x+width/2, y+2*height/3, x+width/2,y+height/4);
	   head = new Ellipse2D.Double(x+width/2-5,y+height/4-5,10,10);
     hand1 = new Line2D.Double(x+width/2,10+y+height/4+height/25,x+width/2-8-width/50,y+height/4+2);
     hand2 = new Line2D.Double(x+width/2,10+y+height/4+height/25,x+width/2+8+width/50,y+height/4+2);
   }

   public void setVisible(boolean visible){
    this.visible = visible;
   }

   public boolean isVisible(){
    return this.visible;
   }
   /**
    *  causes the spaceship to move by advancing the x and y location of the car
    */
   public void translate(double dx, double dy)
   {
      //Translate with the appropriate direction and speed
      x += dx*vx*panel.getDifficulty();
      y += dy*vy*panel.getDifficulty();

      //Check Colision and change direction approprietly
      if(x+width >= panel.getWidth() || x <= 0){
        vx *= -1;
      }
      if(y+height >= panel.getHeight() || y <= 0){
        vy *= -1;
      }

      //Draw again using the new x,y
      drawShip();
   }

   public boolean contains(Point p){
      return (body.contains(p) || globe.contains(p));
   }

   /**
    * Display all car parts. 
    * */
   public void draw(Graphics2D g2)
   { 
      if(!visible) return;
      //Draw body
      g2.setColor(mainColor);
      g2.fill(body);
	    
      //Draw outlines
      g2.setColor(Color.black);
      g2.setStroke(new BasicStroke(5));
      g2.draw(globe);
      g2.setStroke(new BasicStroke(2));
      g2.draw(body);
      
      //Draw globe
      g2.setColor(Color.white);
      g2.fill(globe);

      //Draw Alien
      g2.setColor(alienColor);
      g2.draw(torso);
      g2.fill(head);
      g2.draw(hand1);
      g2.draw(hand2);
      
   }
   
 
}
