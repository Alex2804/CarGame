package alex2804.objects;

import alex2804.MainWindow;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class Object{
    private double x = 0; //X Position of the object
    private double y = 0; //Y Position of the object
    private int width = 0; //width of the object(image)
    private int height = 0; //height of the object(image)
    private boolean visible = true; //visibility
    private BufferedImage originImage; //original image (not scaled)
    private BufferedImage image; //scaled image of the object
    private Area hitboxArea = null; //pixel hitbox of the image (if null, the hitbox is the bounding rectangle)

    private String hitboxPath = null; //path to the file, where the Area of the pixel hitbox is saved. If null, no pixelhitbox created/read.

    //Constructors
    public Object(){ } //Object with no image and standard position
    public Object(double x, double y){
        initVars(x, y, visible, null, false, hitboxPath); //Object with given x and y
    }
    public Object(double x, double y, boolean visible){
        initVars(x, y, visible, null, false, hitboxPath); //Object with given x, y and visibility
    }
    public Object(double x, double y, String imagePath) {
        initVars(x, y, visible, imagePath, false, hitboxPath); //Object with given x, y and image if imagepath is valid
    }
    public Object(double x, double y, boolean visible, String imagePath) {
        initVars(x, y, visible, imagePath, false, hitboxPath); //Object with given x, y, visibility, and image if imagepath is valid
    }
    public Object(double x, double y, boolean visible, String imagePath, String hitboxPath) {
        initVars(x, y, visible, imagePath, false, hitboxPath); //Object with given x, y, visibility, image if path valid and pixel-hitbox (generated or read)
    }
    public Object(double x, double y, String imagePath, boolean pixelHitbox) {
        initVars(x, y, visible, imagePath, pixelHitbox, hitboxPath); //Object with given x, y, image if path valid and generated pixelhitbox (if true)
    }
    public Object(double x, double y, String imagePath, String hitboxPath) {
        initVars(x, y, visible, imagePath, false, hitboxPath); //Object with given x, y, image if path valid and pixel-hitbox (generated or read)
    }
    public Object(double x, double y, boolean visible, String imagePath, boolean pixelHitbox) {
        initVars(x, y, visible, imagePath, pixelHitbox, hitboxPath); //Object with given x, y, visibility, image if path valid and generated pixelhitbox (if true)
    }
    //Initialize the parameter. Called by constructors
    private void initVars(double x, double y, boolean visible, String imagePath, boolean pixelHitbox, String hitboxPath){
        this.x = x;
        this.y = y;
        this.visible = visible;
        this.hitboxPath = hitboxPath;
        if(imagePath != null){ //set image if path to image exists
            setImage(imagePath);
            if(pixelHitbox || hitboxPath != null){ //generate pixelhitbox or read/write it to file if hitboxPath is given
                setHitboxArea(getPixelHitbox());
            }
        }
    }
    private Area getPixelHitbox(){
        try {
            InputStream fis = getClass().getResource("/" + hitboxPath).openStream(); //Read hitbox area from resources
            ObjectInputStream ois = new ObjectInputStream(fis);
            Area hitboxArea = new Area((Path2D) ois.readObject()); //Read Path and convert it to area
            ois.close();
            fis.close(); //close file
            return hitboxArea;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void setHitboxArea(Area hitboxArea) {
        this.hitboxArea = hitboxArea;
    }
    public Area getHitboxArea() {
        return hitboxArea;
    }

    public void setImage(String imagePath){
        imagePath = "/" + imagePath;
        InputStream imageStream = getClass().getResourceAsStream(imagePath); // Read image from resources
        if (imageStream == null) {
            System.out.println("Resource at path " + imagePath + " not found!");
            return;
        }

        try {
            BufferedImage image = ImageIO.read(imageStream); // Read image from the stream
            //BufferedImage image = ImageIO.read(new File(imagePath)); //Reads the Image (throws exeption is path is not valid)
            setImage(image); //Object sets image (this method only reads it)
        }catch (IOException e){
            System.out.println("Image " + imagePath + " not Found!");
        }
    }
    public void setImage(BufferedImage image){
        this.originImage = image; //saves the unscaled image
        int w = image.getWidth(); //width of unscaled image
        int h = image.getHeight(); //height of unscaled image
        // Create a new image of the proper size
        int w2 = (int) (w * MainWindow.scale); //new width
        int h2 = (int) (h * MainWindow.scale); //new height
        BufferedImage scaledImage = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB); //create new BufferedImage
        Graphics g = scaledImage.createGraphics(); //get Graphics object of the scaled image
        ((Graphics2D) g).drawImage(image, 0, 0, w2, h2,null); //paint the unscaled image scaled to the scaled image

        this.image = scaledImage; //set the image

        updateDimensions(); //update the object dimensions
    }
    public void setImageDirect(BufferedImage image){
        this.image = image;
        updateDimensions();
    }
    public BufferedImage getImage(){
        return image;
    }
    public BufferedImage getOriginImage(){
        return originImage;
    }
    private void updateDimensions(){
        setWidth(image.getWidth(null)); //sets the width to the width of the object image
        setHeight(image.getHeight(null)); //sets the height to the height of the object image
    }

    public void moveTo(double x, double y){ //move to given x and y
        setX(x);
        setY(y);
    }
    public void move(double xDif, double yDif){ //move for given x- and y-Difference
        moveHorizontal(xDif);
        moveVertical(yDif);
    }
    public void moveRelative(double xDif, double yDif, double horizontalMovement, double verticalMovement){ //move for given x- and y-Difference relative to the given vertical and horizontal movement
        moveRelativeHorizontal(xDif, horizontalMovement);
        moveRelativeVertical(yDif, verticalMovement);
    }
    public void moveHorizontal(double xDif){ //move for given x Difference horizontal
        x += xDif;
    }
    public void moveRelativeHorizontal(double xDif, double horizontalMovement){ //move for given x Difference horizontal, relative to given horizontal movement
        moveHorizontal(horizontalMovement + xDif);
    }
    public void moveVertical(double yDif){ //move for given y Difference vertical
        y += yDif;
    }
    public void moveRelativeVertical(double yDif, double verticalMovement){ //move for given y Difference vertical, relative to given vertical movement
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
        return new Rectangle(getX(), getY(), width(), height()); //returns a rectangle, with origin x, y and the width and height of the object(image)
    }

    public Area getHitbox(){
        if(hitboxArea == null){
            return new Area(getBoundingRect()); //if no hitboxarea was created, it returns the bounding rectangle
        }
        else { //if a hitboxarea was created, this area is returned, with origin at x, y of the object and scaled
            return getHitboxArea().createTransformedArea(AffineTransform.
                    getTranslateInstance(getX()/MainWindow.scale, getY()/MainWindow.scale)). //move area to x and y
                    createTransformedArea(AffineTransform.getScaleInstance(MainWindow.scale, MainWindow.scale)); //scale area
        }
    }

    public boolean checkHitboxIntersection(Area hitbox){ //checks if the given area intersects with the hitbox of this object
        Area overlap = new Area(hitbox); //this hitbox is used for intersection test
        overlap.intersect(getHitbox()); //only get the area, that is overlapping
        return !overlap.isEmpty(); //if overlap is empty, the areas are not intersecting
    }
    public boolean checkHitboxIntersection(Object object){ //checks if the hitbox of the given object intersects with the hitbox of this object
        return checkHitboxIntersection(object.getHitbox());
    }

    public void draw(Graphics g){
        g.drawImage(getImage(), x(), y(), null); //draws the image with the given graphics object
    }
}