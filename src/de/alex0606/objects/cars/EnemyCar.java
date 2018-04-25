package de.alex0606.objects.cars;


import de.alex0606.StreetManager;

import java.awt.geom.Area;
import java.util.concurrent.ThreadLocalRandom;

public class EnemyCar extends Car{
    private static String imagePath = "res/car.png"; //Path to image
    public static String hitboxPath = "res/enemycarhitbox.ser"; //path to pixel hitbox Area
    private static String realHitboxPath = hitboxPath;
    private static Area hitbox;

    private int track = -1; //track of EnemyCar
    private int targetTrack; //target track at lange change
    private double speed; //speed for lane change
    private int xEnd; //target x-Position at lange change
    private boolean changing = false; //is EnemyCar changing its lane
    private boolean accident = false;

    private StreetManager streetManager; //streetManager (get width of tracks)

    //Constructors
    public EnemyCar(double x, double y, StreetManager streetManager){
        super(x, y, EnemyCar.imagePath, EnemyCar.realHitboxPath); //Object with x, y, image and hitbox file
        initVars(streetManager, getHorizontalSpeed(), getVerticalSpeed(), track); //set StreetManager and default speed
    }
    public EnemyCar(double x, double y, StreetManager streetManager, double horizontalSpeed, double verticalSpeed){
        super(x, y, EnemyCar.imagePath, EnemyCar.realHitboxPath); //Object with x, y, image and hitbox file
        initVars(streetManager, horizontalSpeed, verticalSpeed, track); //set StreetManager, horizontal- and vertical speed
    }
    public EnemyCar(double x, double y, StreetManager streetManager, double horizontalSpeed, double verticalSpeed, int track){
        super(x, y, EnemyCar.imagePath, EnemyCar.realHitboxPath); //Object with x, y, image and hitbox file
        initVars(streetManager, horizontalSpeed, verticalSpeed, track); //set StreetManager, horizontal- and vertical speed and the track
    }
    //This Constructor is for PoliceCar instance
    protected EnemyCar(double x, double y, StreetManager streetManager, double horizontalSpeed, double verticalSpeed, int track, String imagePath, String realHitboxPath){
        super(x, y, imagePath, realHitboxPath); //Object with x, y, image and hitbox file
        this.streetManager = streetManager;
        setHorizontalSpeed(horizontalSpeed);
        setVerticalSpeed(verticalSpeed);
        this.track = track;
    }
    private void initVars(StreetManager streetManager, double horizontalSpeed, double verticalSpeed, int track){ //Initialize parameter with given values, called by constructors
        this.streetManager = streetManager;
        setHorizontalSpeed(horizontalSpeed);
        setVerticalSpeed(verticalSpeed);
        this.track = track;
        if(realHitboxPath == null){
            setHitboxArea(hitbox);
        }else{
            hitbox = getHitboxArea();
            realHitboxPath = null;
        }
    }

    public StreetManager getStreetManager(){
        return streetManager;
    }
    public void setAccident(boolean accident) {
        this.accident = accident;
    }
    public boolean isAccident() {
        return accident;
    }

    public void update(){ //Called by GameLoop
        if(!isAccident()) {
            moveRelative(getHorizontalSpeed(), getVerticalSpeed(), 0, streetManager.getSpeed()); //moves relative to the street

            if (speed > 0 && changing) { //if changing to right
                if (getX() >= xEnd) { //if x end position reached
                    setHorizontalSpeed(0); //don't move horizontal
                    track = targetTrack; //set track to new track
                    changing = false; //not changing anymore
                }
            } else if (speed < 0 && changing) { //if changing to left
                if (getX() <= xEnd) { //if x end position reached
                    setHorizontalSpeed(0); //don't move horizontal
                    track = targetTrack; //set track to new track
                    changing = false; //not changing anymore
                }
            }
        }else
            moveRelative(0, 0, 0, streetManager.getSpeed()); //moves relative to the street
    }

    public void setTrack(int track){
        this.track = track;
    }
    public int getTrack(){
        return track;
    }

    public boolean changeTrack(int targetTrack, double speed) { //initilize lane change
        if(!changing){ //only starts lane change if not changing
            this.targetTrack = targetTrack; //set target track
            if(targetTrack < 0 || targetTrack >= streetManager.getHorizontalStreetCount() || targetTrack == track){
                return false; //if target is out of horizontal lanes or target track is current track, return (and abord change) false
            }
            changing = true; //if not aborted, now changing

            this.speed =  speed * ((targetTrack-track) / Math.abs(targetTrack - track)); //speed has to be positive if changing to right and negative if changing to left
            setHorizontalSpeed(this.speed);  //set horizontal speed

            int xStart = getX(); //start position is current position
            xEnd = xStart + StreetManager.getSampleStreet().getWidth() * (targetTrack - track); //endposition is start position +/- track difference * track width
            return true;
        }
        return false; //if not aborted, return true for sucessful initialized lane change
    }
    public int getNewRandomTrack(){
        int newTrack = ThreadLocalRandom.current().nextInt(0, streetManager.getHorizontalStreetCount()); //generates random track, in range of the streetmanager horizontal lane count
        return newTrack;
    }
    public boolean isChanging(){
        return changing;
    }
}
