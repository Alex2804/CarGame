package de.alex0606;

import de.alex0606.panels.GameBoard;
import de.alex0606.panels.PanelManager;
import de.alex0606.panels.StartMenu;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame{
    //public static double scale = Toolkit.getDefaultToolkit().getScreenSize().width / 1920;
    public static double scale = 1280 / 1920D;

    public MainWindow(){
        setIconImage(new ImageIcon("res/logo.png").getImage());
        setResizable(true);

        PanelManager panelManager = new PanelManager();
        add(panelManager);

        setSize(600, 1000);
        setPreferredSize(new Dimension(600, 1000));
        pack();
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

        MainWindow mainWindow = new MainWindow();
    }
}
