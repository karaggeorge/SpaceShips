

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.Random;
 
public class DisplayedText
{

    private String text, font;
    private double finishTime, duration, time;
    private int x,y,size,width,height;
    private double limit = 70;
    private Color color;
    private int animation;
    private double angle;

    /*
    Animation Codes:
    0 ---> None:      Just Fade Away
    1 ---> Combo:     Rotated, and pops up and down
    2 ---> Score:     Moves to the top while fading away
    3 ---> MultiHit:  Increases in size while fading away
    */

    //Constructor
    public DisplayedText(String text, String font, double time, double duration, int x, int y, int width, int height, int size, Color color, int animation)
   {
      this.text = text;
      this.font = font;
      this.time = time;
      this.duration = duration;
      this.finishTime = time + duration;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.size = size;
      this.color = color;
      this.animation = animation;

      //Depending on the animation, set the angle
      if(animation == 1){
        if(x < width/2)
          angle = 50;
        else
          angle = -50;
       }
       else{
        angle = 0;
       }
    }

    //Gets the percentage of the duration that is over by the current time
    private double getPercentage(double curTime){
        double perc = (curTime-time)/duration;
        perc *= 100.0;
        return perc;
    }

    //Getters
    public String getText(){
      return text;
    }

    public String getFont(){
      return font;
    }

    public int getAnimation(){
      return animation;
    }

    public double getFinishTime(){
      return finishTime;
    }

    //Getters that depend on Animation (check animation Codes at the begining of the code)
    public int getX(double curTime){
      if(animation == 3){
        return x-getSize(curTime);
      }
      else{
        return x;
      }
    }

    public int getY(double curTime){
      if(animation == 2){
        double perc = getPercentage(curTime);        
        if(perc <= 30) return height-size;
        else{
          return (int)((height-size) - 150*((perc-30.0)/70.0));
        }
      }
      else if(animation == 3){
        return y-getSize(curTime);
      }
      else{
        return y;
      }
    }

    public int getSize(double curTime){
      double perc = getPercentage(curTime);
      if(animation == 1) {
          if(perc < 25 || (perc>50 && perc<75)) return size;
          else return 3*size/2;
      }
      else if(animation == 3){
        return (int)((perc/100.0)*size*2 + size/2.0);
      }
      else{
        return size;
      }
    }

    public double getAngle(){
      return angle;
    }

    //Returns the color with increased transparency by (perc)%
    private Color transparentColor(Color color, double perc){
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)((1-perc)*255));
    }

    public Color getColor(double curTime){
      double perc = getPercentage(curTime);

      if(perc < limit) return color;
      else return transparentColor(color, (perc-limit)/(100.0-limit) );
    }
}
