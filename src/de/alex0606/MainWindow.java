package de.alex0606;

import de.alex0606.panels.GameBoard;
import de.alex0606.panels.PanelManager;

import javax.swing.*;
import javax.tools.Tool;
import java.awt.*;

public class MainWindow extends JFrame{
    //public static double scale = Toolkit.getDefaultToolkit().getScreenSize().width / 1920D;
    public static double scale = Toolkit.getDefaultToolkit().getScreenResolution() / 96D;
    //public static double scale = 3840 / 1920D;
    //public static double scale = 1280 / 1920D;

    public MainWindow(){
        setIconImage(new ImageIcon("res/logo.png").getImage());
        setResizable(false);

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
        try {
            // Set cross-platform Java L&F (also called "Metal")
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException e) {
            // handle exception
        }
        catch (ClassNotFoundException e) {
            // handle exception
        }
        catch (InstantiationException e) {
            // handle exception
        }
        catch (IllegalAccessException e) {
            // handle exception
        }
        CleanSetup cs = new CleanSetup();
        //cs.start();
        MainWindow mainWindow = new MainWindow();
    }
}
