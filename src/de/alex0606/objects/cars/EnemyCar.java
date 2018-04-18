package de.alex0606.objects.cars;


import de.alex0606.StreetManager;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

public class EnemyCar extends Car{
    private static Area hitbox;
    private static String imagePath = "res/car.png";

    private int track = -1;
    private int targetTrack;
    private double speed;
    private int xEnd;
    private boolean changing = false;

    private StreetManager streetManager;

    public EnemyCar(double x, double y, StreetManager streetManager){
        super(x, y, EnemyCar.imagePath);
        initVars(streetManager, getHorizontalSpeed(), getVerticalSpeed(), track);
    }
    public EnemyCar(double x, double y, StreetManager streetManager, double horizontalSpeed, double verticalSpeed){
        super(x, y, EnemyCar.imagePath);
        initVars(streetManager, horizontalSpeed, verticalSpeed, track);
    }
    public EnemyCar(double x, double y, StreetManager streetManager, double horizontalSpeed, double verticalSpeed, int track){
        super(x, y, EnemyCar.imagePath);
        initVars(streetManager, horizontalSpeed, verticalSpeed, track);
    }
    private void initVars(StreetManager streetManager, double horizontalSpeed, double verticalSpeed, int track){
        this.streetManager = streetManager;
        setHorizontalSpeed(horizontalSpeed);
        setVerticalSpeed(verticalSpeed);
        this.track = track;
        if(hitbox == null) {
            hitbox = createPixelHitbox();
        }
        setHitboxArea(hitbox);
    }

    public void update(){
        moveRelative(getHorizontalSpeed(), getVerticalSpeed(), 0, streetManager.getSpeed());

        if(speed > 0 && changing){
            if(getX() >= xEnd){
                setHorizontalSpeed(0);
                track = targetTrack;
                changing = false;
            }
        } else{
            if(getX() <= xEnd){
                setHorizontalSpeed(0);
                track = targetTrack;
                changing = false;
            }
        }
    }

    public void setTrack(int track){
        this.track = track;
    }
    public int getTrack(){
        return track;
    }

    public boolean changeTrack(int targetTrack, double speed) {
        if(!changing){
            this.targetTrack = targetTrack;
            if(targetTrack < 0 || targetTrack >= streetManager.getHorizontalStreetCount() || targetTrack == track){
                return false;
            }
            changing = true;

            //Die geschwindigkeit muss negativ sein wenn der Spurwechsel nach links gehen soll und
            //positiv, wenn er nach rechts gehen soll
            this.speed =  speed * ((targetTrack-track) / Math.abs(targetTrack - track));
            setHorizontalSpeed(this.speed);  //horizontale Geschwindigkeit festlegen

            // Startposition und Endposition bestimmen bzw. festlegen
            int xStart = getX();
            xEnd = xStart + StreetManager.getSampleStreet().getWidth() * (targetTrack - track);
        }
        return true;
    }
    public int getNewRandomTrack(){
        int newTrack = ThreadLocalRandom.current().nextInt(0, streetManager.getHorizontalStreetCount());
        return newTrack;
    }
    public boolean isChanging(){
        return changing;
    }
}
