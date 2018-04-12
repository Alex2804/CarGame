package de.Alex2804.objects.cars;

import de.Alex2804.StreetManager;
import de.Alex2804.objects.Street;

import java.awt.event.KeyEvent;

public class PlayerCar extends Car{
    private static String imagePath = "res/car.png";

    double forwardSpeedDefault = -10;
    double slowDownSpeedDefault = -2;
    double horizontalSpeedDefault = 3;
    double baseSpeed = -4;
    double angle = 0;
    StreetManager streetManager;

    boolean leftKey = false;
    boolean rightKey = false;
    boolean upKey = false;
    boolean downKey = false;
    String horizontalKey = "none";
    String verticalKey = "none";

    static double maxFuel = 100;
    static double maxDistance = 10000;
    double fuel = maxFuel;

    public PlayerCar(double x, double y, StreetManager streetManager){
        super(x, y, PlayerCar.imagePath);
        this.streetManager = streetManager;
        updateSpeed();
    }

    public double getFuel(){
        return Math.round(fuel);
    }
    public double getFuelPercent(){
        return getFuel()/(getMaxFuel()/100);
    }
    public void fillFuel(){
        fuel = getMaxFuel();
    }
    public void setFuel(double fuel){
        this.fuel = (fuel > getMaxFuel()) ? getMaxFuel() : fuel;
    }
    public static double getMaxFuel(){
        return maxFuel;
    }
    public static double getMaxDistance(){
        return maxDistance;
    }

    @Override
    public void moveRelativeVertical(double yDif, double verticalMovement) {
        super.moveRelativeVertical(yDif, verticalMovement);
        fuel -= Math.abs(yDif)*(getMaxFuel()/getMaxDistance());
    }

    public void updateSpeed() {
        if (horizontalKey.equals("left")){
            setHorizontalSpeed(-horizontalSpeedDefault);
        }
        else if (horizontalKey.equals("right")) {
            setHorizontalSpeed(horizontalSpeedDefault);
        }
        else if(horizontalKey.equals("none"))
            setHorizontalSpeed(0);

        if(verticalKey.equals("up"))
            setVerticalSpeed(forwardSpeedDefault);
        else if (verticalKey.equals("down"))
            setVerticalSpeed(slowDownSpeedDefault);
        else if(verticalKey.equals("none"))
            setVerticalSpeed(baseSpeed);

    }

    public void update() {
        moveRelative(getHorizontalSpeed(), getVerticalSpeed(), 0, streetManager.getSpeed());
    }



    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();

        if(key == KeyEvent.VK_LEFT)
            leftKey = false;
        if(key == KeyEvent.VK_RIGHT)
            rightKey = false;
        if(key == KeyEvent.VK_UP)
            upKey= false;
        if(key == KeyEvent.VK_DOWN)
            downKey = false;

        if(key == KeyEvent.VK_LEFT)
            if (horizontalKey.equals("left") && rightKey)
                horizontalKey = "right";
            else if (horizontalKey.equals("left"))
                horizontalKey = "none";

        if(key == KeyEvent.VK_RIGHT)
            if (horizontalKey.equals("right") && leftKey)
                horizontalKey = "left";
            else if (horizontalKey.equals("right"))
                horizontalKey = "none";

        if(key == KeyEvent.VK_UP)
            if (verticalKey.equals("up") && downKey)
                verticalKey = "down";
            else if (verticalKey.equals("up"))
                verticalKey = "none";

        if(key == KeyEvent.VK_DOWN)
            if (verticalKey.equals("down") && upKey)
                verticalKey = "up";
            else if (verticalKey.equals("down"))
                verticalKey = "none";

        updateSpeed();
    }
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();

        if(key == KeyEvent.VK_LEFT){
            horizontalKey = "left";
            leftKey = true;
        }
        if(key == KeyEvent.VK_RIGHT){
            horizontalKey = "right";
            rightKey = true;
        }
        if(key == KeyEvent.VK_UP){
            verticalKey = "up";
            upKey = true;
        }
        if(key == KeyEvent.VK_DOWN){
            verticalKey = "down";
            downKey = true;
        }

        updateSpeed();
    }
}
