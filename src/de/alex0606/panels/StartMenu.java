package de.alex0606.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class StartMenu extends JPanel{
    JButton startButton;
    JLabel logoLabel;

    private boolean scaling = false;

    public StartMenu(){
        initUI();
    }
    public void initUI(){
        setBackground(new Color(255, 255, 255, 150));

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = c.BOTH;
        setLayout(layout);

        logoLabel = new JLabel(new ImageIcon("res/logo.png"));
        c.insets = new Insets(200, 0, 0, 0);
        c.gridx = 0;
        c.gridy = 0;
        add(logoLabel, c);

        startButton = new JButton("Start Game");
        c.gridx = 0;
        c.gridy = 1;
        add(startButton, c);
    }
}
