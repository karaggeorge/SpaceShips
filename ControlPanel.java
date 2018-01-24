	//package animation;

import java.awt.BorderLayout;
import javax.swing.*;
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

public class ControlPanel extends javax.swing.JPanel {
	
	private JButton startBtn, pauseBtn, soundBtn;
	private JLabel scoreLabel, hitsLabel, livesLabel;
	private int hits, score, lives, comboMult = 1;
	private JPanel livesPanel;
	private boolean paused = false, over = true, muted = false;
	private ShapePanel sPanel;
	private ImageIcon soundOnIcon, soundOffIcon;

	public ControlPanel(ShapePanel sPanel) {
		   super();	   
		   this.sPanel = sPanel;
		   this.setLayout(new BorderLayout());
		   	
		   soundOnIcon = resizeImage(createImageIcon("/img/soundOn.png"),30,30);
		   soundOffIcon = resizeImage(createImageIcon("/img/soundOff.png"),30,30);

		   //Create the Button panel that contains the Buttons
		   JPanel buttonPanel = new JPanel(new BorderLayout());
		   startBtn = new JButton("Start");
		   pauseBtn = new JButton("  Pause  ");
		   soundBtn = new JButton(soundOnIcon);
		   pauseBtn.setEnabled(false);
		   buttonPanel.add(startBtn, BorderLayout.WEST);
		   buttonPanel.add(pauseBtn, BorderLayout.CENTER);
		   buttonPanel.add(soundBtn, BorderLayout.EAST);

		   //Create the Label Panel that displays Score, Lives and Hits
		   JPanel labelPanel = new JPanel(new BorderLayout());
		   scoreLabel = new JLabel("Score:  0    ");
		   hitsLabel = new JLabel("Hits:  0    ");
		   livesLabel = new JLabel("Lives:  10    ");
		   labelPanel.add(scoreLabel, BorderLayout.EAST);
		   labelPanel.add(hitsLabel, BorderLayout.CENTER);
		   labelPanel.add(livesLabel, BorderLayout.WEST);
		   
		   //Create the lives panel that displays the lives (visual representation)
		   livesPanel = new JPanel();
		   updateLives(0);

		   startBtn.addActionListener(new ActionListener(){
        		public void actionPerformed(ActionEvent e){ //Start or End the game
        			if(over){
        				start();	        		
        			}
	        		else{
	        			end();
	        		}
        		}
           });

           soundBtn.addActionListener(new ActionListener(){
        		public void actionPerformed(ActionEvent e){ //Start or End the game
        			if(muted){
   						soundBtn.setIcon(soundOnIcon);     				
   	        		}
	        		else{
	        			soundBtn.setIcon(soundOffIcon);
	        		}
	        		muted = !muted;
	        		sPanel.setMuted(muted);
        		}
           });

           pauseBtn.addActionListener(new ActionListener(){
        		public void actionPerformed(ActionEvent e){ //Pause or Resume the game
        			if(paused){
        				pauseBtn.setText("  Pause  ");	
        			}
        			else{
        				pauseBtn.setText("Resume");
        			}
        			paused = !paused;
        			sPanel.pause();
        		}
           });

		   this.add(buttonPanel, BorderLayout.WEST);
		   this.add(livesPanel,	BorderLayout.CENTER);
		   this.add(labelPanel, BorderLayout.EAST);

	}

	//Updates score and hits when a ship is hit. Also displays score animated string
	public void hit(int curHits, double curTime){
		if(over) return;
		hits += curHits;
		score += 100*curHits*comboMult;
		refreshLabels();
		sPanel.displayText("+ " + 100*curHits*comboMult + " !","Harlow Solid Italic",curTime,.05,20,0,40,new Color(0,255,0),2);
		changeDif();
	}

	//Changes Difficulty as the player hits increase
	private void changeDif(){
		double dif = sPanel.getSizeDifficulty();
		dif = Math.max(dif- 0.02*((int)hits/20),0.9);
		sPanel.setSizeDifficulty(dif);
	}

	//Updates the combo multiplier
	public void updateCombo(int comboMult){
		this.comboMult = comboMult;
	}

