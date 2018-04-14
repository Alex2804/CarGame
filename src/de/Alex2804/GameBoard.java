package de.Alex2804;

import de.Alex2804.objects.*;
import de.Alex2804.objects.Object;
import de.Alex2804.objects.cars.*;
import org.w3c.dom.css.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.*;


public class GameBoard extends JPanel implements ActionListener{
    private StreetManager streetManager = new StreetManager(this);
    private ObstacleManager obstacles = new ObstacleManager(streetManager);

    private PlayerCar car = new PlayerCar(300, 400, streetManager);

    private Timer timer;
    private boolean startTimer;


    public GameBoard(){
        addKeyListener(new TAdapter());
        addComponentListener(new ResizeListener());
        setFocusable(true);
        setBackground(new Color(0, 100, 0));
        setDoubleBuffered(true);

        //obstacles.addObstacle(new Object(150, 30, "res/barrier.png"));

        timer = new Timer(10, this);
        startTimer = true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //Move all streetManager
        streetManager.update();

        obstacles.update(getHeight());

        car.update();

        if(!carOnRoad() || obstacles.checkObstacleCollision(car)){
            timer.stop();
        }

        repaint();
    }
    private boolean carOnRoad(){
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

        paintStuff(g2d);
    }
    public void paintStuff(Graphics2D g2d){
        //Straßen zeichnen
        streetManager.draw(g2d, this);

        //Hindernisse zeichnen
        obstacles.draw(g2d, this);

        //Seitenstreifen
        Rectangle sr = streetManager.getBoundingRect();
        Rectangle rect = new Rectangle(sr.x, sr.y - 20, sr.width, sr.height + 40);
        g2d.setColor(new Color(255, 255, 255));
        g2d.setStroke(new BasicStroke(4));
        g2d.draw(rect);

        //Spielerauto zeichnen
        g2d.drawImage(car.getImage(), car.getX(), car.getY(), this);

        //Spielerauto-Hitbox zeichnen
        g2d.setColor(new Color(255, 0, 0));
        g2d.setStroke(new BasicStroke(1));
        g2d.draw(car.getBoundingRect());

        g2d.setColor(new Color(0, 255, 0));
        g2d.draw(car.getHitbox());

        g2d.setColor(new Color(0xFFFB17));
        g2d.drawRect(10, 10, 101,20);
        g2d.setColor(new Color(0x34B228));
        g2d.fillRect(11, 11, (int)car.getFuelPercent(), 19);
    }

    class ResizeListener extends ComponentAdapter {
        public void componentResized(ComponentEvent e) {
            if(startTimer == true) {
                timer.start();
                startTimer = false;
            }
        }
    }


    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            car.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            car.keyPressed(e);
        }
    }

    private class GameLoop implements Runnable{
        @Override
        public void run() {
            long beforeTime, timeDiff, sleep;
            int delay = 10;

            beforeTime = System.currentTimeMillis();

            while (true){


                timeDiff = System.currentTimeMillis() - beforeTime;
                sleep = delay - timeDiff;

                if (sleep < 0) {
                    sleep = 2;
                }

                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {

                    String msg = String.format("Thread interrupted: %s", e.getMessage());
                    System.out.println(msg);
                }

                beforeTime = System.currentTimeMillis();
            }


        }
    }
}
