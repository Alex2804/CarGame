package de.Alex2804.objects;

public class Street extends Object {
    private static String imagePath = "res/street.png";
    private double speed = 5;

    public Street(double x, double y){
        super(x, y, true, Street.imagePath);
    }

    public double getSpeed(){
        return speed;
    }
    public double speed(){
        return getSpeed();
    }
    public void setSpeed(double speed){
        this.speed = speed;
    }
}
