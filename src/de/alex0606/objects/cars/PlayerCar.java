package de.alex0606.objects.cars;

import de.alex0606.StreetManager;
import sun.awt.geom.AreaOp;
import sun.awt.geom.Curve;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PlayerCar extends Car{
    private static String imagePath = "res/playercar.png";
    private static Area hitbox;

    private double speedDefaultForward = -10;
    private double speedDefaultSlowDown = -2;
    private double speedDefaultHorizontal = 4;
    private double speedDefaultBase = -4;
    private StreetManager streetManager;

    private boolean keyLeft = false;
    private boolean keyRight = false;
    private boolean keyUp = false;
    private boolean keyDown = false;
    private String keyHorizontal = "none";
    private String keyVertical = "none";

    private static double fuelMax = 100;
    private static double fuelMaxDistance = 10000;
    private double fuel = fuelMax;

    public PlayerCar(double x, double y, StreetManager streetManager){
        super(x, y, PlayerCar.imagePath);
        this.streetManager = streetManager;
        updateSpeed();
        if(hitbox == null){
            hitbox = createPixelHitbox();
        }
        setHitboxArea(hitbox);
    }

    public double getFuel(){
        return Math.round(fuel);
    }
    public double getFuelPercent(){
        return getFuel()/(getFuelMax()/100);
    }
    public void fillFuel(){
        fuel = getFuelMax();
    }
    public void setFuel(double fuel){
        this.fuel = (fuel > getFuelMax()) ? getFuelMax() : fuel;
    }
    public static double getFuelMax(){
        return fuelMax;
    }
    public static double getFuelMaxDistance(){
        return fuelMaxDistance;
    }

    @Override
    public void moveRelativeVertical(double yDif, double verticalMovement) {
        super.moveRelativeVertical(yDif, verticalMovement);
        fuel -= Math.abs(yDif)*(getFuelMax()/ getFuelMaxDistance());
    }

    public void updateSpeed() {
        if (keyHorizontal.equals("left")){
            setHorizontalSpeed(-speedDefaultHorizontal);
        }
        else if (keyHorizontal.equals("right")) {
            setHorizontalSpeed(speedDefaultHorizontal);
        }
        else if(keyHorizontal.equals("none"))
            setHorizontalSpeed(0);

        if(keyVertical.equals("up"))
            setVerticalSpeed(speedDefaultForward);
        else if (keyVertical.equals("down"))
            setVerticalSpeed(speedDefaultSlowDown);
        else if(keyVertical.equals("none"))
            setVerticalSpeed(speedDefaultBase);

    }

    public void update() {
        moveRelative(getHorizontalSpeed(), getVerticalSpeed(), 0, streetManager.getSpeed());
    }

    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();

        if(key == KeyEvent.VK_LEFT)
            keyLeft = false;
        if(key == KeyEvent.VK_RIGHT)
            keyRight = false;
        if(key == KeyEvent.VK_UP)
            keyUp = false;
        if(key == KeyEvent.VK_DOWN)
            keyDown = false;

        if(key == KeyEvent.VK_LEFT)
            if (keyHorizontal.equals("left") && keyRight)
                keyHorizontal = "right";
            else if (keyHorizontal.equals("left"))
                keyHorizontal = "none";

        if(key == KeyEvent.VK_RIGHT)
            if (keyHorizontal.equals("right") && keyLeft)
                keyHorizontal = "left";
            else if (keyHorizontal.equals("right"))
                keyHorizontal = "none";

        if(key == KeyEvent.VK_UP)
            if (keyVertical.equals("up") && keyDown)
                keyVertical = "down";
            else if (keyVertical.equals("up"))
                keyVertical = "none";

        if(key == KeyEvent.VK_DOWN)
            if (keyVertical.equals("down") && keyUp)
                keyVertical = "up";
            else if (keyVertical.equals("down"))
                keyVertical = "none";

        updateSpeed();
    }
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();

        if(key == KeyEvent.VK_LEFT){
            keyHorizontal = "left";
            keyLeft = true;
        }
        if(key == KeyEvent.VK_RIGHT){
            keyHorizontal = "right";
            keyRight = true;
        }
        if(key == KeyEvent.VK_UP){
            keyVertical = "up";
            keyUp = true;
        }
        if(key == KeyEvent.VK_DOWN){
            keyVertical = "down";
            keyDown = true;
        }

        updateSpeed();
    }
}
