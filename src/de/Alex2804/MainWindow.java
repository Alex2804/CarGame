package de.Alex2804;

import javax.swing.JFrame;
import java.awt.*;
import java.awt.event.*;

public class MainWindow extends JFrame{
    public MainWindow(){
        setSize(600, 1000);
        //setResizable(false);

        GameBoard gb = new GameBoard();
        add(gb);


        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String args[]){
        MainWindow mainWindow = new MainWindow();
    }
}
