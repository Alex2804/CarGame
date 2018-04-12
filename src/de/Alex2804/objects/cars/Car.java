package de.Alex2804.objects.cars;

import de.Alex2804.objects.Object;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Car extends Object{
    private static String imagePath = "res/car.png";

    private double verticalSpeed = 0;
    private double horizontalSpeed = 0;

    public Car(double x, double y){
        super(x, y, true, Car.imagePath);
    }
    public Car(double x, double y, String imagePath){
        super(x, y, true, imagePath);
    }

    public void setHorizontalSpeed(double horizontalSpeed){
        this.horizontalSpeed = horizontalSpeed;
    }
    public double getHorizontalSpeed(){
        return horizontalSpeed;
    }

    public void setVerticalSpeed(double verticalSpeed){
        this.verticalSpeed = verticalSpeed;
    }
    public double getVerticalSpeed(){
        return verticalSpeed;
    }

    public void setSpeed(double horizontalSpeed, double verticalSpeed){
        this.horizontalSpeed = horizontalSpeed;
        this.verticalSpeed = verticalSpeed;
    }

    public Area getHitbox(){
        int mirrorWidth = 6;
        int mirrorHeight = 6;
        int mirrorYPos = 59;
        int headLightHeight = 15;
        int headLightWidth = 15;
        int backLightHeight = 20;
        int edgeWidth = mirrorWidth + headLightWidth;
        int x = getX();
        int y = getY();
        int width = width() - 1;

        int[] xPos = {width/2/**1**/, edgeWidth/**2**/, mirrorWidth/**3**/, mirrorWidth/**4**/, 0/**5**/,
                mirrorWidth/**6**/, mirrorWidth/**7**/, edgeWidth/**8**/, width/2/**9**/};
        int[] yCords = {y/**1**/, y/**2**/, y+headLightHeight/**3**/, y+mirrorYPos/**4**/,
                y+mirrorYPos+mirrorHeight/**5**/, y+mirrorYPos+mirrorHeight/**6**/,
                y+height()-backLightHeight/**7**/, y+height()/**8**/, y+height()/**9**/};

        int[] xCordsLeft = new int[xPos.length];
        System.arraycopy(xPos, 0, xCordsLeft, 0, xPos.length);
        for(int i = 0; i < xCordsLeft.length; i++){ xCordsLeft[i] += x; }

        Polygon leftPolygon = new Polygon(xCordsLeft, yCords, xCordsLeft.length);

        int[] xCordsRight = new int[xPos.length];
        System.arraycopy(xPos, 0, xCordsRight, 0, xCordsRight.length);
        for(int i = 0; i < xCordsRight.length; i++){ xCordsRight[i] = x + width -xCordsRight[i]; }
        Polygon rightPolygon = new Polygon(xCordsRight, yCords, xCordsRight.length);

        Area area = new Area(leftPolygon);
        area.add(new Area(rightPolygon));
        return area;
    }
}
