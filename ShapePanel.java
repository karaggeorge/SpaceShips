	//package animation;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import javax.sound.sampled.*;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.io.*;
import java.lang.Math;
import sun.audio.*;
import java.applet.*;

import javax.swing.Timer;

public class ShapePanel extends javax.swing.JPanel {
	
	private final int DELAY = 1; // Milliseconds between timer ticks
	private static final int MAX_SHIP_WIDTH = 200, MAX_SHIP_HEIGHT = 100,PANEL_WIDTH = 1200, PANEL_HEIGHT=600, MAX_SPEED = 1, LAZER = 1, EXPLOSION = 2;
    private Timer t;
    private double time, prevTime, lastTime, difficulty, comboTime, maxTime, sizeDifficulty = 2.0;
    private int hits, comboMult;
	private Random rand = new Random();
	private List<ShipShape> ships = new ArrayList<ShipShape> ();
	private List<DisplayedText> displayedStrings = new ArrayList<DisplayedText> ();
	private AudioStream lazerClip, explosionClip, backgroundClip, cheerClip, booClip;
	private ContinuousAudioDataStream backgroundMusic;
	private boolean gameOver, gamePaused, gameStarted = false, muted = false;
	private ControlPanel cPanel;
	private String message;

	public ShapePanel() {
		   super();	   
		   this.setBackground(java.awt.Color.white);
		   this.setPreferredSize(new java.awt.Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		   this.setSize(new java.awt.Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		   
		   //Load Music Files
		   loadMusic();

		   this.addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent e){
					//Play Lazer sound on mouse click
					playSound(LAZER);

					if(gamePaused || gameOver) return;
								
					Point mousePos = e.getPoint();
					//Count number of hits and remove hit ships
					int hits = 0;
					for(int i = ships.size()-1;i >= 0;i--){
						if(ships.get(i).contains(mousePos)){
							ships.remove(i);
							hits++;
						}
					}
					//If something was hit, gain points, play sound and update UI
					if(hits > 0){
						playSound(EXPLOSION);
						//Update Combo 
						checkCombo(time);
						//Update score/UI
						cPanel.hit(hits, time);
						//If hit more than one, advance to multi-hit
						if(hits > 1) {
							cPanel.multi(hits,time);
						}
					}
					else{ //If nothing was hit, lose points, update UI
						cPanel.miss(time);
						checkCombo(-1.0);
					}

				}
			});	    

			this.addKeyListener(new KeyListener(){
				//Change difficulty with up and down arrow keys (reduces/increases ship speed)
				public void keyPressed(KeyEvent e) {
					if(gamePaused || gameOver) return;
					int id = e.getKeyCode();
					if(id == KeyEvent.VK_DOWN) difficulty = Math.max(difficulty - 0.1 , 0.1);
					else if(id == KeyEvent.VK_UP) difficulty = Math.min(difficulty + 0.1 , 1.0);
				}

				public void keyReleased(KeyEvent e) {}

				public void keyTyped(KeyEvent e) {}
				
			});

