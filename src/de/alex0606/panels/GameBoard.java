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
    private ArrayList<Listener> listener = new ArrayList<Listener>();
    public void addListener(Listener listener){
        this.listener.add(listener);
    }

    public StreetManager streetManager = new StreetManager(this);
    public ObstacleManager obstacles = new ObstacleManager(streetManager);

    public PlayerCar car = new PlayerCar(300, 400, streetManager);

    public boolean fuelBelowCar = true;

    public double score = 0;

    private Timer gameTimer;
    private Timer startTimer;
    private double startTimerTime;
    private double startTimerTimeMax = 5000;
    private double timeStart;
    private int timerSpeed = 10;

    public boolean gameOver = true;

    public GameBoard(){
        addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(new Color(0, 100, 0));
        setDoubleBuffered(true);

        //obstacles.addObstacle(new Object(150, 30, "res/barrier.png"));

        gameTimer = new Timer(timerSpeed, this);
        startTimer = new Timer(timerSpeed, this);
    }
    public void copy(GameBoard gameBoard){
        this.streetManager = gameBoard.streetManager;
        this.car = gameBoard.car;
        this.obstacles = gameBoard.obstacles;
        this.fuelBelowCar = gameBoard.fuelBelowCar;
        this.score = score;
    }

    public void initializeStart(){
        for(Listener listener : listener){
            listener.gameStartInitialized();
        }
        reset();
        startTimerTime = startTimerTimeMax;
        timeStart = System.currentTimeMillis();
        startTimer.start();
    }
    public void startGame(){
        for(Listener listener : listener){
            listener.gameStarted();
        }
        reset();
        gameTimer.start();
    }
    public void reset(){
        gameOver = false;
        obstacles = new ObstacleManager(streetManager);
        car.moveTo((getWidth()/2) - (car.getWidth()/2), getHeight() * 0.6);
    }
    public void gameOver(){
        gameOver = true;
        gameTimer.stop();
        for(Listener listener : listener){
            listener.gameOver();
        }
    }
    public boolean getGameOver(){
        return gameOver;
    }
    public void pause(){
        gameTimer.stop();
        startTimer.stop();
        for(Listener listener : listener){
            listener.gamePaused();
        }

    }
    public void continueGame(){
        if(startTimerTime > -1000) {
            startTimer.start();
        }else if(startTimerTime <= -1000){
            gameTimer.start();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(gameTimer)){
            obstacles.update(getHeight());
            car.update();
        }else if(e.getSource().equals(startTimer)){
            startTimerTime -= System.currentTimeMillis() - timeStart;
            timeStart = System.currentTimeMillis();

            if(startTimerTime < -1000){
                startTimer.stop();
                startGame();
            }
            car.fillFuel();
        }

        streetManager.update();

        if(!carOnRoad() || obstacles.checkObstacleCollision(car) || car.getFuel() <= 0){
            gameOver();
        }

        repaint();
    }
    public boolean carOnRoad(){
        Area streetArea = new Area(streetManager.getBoundingRect());
        streetArea.intersect(new Area(car.getBoundingRect()));

        if(streetArea.getBounds().getWidth() > car.getBoundingRect().getWidth() - 10 &&
                streetArea.getBounds().getHeight() > 10){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawStreet(g2d);

        //Hindernisse zeichnen
        obstacles.draw(g2d, this);

        //Auto zeichnen
        drawCar(g2d);

        //Tankfüllung zeichnen
        drawFuel(g2d);

        if(startTimer.isRunning()){
            drawStartCounter(g2d);
        }
    }
    public void drawStreet(Graphics2D g2d){
        //Straßen zeichnen
        streetManager.draw(g2d, this);

        //Seitenstreifen
        Rectangle sr = streetManager.getBoundingRect();
        Rectangle rect = new Rectangle(sr.x, sr.y - 20, sr.width, sr.height + 40);
        g2d.setColor(new Color(255, 255, 255));
        g2d.setStroke(new BasicStroke(4));
        g2d.draw(rect);
    }
    public void drawCar(Graphics2D g2d){
        if(car.isVisible())
            g2d.drawImage(car.getImage(), car.getX(), car.getY(), this);
        g2d.setColor(new Color(0x34B228));
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(car.getHitbox());
    }
    public void drawFuel(Graphics2D g2d){
        int width = 100;
        int height = 15;
        int x = 10;
        int y = 10;
            
        drawFuel(g2d, x, y, width, height, car.getFuelPercent());

        if(fuelBelowCar){
            x = car.getX() - (int)(width*MainWindow.scale) / 2 + car.getWidth()/2;
            y = car.getY() + car.getHeight() + 20;
            drawFuel(g2d, x, y, width, height, car.getFuelPercent());
        }
    }
    public void drawFuel(Graphics2D g2d, int x, int y, int width, int height, double fuelPercent){
    	width *= MainWindow.scale;
    	height *= MainWindow.scale;
    	g2d.setStroke(new BasicStroke(1));
    	g2d.setColor(new Color(0, 0, 0));
    	g2d.drawRect(x,  y,  width, height);
    	g2d.setColor(new Color((int)(255-fuelPercent*(255/100)), (int)(fuelPercent*(255/100)), 0));
    	g2d.fillRect(x+1, y+1, (int)(fuelPercent*(width/100D))-1, height-1);
    }
    public void drawStartCounter(Graphics2D g2d){
        String text = startTimerTime > 0 ? Integer.toString((int)startTimerTime/1000) : "Drive!!!";
        Font font = new Font("Arial", Font.BOLD, 40);
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics(font);
        int x = (getWidth() - metrics.stringWidth(text)) / 2;
        int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(text, x, y);
    }


    public void keyReleased(KeyEvent e){
        car.keyReleased(e);
    }
    public void keyPressed(KeyEvent e){
        car.keyPressed(e);
    }
    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            GameBoard.this.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            GameBoard.this.keyPressed(e);
        }
    }
}
