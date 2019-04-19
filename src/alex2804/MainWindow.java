package alex2804;

import alex2804.panels.PanelManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainWindow extends JFrame{
    //public static double scale = Toolkit.getDefaultToolkit().getScreenSize().width / 1920D;
    public static double scale = Toolkit.getDefaultToolkit().getScreenResolution() / 96D;
    //public static double scale = 3840 / 1920D;
    //public static double scale = 1280 / 1920D;

    public MainWindow(){
        ResourceHelper.trySetIcon(this, "images/logo.png");
        setResizable(true);

        int width = 600;
        int height = 1000;
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        double scale = size.height / 1200D;
        this.scale *= scale;
        setPreferredSize(new Dimension((int)(width*this.scale), (int)(height*this.scale)));
        setMinimumSize(getPreferredSize());
        pack();

        PanelManager panelManager = new PanelManager();
        add(panelManager);

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String args[]){
        System.setProperty("sun.java2d.opengl", "True");
        //CleanSetup cs = new CleanSetup();
        //cs.start();
        if (args.length == 1 && args[0].contains("writehitboxes")) {
            ResourceHelper.writeHitbox("images/playercar.png", "playercarhitbox.ser");
            ResourceHelper.writeHitbox("images/enemycar.png", "enemycarhitbox.ser");
            ResourceHelper.writeHitbox("images/policecarleft.png", "policecarhitbox.ser");
        } else {
            new MainWindow();
        }
    }
}
