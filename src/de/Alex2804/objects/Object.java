package de.Alex2804.objects;

import java.awt.*;
import java.awt.geom.Area;
import javax.swing.*;

public class Object {
    private double x = 0;
    private double y = 0;
    private int width = 0;
    private int height = 0;
    private boolean visible = true;
    private Image image;

    public Object(){ }
    public Object(double x, double y){
        initVars(x, y, visible);
    }
    public Object(double x, double y, boolean visible){
        initVars(x, y, visible);
    }
    public Object(double x, double y, String imagePath) {
        initVars(x, y, visible);
        setImage(imagePath);
    }
    public Object(double x, double y, boolean visible, String imagePath) {
        initVars(x, y, visible);
        setImage(imagePath);
    }
    private void initVars(double x, double y, boolean visible){
        this.x = x;
        this.y = y;
        this.visible = visible;
    }


    public void setImage(String imagePath){
        ImageIcon icon = new ImageIcon(imagePath);
        setImage(icon.getImage());
    }
    public void setImage(Image image){
        this.image = image;
        updateDimensions();
    }
    public Image getImage(){
        return image;
    }
    private void updateDimensions(){
        setWidth(image.getWidth(null));
        setHeight(image.getHeight(null));
    }

    public void moveTo(double x, double y){
        setX(x);
        setY(y);
    }
    public void move(double xDif, double yDif){
        moveHorizontal(xDif);
        moveVertical(yDif);
    }
    public void moveRelative(double xDif, double yDif, double horizontalMovement, double verticalMovement){
        moveRelativeHorizontal(xDif, horizontalMovement);
        moveRelativeVertical(yDif, verticalMovement);
    }
    public void moveHorizontal(double xDif){
        x += xDif;
    }
    public void moveRelativeHorizontal(double xDif, double horizontalMovement){
        moveHorizontal(horizontalMovement + xDif);
    }
    public void moveVertical(double yDif){
        y += yDif;
    }
    public void moveRelativeVertical(double yDif, double verticalMovement){
        moveVertical(verticalMovement + yDif);
    }

    public void setX(double x){
        this.x = x;
    }
    public int getX(){
        return (int)x;
    }
    public int x(){
        return getX();
    }

    public void setY(double y){
        this.y = y;
    }
    public int getY() {
        return (int)y;
    }
    public int y(){
        return getY();
    }

    public void setVisible(boolean visible){
        this.visible = visible;
    }
    public void show(){
        setVisible(true);
    }
    public void hide(){
        setVisible(false);
    }
    public boolean isVisible(){
        return visible;
    }

    private void setWidth(int width){
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public int width(){
        return getWidth();
    }

    private void setHeight(int height){
        this.height = height;
    }
    public int getHeight(){
        return height;
    }
    public int height(){
        return getHeight();
    }

    public Rectangle getBoundingRect(){
        return new Rectangle(getX(), getY(), width(), height());
    }

    public Area getHitbox(){
        return new Area(getBoundingRect());
    }

    public boolean checkHitboxIntersection(Area hitbox){
        Area overlap = new Area(hitbox);
        overlap.intersect(getHitbox());
        return !overlap.isEmpty();
    }
    public boolean checkHitboxIntersection(Object object){
        return checkHitboxIntersection(object.getHitbox());
    }

    public void draw(Graphics g, JPanel observer){
        draw((Graphics2D) g, observer);
    }
}