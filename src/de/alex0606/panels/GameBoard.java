package de.alex0606.panels;

import de.alex0606.ObstacleManager;
import de.alex0606.StreetManager;
import de.alex0606.objects.cars.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.event.*;
import java.util.ArrayList;


public class GameBoard extends JPanel implements ActionListener{
    private ArrayList<GameBoardListener> gameBoardListeners = new ArrayList<GameBoardListener>();
    public void addListener(GameBoardListener listener){
        gameBoardListeners.add(listener);
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

    public void initializeStart(){
        for(GameBoardListener listener : gameBoardListeners){
            listener.gameStartInitialized();
        }
        reset();
        startTimerTime = startTimerTimeMax;
        timeStart = System.currentTimeMillis();
        startTimer.start();
    }
    public void startGame(){
        for(GameBoardListener listener : gameBoardListeners){
            listener.gameStarted();
        }
        reset();
        gameTimer.start();
    }
    public void reset(){
        obstacles = new ObstacleManager(streetManager);
        car.moveTo((getWidth()/2) - (car.getWidth()/2), getHeight() * 0.6);
    }
    public void gameOver(){
        gameTimer.stop();
        for(GameBoardListener listener : gameBoardListeners){
            listener.gameOver();
        }
    }
    public boolean getGameOver(){
        return gameOver;
    }
    public void pause(){
        gameTimer.stop();
        for(GameBoardListener listener : gameBoardListeners){
            listener.gamePaused();
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
        for(EnemyCar enemy : obstacles.getEnemys()){
            g2d.draw(enemy.getHitbox());
        }

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
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(new Color(0xFFFB17));
        g2d.drawRect(10, 10, 101,20);
        g2d.setColor(new Color((int)(255 - (car.getFuelPercent() * (255/100))), (int)(car.getFuelPercent() * (255/100)),0));
        g2d.fillRect(11, 11, (int)car.getFuelPercent(), 19);

        if(fuelBelowCar){
            int fuel = (int)car.getFuel();
            int widht = 100;
            int height = 15;
            int x = car.getX() - widht / 2 + car.getWidth()/2;
            int y = car.getY() + car.getHeight() + 20;
            //Tankfüllung zeichnen
            g2d.setStroke(new BasicStroke(1));
            g2d.setColor(new Color(0xFFFB17));
            g2d.drawRect(x, y, widht + 1,height);
            g2d.setColor(new Color(0, 255, 0));
            g2d.setColor(new Color((int)(255 - (car.getFuelPercent() * (255/100))), (int)(car.getFuelPercent() * (255/100)),0));
            g2d.fillRect(x + 1, y + 1, (int)(fuel)*(widht/100), height - 1);
        }
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
