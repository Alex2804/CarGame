package alex2804.objects.cars;

import alex2804.objects.Object;
import alex2804.MainWindow;

public abstract class Car extends Object {
    private static String imagePath = "res/images/car.png"; //standard car image

    public double verticalSpeed = 0; //vertical speed of the car
    private double horizontalSpeed = 0; //horizontal speed of the car

    public Car(double x, double y){
        super(x, y, true, Car.imagePath); //Object with given x, y and the car image
    }
    public Car(double x, double y, String imagePath){
        super(x, y, true, imagePath); //Object with the given x, y and image
    }
    public Car(double x, double y, String imagePath, String hitboxPath){
        super(x, y, true, imagePath, hitboxPath); //Object with the given x, y, image and path to hitbox file
    }

    public void setHorizontalSpeed(double horizontalSpeed){
        this.horizontalSpeed = horizontalSpeed*MainWindow.scale; //set horizontal speed (dependent on scale)
    }
    public double getHorizontalSpeed(){
        return horizontalSpeed/MainWindow.scale;
    }

    public void setVerticalSpeed(double verticalSpeed){
        this.verticalSpeed = verticalSpeed*MainWindow.scale; //set vertical speed (dependent on scale)
    }
    public double getVerticalSpeed(){
        return verticalSpeed/MainWindow.scale;
    }

    public void setSpeed(double horizontalSpeed, double verticalSpeed){
        setHorizontalSpeed(horizontalSpeed);
        setVerticalSpeed(verticalSpeed);
    }
}