	//Updates score and animated string after a multi-hit
	public void multi(int mHits, double curTime){
		score += mHits*20;
		sPanel.displayText("Multi-Kill!","Eras Bold ITC",curTime,.08,sPanel.getWidth()/3,sPanel.getHeight()/3,45,new Color(255,0,128),3);
		sPanel.displayText("+ " + mHits*20 + " !","Harlow Solid Italic",curTime,.05,20,0,40,new Color(0,255,0),2);
	}

	//Updates the score after a miss-click
	public void miss(double curTime){
		if(over) return;
		score = Math.max(0,score-50);
		sPanel.displayText("- " + 50 + " !","Harlow Solid Italic",curTime,.05,20,0,40,new Color(255,0,0),2);
		lives--;
		if(lives == 0){ //If there are no more lives left, lose
			sPanel.lose();
		}
		refreshLabels();
	}

	//Returns score
	public int getScore(){
		return score;
	}

	//Refreshes the Labels with the appropriate values
	private void refreshLabels(){
		scoreLabel.setText("Score:  " + score + "    ");
		hitsLabel.setText("Hits:  " + hits + "    ");
		livesLabel.setText("Lives:  " + lives + "    ");
		updateLives(lives);
	}

	//Starts the game and resets values
	private void start(){
		over = false;
		startBtn.setText("Stop");
		pauseBtn.setEnabled(true);
		sPanel.start();
		hits = 0;
		score = 0;
		lives = 10;
		refreshLabels();
	}

	//Ends game and resets values
	private void end(){
		over = true;
		startBtn.setText("Start");
	    pauseBtn.setEnabled(false);
	    sPanel.stop();
	    lives = 10;
	    hits = 0;
	    score = 0;
	    refreshLabels();
	    updateLives(0);
	}

	//Win the game. Calculates and displayes extra points additions
	public void win(double timeLeft, double curTime){
		sPanel.displayText("+ " + (int)timeLeft*100 + " for time left!","Harlow Solid Italic",curTime,.5,20,sPanel.getHeight()/2+10,40,new Color(0,128,0),0);	
		sPanel.displayText("+ " + lives*500 + " for lives left!","Harlow Solid Italic",curTime,.5,20,sPanel.getHeight()/2+60,40,new Color(0,128,0),0);	
		score += (int)timeLeft*100 + lives*500;
		refreshLabels();
		over = true;
		startBtn.setText("Start");
	    pauseBtn.setEnabled(false);
	}

	//Lose the game. Calculates and displays extra points deductions
	public void lose(int shipsLeft, double curTime){
		sPanel.displayText("- " + shipsLeft*100 + " for ships left!","Harlow Solid Italic",curTime,.5,20,sPanel.getHeight()/2+60,40,new Color(128,0,0),0);	
		score = Math.max(0,score-shipsLeft*100);
		refreshLabels();
		over = true;
		startBtn.setText("Start");
	    pauseBtn.setEnabled(false);
	}

	//Updates lives panel
	private void updateLives(int n){
		ImageIcon heartIcon = resizeImage(createImageIcon("/img/heart.png"), 30, 30);
		ImageIcon blankIcon = resizeImage(createImageIcon("/img/blank.png"), 30, 30);
		
		livesPanel.removeAll(); //Reset it
		livesPanel.setLayout(new GridLayout(1,30)); //Create a new GridLayout with 30 Columns
		
		for(int i = 0;i < 10;i++){ //Fill the first 10 with blank icons
			livesPanel.add(new JLabel(blankIcon));
		}
		for(int i = 0;i < n;i++){ //Fill the next (lives) with heart icons
			livesPanel.add(new JLabel(heartIcon));
		}
		for(int i = 0;i < 20-n;i++){ //Fill the rest with blank icons
			livesPanel.add(new JLabel(blankIcon));	
		}
	}

	//Resizes an ImageIcon
	ImageIcon resizeImage(ImageIcon img, int x, int y){
      return new ImageIcon(img.getImage().getScaledInstance(x,y,Image.SCALE_SMOOTH));
   	}

   /** Returns an ImageIcon, or null if the path was invalid. */
   private static ImageIcon createImageIcon(String path) {
       java.net.URL imgURL = ControlPanel.class.getResource(path);
       if (imgURL != null) {
           return new ImageIcon(imgURL);
       } else {
           System.err.println("Couldn't find file: " + path);
           return null;
       }
   }

}
