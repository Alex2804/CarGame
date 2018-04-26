package alex2804.objects.cars;

import alex2804.ObstacleManager;
import alex2804.MainWindow;
import alex2804.StreetManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class PoliceCar extends EnemyCar implements ActionListener {
    public static String hitboxPath = "res/hitboxes/policecarhitbox.ser"; //path to pixel hitbox Area
    private static String realHitboxPath = hitboxPath; //is set to null if hitbox was generated (or read from file)
    private static Area hitbox; //pixel hitbox is stored

    private Timer lightTimer = new Timer(200, this);
    private boolean right = false;
    private static BufferedImage leftImage = null;
    private static BufferedImage rightImage = null;
    private static String imagePathLeft = "res/images/policecarleft.png"; //Path to image
    private static String imagePathRight = "res/images/policecarright.png"; //Path to image

    public PoliceCar(double x, double y, StreetManager streetManager, double horizontalSpeed, double verticalSpeed, int track){
        super(x, y, streetManager, horizontalSpeed, verticalSpeed, track, imagePathLeft, hitboxPath); //Object with x, y, image and hitbox file

        //create / read hitbox if it's the first instance or set hitbox to created / read hitbox
        if(realHitboxPath == null){
            setHitboxArea(hitbox);
        }else{
            hitbox = getHitboxArea();
            realHitboxPath = null;
        }

        if(rightImage == null || leftImage == null) {
            setImage(imagePathLeft);
            leftImage = getImage();
            setImage(imagePathRight);
            rightImage = getImage();
        }
        lightTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(right)
            setImageDirect(rightImage);
        else
            setImageDirect(leftImage);
        right = !right;
    }

    public Area getForwardHitbox(){ //hitbox in front of car
        int height = (int)(getHeight()*(0.5/MainWindow.scale));
        int y = (int)(getY() - height * 1.1);
        return new Area(new Rectangle(x(), y, width(), height));
    }
    public Area getLeftHitbox(){ //hitbox left of car (not over one lane)
        int width = (int)(getStreetManager().getSampleStreet().getWidth() * 0.5);
        int height = (int)(getHeight() * 1.5 * (getStreetManager().getSpeed()/5));
        int x = (int)(getX() - width - width * 0.5);
        int y = (int)(getY() + (getHeight() - height));
        return new Area(new Rectangle(x, y, width, height));
    }
    public Area getRightHitbox(){ //hitbox right of car (not over one lane)
        Area leftHitbox = getLeftHitbox();
        int width = leftHitbox.getBounds().width;
        int dif = 2 * (getX() - (leftHitbox.getBounds().x + width)) + width;
        return leftHitbox.createTransformedArea(AffineTransform.getTranslateInstance(dif + getWidth(), 0));
    }

    public void updateTrack(ObstacleManager om, PlayerCar target){ //check if colliding with car and possible to avoid
        //trackTrend states: 0 drive with normal speed forward; -1 change lane in negative direction (left); 1 chagne lane in positive direction (right), 10 forward and slow down
        int trackTrend = 0; //hold lane by default
        if(target.getX() < getX()-20*MainWindow.scale) //if playercar is on the left of police car
            trackTrend = -1; //try lane change to left
        else if(target.getX() > getX() + getWidth()+20*MainWindow.scale) //if playercar is on the right of police car
            trackTrend = 1; //try lange change to right
        if(!isChanging()) { //if police car isn't already changing
            trackTrend = checkTrackTrend(om, trackTrend); //check if police car can change to player car lane or has to avoid obstacles
            changeTrack(getTrack() + trackTrend, 2*MainWindow.scale); //change track to designated
        }
        if(trackTrend != 10){ //if car is not forced to drive forward
            if(isChanging()){ //if car changes lange
                setVerticalSpeed(target.getVerticalSpeed()*0.98); //speed is lower than player car speed
            }else { //if not changing
                setVerticalSpeed(target.getVerticalSpeed()*1.05); //speed is higher than player car speed
            }
        }else { //if behind obstacle but can't avoid
            holdTrack(); //drive forward an slow down
        }
    }
    private int checkTrackTrend(ObstacleManager om, int trackTrend){
        return checkTrackTrend(om, trackTrend, 1); //pass with standard value
    }
    private int checkTrackTrend(ObstacleManager om, int trackTrend, int run){
        if(run >= 4){ //if all capabilities checked but ther is no obstacle avoiding
            return 10;
        }else if(trackTrend == 0 && !checkForward(om)){ //if trying to drive forward and forward is clear
            return trackTrend;
        }else if(trackTrend == -1 && !checkLeft(om) && getTrack() > 0){ //if trying to drive to the left and the left is free
            return trackTrend;
        }else if(trackTrend == 1 && !checkRight(om) && getTrack() < (getStreetManager().getHorizontalStreetCount() - 1)){ //if trying to drive to the right and the right is free
            return trackTrend;
        }else //if none is true
            return checkTrackTrend(om, trackTrend + 1 > 1 ? -1 : trackTrend + 1, run + 1); //call itself recursive (until it's the 4th run) and change trackTrend and run
    }
    private boolean checkLeft(ObstacleManager om){ //checks if any obstacle collides with left hitbox
        return om.checkObstacleCollision(getLeftHitbox());
    }
    private boolean checkRight(ObstacleManager om){ //checks if any obstacle collides with right hitbox
        return om.checkObstacleCollision(getRightHitbox());
    }
    private boolean checkForward(ObstacleManager om){ //checks if any obstacle collides with forward hitbox
        return om.checkObstacleCollision(getForwardHitbox());
    }
    private void holdTrack(){ //can't avoid obstacle
        setVerticalSpeed(getVerticalSpeed() + 0.5); //slow down (until forward hitbox don't collide with any obstacle hitbox)
    }
}
