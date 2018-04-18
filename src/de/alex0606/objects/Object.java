package de.alex0606.objects;

import de.alex0606.MainWindow;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Object{
    private double x = 0;
    private double y = 0;
    private int width = 0;
    private int height = 0;
    private boolean visible = true;
    private BufferedImage image;
    private Area hitboxArea;

    public Object(){ }
    public Object(double x, double y){
        initVars(x, y, visible, null, false);
    }
    public Object(double x, double y, boolean visible){
        initVars(x, y, visible, null, false);
    }
    public Object(double x, double y, String imagePath) {
        initVars(x, y, visible, imagePath, false);
    }
    public Object(double x, double y, boolean visible, String imagePath) {
        initVars(x, y, visible, imagePath, false);
    }
    public Object(double x, double y, String imagePath, boolean pixelHitbox) {
        initVars(x, y, visible, imagePath, pixelHitbox);
    }
    public Object(double x, double y, boolean visible, String imagePath, boolean pixelHitbox) {
        initVars(x, y, visible, imagePath, pixelHitbox);
    }
    private void initVars(double x, double y, boolean visible, String imagePath, boolean pixelHitbox){
        this.x = x;
        this.y = y;
        this.visible = visible;
        if(imagePath != null){
            setImage(imagePath);
            if(pixelHitbox){
                hitboxArea = createPixelHitbox();
            }
        }
    }
    public Area createPixelHitbox(){
        Area area = new Area();

        BufferedImage image = getImage();

        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                if(!(image.getRGB(x, y)>>24==0x00)){
                    area.add(new Area(new Rectangle(x, y, 1, 1)));
                }
            }
        }
        return area;
    }
    public void setHitboxArea(Area hitboxArea) {
        this.hitboxArea = hitboxArea;
    }

    public Area getHitboxArea() {
        return hitboxArea;
    }

    public void setImage(String imagePath){
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(imagePath));
        }catch (IOException e){
            System.out.println("Image " + imagePath + " not Found!");
        }
        setImage(image);
    }
    public void setImage(BufferedImage image){
        int w = image.getWidth();
        int h = image.getHeight();
        double scale = MainWindow.scale;
        // Create a new image of the proper size
        int w2 = (int) (w * scale);
        int h2 = (int) (h * scale);
        BufferedImage scaledImage = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
        AffineTransform scaleInstance = AffineTransform.getScaleInstance(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_BILINEAR);

        scaleOp.filter(image, scaledImage);

        this.image = scaledImage;

        updateDimensions();
    }
    public BufferedImage getImage(){
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
        if(hitboxArea == null){
            return new Area(getBoundingRect());
        }
        else {
            return hitboxArea.createTransformedArea(AffineTransform.getTranslateInstance(getX(), getY()));
        }
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