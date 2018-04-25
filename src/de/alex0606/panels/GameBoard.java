package de.alex0606.panels;

import de.alex0606.MainWindow;
import de.alex0606.ObstacleManager;
import de.alex0606.StreetManager;
import de.alex0606.objects.cars.*;

import javax.swing.*;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.event.*;
import java.util.ArrayList;


public class GameBoard extends JPanel implements ActionListener{
    private ArrayList<Listener> listeners = new ArrayList<Listener>(); //game listeners
    public void addListener(Listener listener){
        this.listeners.add(listener);
    } //add game listeners

    public StreetManager streetManager = new StreetManager(this); //manages road
    public ObstacleManager obstacles = new ObstacleManager(streetManager); //manages obstacles

    public PlayerCar car = new PlayerCar(300, 400, streetManager); //player car

    public boolean fuelBelowCar = true; //fuel gauge below player car

    public double score = 0; //saves the score (moved street distance)

    private Timer gameTimer; //Game loop timer
    private Timer startTimer; //start sequence timer
    private double streetSpeedTime = 0; //time since last street speedup
    private double startTimerTime; //runtime of starttimer
    private double startTimerTimeMax = 5000; //runtime of starttimer in milliseconds
    private double timeStart; //caches time to calculate time difference between game loop repetitions
    private int timerSpeed = 10; //speed of game loop and start sequence timer

    public boolean gameOver = true; //stores if game is over
    public boolean pause = false; //stores if game paused

    public GameBoard(){
        addKeyListener(new TAdapter()); //Listen to key events (if focused top level panel)
        setFocusable(true); //activate focus to get key events if top level panel
        setBackground(new Color(0, 100, 0)); //set Background color to green
        setDoubleBuffered(true); //enable doublebuffering (if it isn't enabled by default)

        gameTimer = new Timer(timerSpeed, this); //create game loop (timer)
        startTimer = new Timer(timerSpeed, this); //create start sequence timer
    }

    public void initializeStart(){ //start start sequenze with start timer
        for(Listener listener : listeners){
            listener.gameStartInitialized(); //send to all listeners, that start sequence is starting (start is initialized)
        }
        reset(); //reset all necessary
        startTimerTime = startTimerTimeMax; //start timer is at max
        timeStart = System.currentTimeMillis(); //get current time to calculate time difference
        startTimer.start(); //start timer for start sequence
    }
    public void startGame(){//start the game
        for(Listener listener : listeners){
            listener.gameStarted(); //call listeners, that game starts
        }
        reset(); //reset all necessary
        gameTimer.start(); //start game loop
    }
    public void reset(){
        setGameOver(false); //game is not over
        setPause(false); //and not paused
        obstacles = new ObstacleManager(streetManager); //reset obstacles
        car.moveTo((getWidth()/2) - (car.getWidth()/2), getHeight() * 0.6); //reset car position
        car.reset(); //reset car
        streetManager.resetSpeed(); //set road speed to default
        streetSpeedTime = 0;
        score = 0; //reset score
    }
    public void gameOver(){
        for(Listener listener : listeners){
            listener.gameOver(getScore()); //tell listeners that game is over (stopped)
        }
        setGameOver(true); //set gameOver true
        gameTimer.stop(); //stop game loop
    }
    public boolean getGameOver(){
        return gameOver;
    }
    public void setGameOver(boolean gameOver){
        this.gameOver = gameOver;
    }
    public void setScore(int score){
        this.score = score;
    }
    public int getScore(){
        return (int)score;
    }
    public void pause(){
        for(Listener listener : listeners){
            listener.gamePaused(); //tell listeners that game paused
        }
        gameTimer.stop(); //stop game loop
        setPause(true); //pause is true
        startTimer.stop(); //stop start sequence
    }
    public void setPause(boolean pause) {
        this.pause = pause;
    }
    public boolean isPause() {
        return pause;
    }

    public void continueGame(){
        if(startTimerTime > -1000) { //if start sequence was running before paused
            startTimer.start(); //continue start sequence
        }else{ //if start sequence was over
            gameTimer.start(); //restart game loop
        }
        setPause(false); //pause is false
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(gameTimer)){ //if in game loop
            score += streetManager.getSpeed()/10; //add streetmanager distance to score
            car.update(); //update car (move it)
            obstacles.update(getHeight()); //update all obstacles (move + generate)
            obstacles.updatePoliceCars(car);
            streetSpeedTime += System.currentTimeMillis() - timeStart; //increase time since last road speedup
            timeStart = System.currentTimeMillis(); //save time to calculate elapsed time
            if(streetSpeedTime >= 15000){ //if 15 seconds left since last road speedup
                if(streetManager.increaseSpeed(1)) { //if speed increase by 1 possible (speed of road below max speed)
                    car.speedBase -= 1; //speedup car
                    car.speedSlowDown -= 1;
                    car.speedForward -= 1;
                    car.updateSpeed(); //update new speed of player car
                }
                streetSpeedTime = 0; //time since last road speed is 0
            }
        }else if(e.getSource().equals(startTimer)){ //if in start sequence
            startTimerTime -= System.currentTimeMillis() - timeStart; //time left until start sequence stops
            timeStart = System.currentTimeMillis(); //save time to calculate elapsed time

            if(startTimerTime < -1000){ //if start timer reached 0 (start sequence is over)
                startTimer.stop(); //stop the start sequence timer
                startGame(); //start the game
            }
            car.fillFuel(); //fill fuel in start sequence
            car.moveTo((getWidth()/2) - (car.getWidth()/2), getHeight() * 0.6); //center car in startsequence
        }
        streetManager.update(); //update streetmanager (doesn't matter if in game loop or start sequence)

