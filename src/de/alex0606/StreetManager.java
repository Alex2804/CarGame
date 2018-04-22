package de.alex0606;

import de.alex0606.objects.Street;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

public class StreetManager {
    private static Street sampleStreet = new Street(0, 0);
    private ArrayList<Street> streets;

    private double y = 0;
    private JPanel panel;
    private double speed = Math.round(((5*MainWindow.scale)*10)/10);
    private int spacing = (int)(30*MainWindow.scale);
    private int xAdditive = 0;
    private int horizontalStreetCount = 0;
    private int verticalStreetCount = 0;

    public static Street getSampleStreet(){
        return sampleStreet;
    }

    public StreetManager(JPanel panel){
        this.panel = panel;
        this.panel.addComponentListener(new ResizeListener());
        initStreets();
    }

    public void initStreets(){
        streets = new ArrayList<>();
        int streetWidth = sampleStreet.getWidth();
        int streetHeight = sampleStreet.getHeight();

        int width = panel.getWidth() - spacing * 2;
        int height = panel.getHeight();

        setHorizontalStreetCount((int)(width/streetWidth));
        setVerticalStreetCount((int)(height/streetHeight) + 1);
        setXAdditive(spacing + ((width % streetWidth) / 2));

        for(int i = 0; i < getHorizontalStreetCount(); i++){
            for(int j = -2; j <= getVerticalStreetCount(); j++){
                Street street = new Street(getXAdditive() + i * streetWidth, j * streetHeight + y);
                streets.add(street);
            }
        }
    }

    public double getSpeed(){
        return speed;
    }
    public void setSpeed(double speed){
        this.speed = speed;
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
        this.spacing = spacing;
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

    public void move(){
        y += speed;
        if(y / sampleStreet.getHeight() >= 1){
            setY(y % sampleStreet.getHeight());

            for(Street street : streets){
                street.moveVertical(speed - sampleStreet.getHeight());
                street.setY(street.getY() + getY());
            }
        }
        else{
            for(Street street : streets){
                street.moveVertical(speed);
            }
        }
    }

    public void update(){
        move();
    }

    public void draw(Graphics g){
        for(Street street : streets){
            g.drawImage(street.getImage(), street.getX(), street.getY(), panel);
        }
    }
    public void draw(Graphics g, JPanel observer){
        if(observer.getWidth() == panel.getWidth() && observer.getHeight() == panel.getHeight()){
            for(Street street : streets){
                g.drawImage(street.getImage(), street.getX(), street.getY(), observer);
            }
        }
    }

    public Rectangle getBoundingRect(){
        return new Rectangle(getXAdditive(), 0,
                getHorizontalStreetCount() * sampleStreet.getWidth(), panel.getHeight());
    }


    //Remap streets, if panel gets resized
    class ResizeListener extends ComponentAdapter {
        public void componentResized(ComponentEvent e) {
            initStreets();
        }
    }
}
