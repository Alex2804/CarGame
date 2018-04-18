package de.alex0606.objects.cars;

import de.alex0606.objects.Object;

import java.awt.*;
import java.awt.geom.*;

public abstract class Car extends Object{
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
}
