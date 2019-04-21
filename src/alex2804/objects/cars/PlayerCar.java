package alex2804.objects.cars;

import alex2804.StreetManager;

import java.awt.event.KeyEvent;

public class PlayerCar extends Car{
    private static String imagePath = "images/playercar.png"; //path to image
    public static String hitboxPath = "hitboxes/playercarhitbox.ser"; //path to hitbox area

    private double speedDefaultForward = -10; //key up speed default
    private double speedDefaultSlowDown = -2; //key down speed default
    private double speedDefaultHorizontal = 4; //key left/right speed default
    private double speedDefaultBase = -4; //no key pressed speed default
    public double speedForward = speedDefaultForward; //key up speed
    public double speedSlowDown = speedDefaultSlowDown; //key down speed
    public double speedHorizontal = speedDefaultHorizontal; //key left/right speed
    public double speedBase = speedDefaultBase; //no key pressed speed
    private StreetManager streetManager; //StreetManager object

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
        super(x, y, PlayerCar.imagePath, PlayerCar.hitboxPath);
        this.streetManager = streetManager;
        updateSpeed();
    }

    public void reset(){
        fillFuel();
        keyLeft = false;
        keyRight = false;
        keyUp = false;
        keyDown = false;
        keyHorizontal = "none";
        keyVertical = "none";
        speedForward = speedDefaultForward;
        speedSlowDown = speedDefaultSlowDown;
        speedHorizontal = speedDefaultHorizontal;
        speedBase = speedDefaultBase;
        updateSpeed();
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
            setHorizontalSpeed(-speedHorizontal);
        }
        else if (keyHorizontal.equals("right")) {
            setHorizontalSpeed(speedHorizontal);
        }
        else if(keyHorizontal.equals("none"))
            setHorizontalSpeed(0);

        if(keyVertical.equals("up"))
            setVerticalSpeed(speedForward);
        else if (keyVertical.equals("down"))
            setVerticalSpeed(speedSlowDown);
        else if(keyVertical.equals("none"))
            setVerticalSpeed(speedBase);

    }

    public void update() {
        moveRelative(getHorizontalSpeed(), getVerticalSpeed(), 0, streetManager.getSpeed());
    }

    public void keyReleased(KeyEvent e){ //Key release event
        int key = e.getKeyCode(); //get released key

        if(key == KeyEvent.VK_LEFT)
            keyLeft = false; //left key released
        if(key == KeyEvent.VK_RIGHT)
            keyRight = false; //right key released
        if(key == KeyEvent.VK_UP)
            keyUp = false; //up key released
        if(key == KeyEvent.VK_DOWN)
            keyDown = false; //down key released

        if(key == KeyEvent.VK_LEFT) //if left key was released
            if (keyHorizontal.equals("left") && keyRight)
                keyHorizontal = "right"; //sets to right, if right key is still pressed
            else if (keyHorizontal.equals("left"))
                keyHorizontal = "none"; //sets to none, if right key isn't pressed

        if(key == KeyEvent.VK_RIGHT) //if right key was released
            if (keyHorizontal.equals("right") && keyLeft)
                keyHorizontal = "left"; //sets to left, if left key is still pressed
            else if (keyHorizontal.equals("right"))
                keyHorizontal = "none"; //sets to none, if left key isn't pressed

        if(key == KeyEvent.VK_UP) //if up key was released
            if (keyVertical.equals("up") && keyDown)
                keyVertical = "down"; //sets to down, if down key is still pressed
            else if (keyVertical.equals("up"))
                keyVertical = "none"; //sets to none, if down key isn't pressed

        if(key == KeyEvent.VK_DOWN) //if down key was released
            if (keyVertical.equals("down") && keyUp)
                keyVertical = "up"; //sets to up, if up key is still pressed
            else if (keyVertical.equals("down"))
                keyVertical = "none"; //sets to none, if up key isn't pressed

        updateSpeed(); //update speeds
    }
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode(); //get pressed key

        if(key == KeyEvent.VK_LEFT){
            keyHorizontal = "left"; //move horizontal left
            keyLeft = true; //left key pressed
        }
        if(key == KeyEvent.VK_RIGHT){
            keyHorizontal = "right"; //move horizontal right
            keyRight = true; //right key pressed
        }
        if(key == KeyEvent.VK_UP){
            keyVertical = "up"; //move vertical up
            keyUp = true; //up key pressed
        }
        if(key == KeyEvent.VK_DOWN){
            keyVertical = "down"; //move vertical down
            keyDown = true; //down key pressed
        }

        updateSpeed(); //update speeds
    }
}
