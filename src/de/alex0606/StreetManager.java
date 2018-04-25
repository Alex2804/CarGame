package de.alex0606;

import de.alex0606.objects.Object;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class StreetManager { //Manages the road
    public static String imagePath = "res/street.png"; //Path to street image
    private static Object sampleStreet = new Object(0, 0, imagePath); //street to get its width and height
    private ArrayList<Object> streets; //holds the street objects

    private double y = 0; //y of road
    private JPanel panel; //panel of streetManager
    private double defaultSpeed = 5; //default speed
    private double maxSpeed = (int)11*MainWindow.scale; //max speed with increaseSpeed()
    private double speed = defaultSpeed; //speed
    private int spacing = 30; //minimal distance between left panel edge and first street
    private int xAdditive = 0; //real distance between left panel edge and first street
    private int horizontalStreetCount = 0;
    private int verticalStreetCount = 0;

    public static Object getSampleStreet(){
        return sampleStreet;
    }

    public StreetManager(JPanel panel){
        this.panel = panel;
        this.panel.addComponentListener(new ResizeListener()); //if panel is resized, streets has to be initialized again
        setSpacing(spacing);
        resetSpeed();
        initStreets(); //initialize streets
    }

    public void initStreets(){
        streets = new ArrayList<>(); //reset streets ArrayList
        int streetWidth = sampleStreet.getWidth(); //width of one street
        int streetHeight = sampleStreet.getHeight(); //height of one street

        int width = panel.getWidth() - (getSpacing() * 2); //available width in panel (without spacing on both sides)
        int height = panel.getHeight(); //available height (panel height)

        setHorizontalStreetCount((int)(width/streetWidth)); //amount of horizontal street Objects
        setVerticalStreetCount((int)(height/streetHeight) + 2); //amount of vertical street Objects
        setXAdditive(getSpacing() + ((width % streetWidth) / 2)); //unused horizontal space

        for(int i = 0; i < getHorizontalStreetCount(); i++){ //fill columns
            for(int j = -4; j <= getVerticalStreetCount(); j++){ //fill rows
                Object street = new Object(getXAdditive() + i * streetWidth, j * streetHeight + y, imagePath); //create street Object
                streets.add(street); //add street Object to list
            }
        }
    }

    public double getSpeed(){
        return Math.round(((speed/MainWindow.scale)*10)/10);
    }
    public double speed(){
    	return Math.round((speed*10)/10);
    }
    public void setSpeed(double speed){
        this.speed = Math.round(((speed*MainWindow.scale)*10)/10); //speed depends on scale ratio and is rounded to 1 decimal
    }
    public void resetSpeed(){
        setSpeed(defaultSpeed);
    }
    public boolean increaseSpeed(double count){
        if((int)(getSpeed() + count) <= maxSpeed){
            setSpeed(getSpeed() + 1);
            return true;
        }
        else
            setSpeed(maxSpeed);
        return false;
    }
    public int getHorizontalStreetCount(){
        return horizontalStreetCount;
    }
    private void setHorizontalStreetCount(int horizontalStreetCount){
        this.horizontalStreetCount = horizontalStreetCount;
    }
    public int getVerticalStreetCount(){
        return verticalStreetCount;
    }
    private void setVerticalStreetCount(int verticalStreetCount){
        this.verticalStreetCount = verticalStreetCount;
    }
    public int getSpacing(){
        return spacing;
    }
    public void setSpacing(int spacing){
        this.spacing = (int)(spacing * MainWindow.scale);
    }
    public double getY(){
        return y;
    }
    public void setY(double y){
        this.y = y;
    }
    public int getXAdditive(){
        return xAdditive;
    }
    private void setXAdditive(int xAdditive){
        this.xAdditive = xAdditive;
    }

    public void move(){ //moves all objects as one big road
        y += speed; //add the speed to the y position of the StreetManager
        if(y / sampleStreet.getHeight() >= 1){ //if the y translation is bigger than one street height
            int temp = (int)(y/sampleStreet.getHeight()); //store shift
            setY(y - sampleStreet.getHeight()*temp); //subtract one street height from y

            for(Object street : streets){ //for all street objects
                street.moveVertical(speed - sampleStreet.getHeight()*temp + y); //move streets backwards (for one street height) until at default position
            }
        }
        else{
            for(Object street : streets){
                street.moveVertical(speed);
            }
        }
    }

    public void update(){
        move();
    }

    public void draw(Graphics g){
        for(Object street : streets){
            g.drawImage(street.getImage(), street.getX(), street.getY(), null);
        }
    }

    public Rectangle getBoundingRect(){
        return new Rectangle(getXAdditive(), 0, //get bounding rect, with the origin xAdditive, 0, width of one street * horizontal street count and panel height
                getHorizontalStreetCount() * sampleStreet.getWidth(), panel.getHeight());
    }


    //Remap streets, if panel gets resized
    class ResizeListener extends ComponentAdapter {
        public void componentResized(ComponentEvent e) {
            initStreets();
        }
    }
}
