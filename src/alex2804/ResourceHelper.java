package alex2804;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ResourceHelper {
    public static void trySetIcon(JFrame frame, String path) {
        try {
            Image image = ImageIO.read(ResourceHelper.class.getResource("/" + path));
            frame.setIconImage(image);
        } catch (IOException e){
            System.out.println("Failed to load resource from /" + path);
        }
    }
    public static ImageIcon getScaledIcon(String path, double factor) {
        try {
            BufferedImage image = ImageIO.read(ResourceHelper.class.getResource("/" + path));
            return new ImageIcon(image.getScaledInstance((int)Math.round(image.getWidth() * factor),
                    (int)Math.round(image.getHeight() * factor), BufferedImage.SCALE_SMOOTH));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load resource from /" + path);
        }
    }
}