        repaint(); //repaint the gameBoard

        if(!carOnRoad() || obstacles.checkObstacleCollision(car) || car.getFuel() <= 0){ //if car is off road or collides with an obstacle or has no fuel
            gameOver(); //the game is over
        }


    }
    public boolean carOnRoad(){ //checks if car is on road
        Area streetArea = new Area(streetManager.getBoundingRect()); //bounding rect of street area
        streetArea.intersect(new Area(car.getBoundingRect()));

        if(streetArea.getBounds().getWidth() > car.getBoundingRect().getWidth()*0.95 &&
                streetArea.getBounds().getHeight() > car.getBoundingRect().getHeight()*0.05){
            return true; //if more than 5% of the car(height) and 95% (width) is on the road return true
        }
        else{
            return false; //else return false
        }
    }

    @Override
    public void paintComponent(Graphics g) { //repaint
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g; //cast Graphics to Graphics2D (more shapes)

        drawStreet(g2d); //draw Streets

        obstacles.draw(g2d); //draw obstacles
        g2d.setStroke(new BasicStroke(1));
        for(PoliceCar police : obstacles.getPolice()){
            g2d.draw(police.getForwardHitbox());
            g2d.draw(police.getLeftHitbox());
            g2d.draw(police.getRightHitbox());
        }

        drawCar(g2d); //draw car

        drawFuel(g2d); //draw fuel

        if(startTimer.isRunning())
            drawStartCounter(g2d); //if start timer is running, draw the start counter
        else
            drawScore(g2d); //if game is running draw the score
    }
    public void drawStreet(Graphics2D g2d){
        streetManager.draw(g2d); //draw road

        //draw side stripe
        Rectangle bounds = streetManager.getBoundingRect();
        Rectangle rect = new Rectangle(bounds.x, bounds.y - 20, bounds.width, bounds.height + 40);
        g2d.setColor(new Color(255, 255, 255));
        g2d.setStroke(new BasicStroke((int)(4*MainWindow.scale)));
        g2d.draw(rect);
    }
    public void drawCar(Graphics2D g2d){
        if(car.isVisible()) //if player car is visible
            g2d.drawImage(car.getImage(), car.getX(), car.getY(), this); //draw its image
    }
    public void drawFuel(Graphics2D g2d){
        int width = (int)(100*MainWindow.scale);
        int height = (int)(15*MainWindow.scale);
        int x = (int)(10*MainWindow.scale);
        int y = (int)(10*MainWindow.scale);
            
        drawFuel(g2d, x, y, width, height, car.getFuelPercent()); //draw fuel in upper left corner

        if(fuelBelowCar){ //if activated draw fuel below car
            x = car.getX() - width/ 2 + car.getWidth()/2;
            y = car.getY() + car.getHeight() + (int)(20*MainWindow.scale);
            drawFuel(g2d, x, y, width, height, car.getFuelPercent());
        }
    }
    public void drawFuel(Graphics2D g2d, int x, int y, int width, int height, double fuelPercent){ //draw fuel at given position
    	g2d.setStroke(new BasicStroke(1));
    	g2d.setColor(new Color(0, 0, 0));
    	g2d.drawRect(x,  y,  width, height); //border
    	g2d.setColor(new Color((int)(255-fuelPercent*(255/100)), (int)(fuelPercent*(255/100)), 0)); //if fuel high --> green, fuel low --> red
    	g2d.fillRect(x+1, y+1, (int)(fuelPercent*(width/100D))-1, height-1); //fuel (percent)
    }
    public void drawStartCounter(Graphics2D g2d){
        String text = startTimerTime > 0 ? Integer.toString((int)(startTimerTime/1000)+1) : "Drive!!!"; //if startcounter is 0, draw "Drive!!!" and not the counter
        Font font = new Font("Arial", Font.BOLD, (int)(40*MainWindow.scale)); //set font and compute centered position
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics(font);
        int x = (getWidth() - metrics.stringWidth(text)) / 2;
        int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.setColor(new Color(255,255,255));
        g2d.drawString(text, x, y); //draw text
    }
    public void drawScore(Graphics2D g2d){
        String text = Integer.toString((int)score); //get score as string
        Font font = new Font("Arial", Font.BOLD, (int)(40*MainWindow.scale)); //set font and compute position
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics(font);
        int x = (getWidth() - metrics.stringWidth(text) - (int)(50*MainWindow.scale));
        int y = metrics.getHeight();
        g2d.setColor(new Color(255,255,255));
        g2d.drawString(text, x, y); //draw score
    }


    public void keyReleased(KeyEvent e){
        car.keyReleased(e); //pass key event to playercar where they get handled
    }
    public void keyPressed(KeyEvent e){
        car.keyPressed(e); //pass key event to playercar where they get handled
    }
    private class TAdapter extends KeyAdapter { //Key listener
        @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_P)
                    pause();
                GameBoard.this.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            GameBoard.this.keyPressed(e);
        }
    }
}