			setFocusable(true);
		    /** Create a timer object and create an Anonymous class that implements the AcitonListener interface and
		     * the actionPerformed method.Each time the timer ticks, the actionPerformed method is called.
		     **/
		    t = new Timer(DELAY, new
		    
		    ActionListener()
		    {
		       public void actionPerformed(ActionEvent event)
		       {
		       	  time += DELAY/1000.0; //Keep track of time in seconds
				  

		       	  if(!gameOver && !gamePaused){
			       	  //Translate ships
			       	  for(ShipShape i: ships){
			       	  		i.translate(MAX_SPEED,MAX_SPEED);
			       	  }

			          if(time-prevTime >= 0.15){ //Ever 1.5 seconds
			          	createNewShips(2);   //Add 2 new ships to the game
			          	prevTime = time;     //Set previously added time to current time
			          }

			          repaint();
			          gameOver = (ships.size() == 0)||(time>maxTime); //Check if game is over (ships destroyed or time ran out)		       	 	

			      }
			      
			      if(gameOver) {
			      	if(ships.size() == 0){ //If ships are done, win
			      		win();
			      	}
			      	else{                  //If time ran out or lives are done, lose
			      		lose();
			      	}
			      }

		       
		       }

		    });

	}

	//Play sound depending on code
	private void playSound(int n){ //n is the sound code 
		if(muted) return;
		if(n == LAZER){
			AudioPlayer.player.stop(lazerClip);
		    try{
		   		lazerClip = new AudioStream( new FileInputStream("lazer.wav"));
		    }
		    catch(Exception e){
		   		System.out.println("Something went wrong! " + e);
		    }
			AudioPlayer.player.start(lazerClip);
		}
		else if(n == EXPLOSION){
			AudioPlayer.player.stop(explosionClip);
		    try{
		   		explosionClip = new AudioStream( new FileInputStream("explosion2.wav"));
		    }
		    catch(Exception e){
		   		System.out.println("Something went wrong! " + e);
		    }
			AudioPlayer.player.start(explosionClip);
		}
	} 

	//Win: display appropriate scores/messages and stop the gameplay
	private void win(){
		t.stop();
		cPanel.win((maxTime-time)*10,time);
		gameOver = true;
		if(!muted){
			AudioPlayer.player.stop(backgroundMusic);
			AudioPlayer.player.start(cheerClip);
		}
		message = "You Win!";
		repaint();
	}

	//Lose: display appropriate scores/messages and stop the gameplay
	public void lose(){
		t.stop();
		cPanel.lose(ships.size(),time);
		gameOver = true;
		if(!muted){
			AudioPlayer.player.stop(backgroundMusic);
			AudioPlayer.player.start(booClip);
		}
		message = "You Lose!";
		repaint();
	}

	//Add the specified number of ships to the game
	private void createNewShips(int n){
		for(int i = 0;i < n;i++){
		   		//Random Starting point
		   		double x0 = randomNumber(0,PANEL_WIDTH - MAX_SHIP_WIDTH);
		   		double y0 = randomNumber(0,PANEL_HEIGHT - MAX_SHIP_HEIGHT);

		   		//Random size, not larger than maximum size
		    	double size = randomNumber(0.5,1)*sizeDifficulty; //SizeDifficulty decreases the ship size as time goes by

		    	//Random Speed in both directions
		    	double speedX = randomSign(randomNumber(0.2,0.9));
		    	double speedY = randomSign(randomNumber(0.2,0.9));

		    	//Add new ship to the List
		    	ships.add(new ShipShape(x0,y0,size*MAX_SHIP_WIDTH,size*MAX_SHIP_HEIGHT,speedX,speedY,randomColor(),randomColor(),this));
		   }
	}

	//Load sound files
	private void loadMusic(){
		try{
		   		lazerClip = new AudioStream( new FileInputStream("lazer.wav"));
		   		explosionClip = new AudioStream( new FileInputStream("explosion.wav"));
		   		cheerClip = new AudioStream( new FileInputStream("cheer.wav"));
		   		booClip = new AudioStream( new FileInputStream("boo.wav"));

		   		backgroundClip = new AudioStream( new FileInputStream("background.wav"));
		   		backgroundMusic = new ContinuousAudioDataStream( backgroundClip.getData());
		   }
		   catch(Exception e){
		   	System.out.println("Something went wrong! " + e);
		   }

	}

	//If you hit two ships within a second you get combo multiplier bonus on your score
	public void checkCombo(double curTime){
		if(curTime < 0){ //If you miss, you lose the combo Multiplier
			lastTime = 0;
			comboMult = 1; //Reset
			cPanel.updateCombo(comboMult);
			return;
		}

		if(curTime-lastTime < comboTime){ //If the hit is within a (comboTime) of the previous hit 
			comboMult = Math.min(comboMult*2,32);
			for(int i = displayedStrings.size()-1;i >= 0;i--){     //Remove other combo animated strings to display the new one
				if(displayedStrings.get(i).getAnimation() == 1){
					displayedStrings.remove(i);
				}
			}
			//Display combo animated String
			displayText("COMBOx" + comboMult + "!","Showcard Gothic",curTime,.08,30,100,25,new Color(255,0,0),1);
		}
		else{
			comboMult = 1;
		}

		lastTime = curTime; //Update last hit time 
		cPanel.updateCombo(comboMult);
	}

	//Start the game. Updates and refreshes all variables and starts the game
	public void start(){
		ships.clear();
		displayedStrings.clear();
		sizeDifficulty = 1.0;
		createNewShips(10);
		time = 0.0;
		difficulty = 1.0;
		maxTime = 3;
		comboTime = 0.1;
		comboMult = 1;
		lastTime = -10.0;
		prevTime = 0.0;
		gameOver = false;
		gamePaused = false;
		t.start();
		gameStarted = true;
		if(!muted) AudioPlayer.player.start(backgroundMusic);
	}

	//Stops the game
	public void stop(){
		AudioPlayer.player.stop(backgroundMusic);
		ships.clear();
		displayedStrings.clear();
		time = maxTime;
		gameOver = false;
		gamePaused = true;
		repaint();
	}

	//Pauses and Resumes the game
	public void pause(){
		gamePaused = !gamePaused;
		if(gamePaused) {
			t.stop();
			AudioPlayer.player.stop(backgroundMusic);
		}
		else{
			t.start();
			if(muted) return;
			AudioPlayer.player.start(backgroundMusic);
		}
	}

	//Returns a random double number in the range [a,b)
	private double randomNumber(double a, double b){
		return rand.nextDouble()*(b-a) + a;
	}

	//Reuturns either n or -n 
	private double randomSign(double n){
		return n * ((rand.nextInt(100)%2)*2 - 1);
	}

	//Returns a random RGB Color
	private Color randomColor(){
		return new Color(rand.nextInt(255),rand.nextInt(255),rand.nextInt(255));
	}

	//Adds a String to the Animated String list to be displayed
	//Accepts the text, the font of the text, the time the commad was initiated, the duration for which the string should be displayed
	//The position (x,y), the size, the color, and the animation type
	public void displayText(String text, String font, double time, double duration, int x, int y, int size, Color color, int animation){
		//Creates and adds the DisplayedText object with all the appropriate information in the list 
		displayedStrings.add(new DisplayedText(text,font,time,duration,x,y,this.getWidth(),this.getHeight(),size,color,animation));
	}

	//Override paintComponent
	public void paintComponent(Graphics g) {
		   super.paintComponent(g);
		   Graphics2D brush = (Graphics2D) g;
		   
		 	//Draw all ships
		   if(!gameOver) {
			   for(MoveableShape i: ships){
			   		i.draw(brush);
			   }
			}

			//Draw time
		   brush.setFont(new Font("Serif",Font.PLAIN,20));
		   brush.setColor(Color.black);
		   brush.drawString("Time: " + String.format("%.1f",Math.max(maxTime*10-time*10,0)), this.getWidth()-100,30);

		   //Check and remove animated strings that are over (their duration is done)
		   for(int i = displayedStrings.size()-1;i >= 0;i--){
				if(displayedStrings.get(i).getFinishTime() < time){
					displayedStrings.remove(i);
				}
			}

			//Display animated Strings
		   for(DisplayedText i: displayedStrings){
		   		double angle = i.getAngle();
		   		brush.rotate(angle,i.getX(time),i.getY(time)); //Set angle of the String

		   		brush.setFont(new Font(i.getFont(), Font.PLAIN, i.getSize(time)));
		   		brush.setColor(i.getColor(time));
		   		brush.drawString(i.getText(),i.getX(time),i.getY(time));
		   		
		   		brush.rotate(-angle,i.getX(time),i.getY(time)); //Reset angle of the brush
		   }

		   //If game is over, display appropriate message and score
		   if(gameOver){
		   		brush.setFont(new Font("Serif",Font.PLAIN,90));
		   		brush.setColor(Color.black);
		   		brush.drawString(message,this.getWidth()/3,this.getHeight()/2);
		   		brush.setFont(new Font("Serif",Font.PLAIN,60));
		   		brush.drawString("Score: " + cPanel.getScore(),this.getWidth()/3,2*this.getHeight()/3);
		   }
	}
	
	//Returns the difficulty
	public double getDifficulty(){
		return difficulty;
	}

	//Sets the control panel
	public void setControlPanel(ControlPanel cPanel){
		this.cPanel = cPanel;
	}

	//Returns the sizeDifficulty
	public double getSizeDifficulty(){
		return sizeDifficulty;
	}

	//Sets the sizeDifficulty
	public void setSizeDifficulty(double dif){
		sizeDifficulty = dif;
	}

	//Sets Muted or not
	public void setMuted(boolean mute){
		muted = mute;
		if(muted){
			AudioPlayer.player.stop(backgroundMusic);
		}
		else{
			if(gameOver||gamePaused||(!gameStarted)) return;
			AudioPlayer.player.start(backgroundMusic);	
		}
	}
	   
}
