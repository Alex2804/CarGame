package de.alex0606.objects.cars;

import de.alex0606.MainWindow;
import de.alex0606.ObstacleManager;
import de.alex0606.StreetManager;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;

public class PoliceCar extends EnemyCar {
    private static String imagePath = "res/policecar.png"; //Path to image
    public static String hitboxPath = "res/policecarhitbox.ser"; //path to pixel hitbox Area
    private static String realHitboxPath = hitboxPath;
    private static Area hitbox;


    public PoliceCar(double x, double y, StreetManager streetManager, double horizontalSpeed, double verticalSpeed, int track){
        super(x, y, streetManager, horizontalSpeed, verticalSpeed, track, imagePath, hitboxPath); //Object with x, y, image and hitbox file

        if(realHitboxPath == null){
            setHitboxArea(hitbox);
        }else{
            hitbox = getHitboxArea();
            realHitboxPath = null;
        }
    }

    public Area getForwardHitbox(){
        int height = (int)(getHeight()*(0.5/MainWindow.scale));
        int y = (int)(getY() - height * 1.1);
        return new Area(new Rectangle(x(), y, width(), height));
    }
    public Area getLeftHitbox(){
        int width = (int)(getStreetManager().getSampleStreet().getWidth() * 0.5);
        int height = (int)(getHeight() * 1.5 * (getStreetManager().getSpeed()/5));
        int x = (int)(getX() - width - width * 0.5);
        int y = (int)(getY() + (getHeight() - height));
        return new Area(new Rectangle(x, y, width, height));
    }
    public Area getRightHitbox(){
        Area leftHitbox = getLeftHitbox();
        int width = leftHitbox.getBounds().width;
        int dif = 2 * (getX() - (leftHitbox.getBounds().x + width)) + width;
        return leftHitbox.createTransformedArea(AffineTransform.getTranslateInstance(dif + getWidth(), 0));
    }

    public void updateTrack(ObstacleManager om, PlayerCar target){
        int trackTrend = 0;
        if(target.getX() < getX()-20*MainWindow.scale)
            trackTrend = -1;
        else if(target.getX() > getX() + getWidth()+20*MainWindow.scale)
            trackTrend = 1;
        if(!isChanging()) {
            trackTrend = checkTrackTrend(om, trackTrend);
            changeTrack(getTrack() + trackTrend, 2*MainWindow.scale);
        }
        if(trackTrend != 10){
            if(!isChanging()){
                setVerticalSpeed(target.getVerticalSpeed()*1.05);
            }else {
                setVerticalSpeed(target.getVerticalSpeed()*0.98);
            }
        }else {
            holdTrack();
        }
    }
    private int checkTrackTrend(ObstacleManager om, int trackTrend){
        return checkTrackTrend(om, trackTrend, 1);
    }
    private int checkTrackTrend(ObstacleManager om, int trackTrend, int run){
        if(run >= 4){
            return 10;
        }else if(trackTrend == 0 && !checkForward(om)){
            return trackTrend;
        }else if(trackTrend == -1 && !checkLeft(om) && getTrack() > 0){
            return trackTrend;
        }else if(trackTrend == 1 && !checkRight(om) && getTrack() < (getStreetManager().getHorizontalStreetCount() - 1)){
            return trackTrend;
        }else
            return checkTrackTrend(om, trackTrend + 1 > 1 ? -1 : trackTrend + 1, run + 1);
    }
    private boolean checkLeft(ObstacleManager om){
        return om.checkObstacleCollision(getLeftHitbox());
    }
    private boolean checkRight(ObstacleManager om){
        return om.checkObstacleCollision(getRightHitbox());
    }
    private boolean checkForward(ObstacleManager om){
        return om.checkObstacleCollision(getForwardHitbox());
    }
    private void holdTrack(){
        setVerticalSpeed(getVerticalSpeed() + 0.1);
    }

    private boolean checkHitboxIntersection(Area hitbox1, Area hitbox2){
        Area overlap = new Area(hitbox1);
        overlap.intersect(hitbox2);
        return !overlap.isEmpty();
    }
}
