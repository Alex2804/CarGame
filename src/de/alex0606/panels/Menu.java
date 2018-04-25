package de.alex0606.panels;

import de.alex0606.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public abstract class Menu extends JPanel{
    private ArrayList<JComponent> components = new ArrayList<>();
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    public void addListener(Listener listener){
        listeners.add(listener);
    }

    public Menu(){
        setLayout(new GridBagLayout());
        resetBackground();
    }
    public void addComponent(JComponent component, GridBagConstraints c){
        c.ipady *= MainWindow.scale;
        c.ipadx *= MainWindow.scale;
        c.insets = new Insets((int)(c.insets.top*MainWindow.scale), (int)(c.insets.left*MainWindow.scale), (int)(c.insets.bottom*MainWindow.scale), (int)(c.insets.right*MainWindow.scale));
        components.add(component);
        if(component instanceof JButton)
            component.setPreferredSize(new Dimension((int)(component.getPreferredSize().width*MainWindow.scale), (int)(component.getPreferredSize().height*MainWindow.scale)));
        add(component, c);
    }
    public void removeComponent(JComponent component){
        components.remove(component);
        getLayout().removeLayoutComponent(component);
    }
    public ArrayList<Listener> getListeners(){
        return listeners;
    }

    public void resetBackground(){
        setBackground(new Color(255, 255, 255, 150));
    }
}

class StartMenu extends Menu{
    ImageIcon logo = null;
    JLabel logoLabel;
    JButton startButton;
    JButton leaveButton;

    public StartMenu(){
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 0, 20, 0);
        c.fill = c.HORIZONTAL;
        c.ipady = 30;

        logo = new ImageIcon("res/logo.png");
        logo = new ImageIcon(logo.getImage().getScaledInstance((int)(logo.getIconWidth() * MainWindow.scale), (int)(logo.getIconHeight() * MainWindow.scale), BufferedImage.SCALE_SMOOTH));
        logoLabel = new JLabel(logo);
        c.gridy = 0;
        addComponent(logoLabel, c);

        startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Listener listener : getListeners()){
                    listener.startGame();
                }
            }
        });
        c.gridy = 1;
        addComponent(startButton, c);

        leaveButton = new JButton("Leave Game");
        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        c.gridy = 2;
        addComponent(leaveButton, c);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
            }
        });
    }
}

class PauseMenu extends Menu{
    JButton continueButton;
    JButton restartButton;
    JButton homeButton;

    public PauseMenu(){
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 0, 20, 0);
        c.fill = c.HORIZONTAL;
        c.ipady = 30;

        continueButton = new JButton("Continue Game");
        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Listener listener : getListeners()){
                    listener.continueGame();
                }
            }
        });
        c.gridy = 0;
        addComponent(continueButton, c);

        restartButton = new JButton("Restart Game");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Listener listener : getListeners()){
                    listener.startGame();
                }
            }
        });
        c.gridy = 1;
        addComponent(restartButton, c);

        homeButton = new JButton("Back To Start Menu");
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Listener listener : getListeners()){
                    listener.startMenu();
                }
            }
        });
        c.gridy = 2;
        addComponent(homeButton, c);
    }
}

class GameOverMenu extends Menu{
    ImageIcon gameOverIcon = null;
    JLabel gameOverLabel;
    JButton restartButton;
    JButton homeButton;
    JLabel scoreLabel;

    public GameOverMenu(){
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 0, 20, 0);
        c.fill = c.HORIZONTAL;
        c.ipady = 30;


        gameOverIcon = new ImageIcon("res/gameover.png");
        gameOverIcon = new ImageIcon(gameOverIcon.getImage().getScaledInstance((int)(gameOverIcon.getIconWidth() * MainWindow.scale), (int)(gameOverIcon.getIconHeight() * MainWindow.scale), BufferedImage.SCALE_SMOOTH));
        gameOverLabel = new JLabel(gameOverIcon);
        c.gridy = 0;
        addComponent(gameOverLabel, c);

        restartButton = new JButton("Restart Game");
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Listener listener : getListeners()){
                    listener.startGame();
                }
            }
        });
        c.gridy = 1;
        addComponent(restartButton, c);

        homeButton = new JButton("Back To Start Menu");
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Listener listener : getListeners()){
                    listener.startMenu();
                }
            }
        });
        c.gridy = 2;
        addComponent(homeButton, c);

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, (int)(40*MainWindow.scale)));
        scoreLabel.setForeground(new Color(255, 255,255));
        c.gridy = 3;
        addComponent(scoreLabel, c);
    }

    public void setScore(int score){
        scoreLabel.setText("Score: " + score);
    }
}