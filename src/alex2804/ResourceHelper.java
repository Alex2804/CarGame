package alex2804;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.*;

public class ResourceHelper {
    public static void trySetIcon(JFrame frame, String path) {
        try {
            Image image = ImageIO.read(ResourceHelper.class.getResource("/" + path));
            frame.setIconImage(image);
        } catch (IOException e) {
            System.out.println("Failed to load resource from /" + path);
        }
    }

    public static ImageIcon getScaledIcon(String path, double factor) {
        try {
            BufferedImage image = ImageIO.read(ResourceHelper.class.getResource("/" + path));
            return new ImageIcon(image.getScaledInstance((int) Math.round(image.getWidth() * factor),
                    (int) Math.round(image.getHeight() * factor), BufferedImage.SCALE_SMOOTH));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource from /" + path);
        }
    }

    public static void writeHitbox(String imagePath, String hitboxName) {
        // Create hitbox
        Area area = new Area(); //create new (empty) Area object
        BufferedImage image;
        try {
            image = ImageIO.read(ResourceHelper.class.getResource("/" + imagePath)); //get original (unscaled) image
        } catch (IOException e) {
            System.out.println("Failed to load resource from " + imagePath + ", skipping " + hitboxName);
            return;
        }

        for (int x = 0; x < image.getWidth(); x++) { //Parse every column
            for (int y = 0; y < image.getHeight(); y++) { //Parse every row
                if (!(image.getRGB(x, y) >> 24 == 0x00)) { //if Alpha is 0 (pixel is transparent)
                    area.add(new Area(new Rectangle(x, y, 1, 1))); //The transparent pixel is added to the area
                }
            }
        }

        // Save hitbox
        new File("res/hitboxes").mkdirs();
        String hitboxPath = "res/hitboxes/" + hitboxName;

        try {
            FileOutputStream fos = new FileOutputStream(hitboxPath);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(AffineTransform.getTranslateInstance(0, 0).createTransformedShape(area)); //Write area to file. Only transformed shapes are serializable
            oos.close(); //close file
            fos.close();
            System.out.println("Successfully exported hitbox to " + hitboxPath);
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
