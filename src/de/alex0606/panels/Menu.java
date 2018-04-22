package de.alex0606.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public abstract class Menu extends JPanel implements ActionListener{
    private ArrayList<JComponent> components = new ArrayList<>();
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    public void addListener(Listener listener){
        listeners.add(listener);
    }
    private Timer alphaMinusTimer = new Timer(10, this);
    private Timer alphaPlusTimer = new Timer(10, this);
    private int alphaDif = 1;
    private int maxAlpha = 150;

    public Menu(){
        setLayout(new GridBagLayout());
        resetBackground();
    }
    public void addComponent(JComponent component, GridBagConstraints c){
        components.add(component);
        add(component, c);
    }
    public void removeComponent(JComponent component){
        components.remove(component);
        getLayout().removeLayoutComponent(component);
    }
    public ArrayList<Listener> getListeners(){
        return listeners;
    }

    public void hideSlow(int time){
        if(time > 0) {
            double delay = 0;
            alphaDif = 1;
            while (delay <= 0) {
                delay = (time / (getBackground().getAlpha() <= 0 ? 1 : getBackground().getAlpha() / alphaDif));
                if (delay <= 0)
                    alphaDif++;
            }
            alphaMinusTimer.setDelay((int) delay);
            alphaMinusTimer.start();
        }else{
            setBackground(new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(),
                    0));
        }
    }
    public void showSlow(int time){
        setBackground(new Color(getBackground().getRed(), getBackground().getGreen(), getBackground().getBlue(), 0));
        double delay = 0;
        alphaDif = 1;
        while (delay <= 0){
            delay = (time / ((maxAlpha - getBackground().getAlpha()) / alphaDif));
            if(delay <= 0)
                alphaDif++;
        }
        alphaPlusTimer.setDelay((int)delay);
        alphaPlusTimer.start();
    }
    public void resetBackground(){
        alphaMinusTimer.stop();
        setBackground(new Color(255, 255, 255, maxAlpha));
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == alphaMinusTimer){
            setBackground(new Color(getBackground().getRed(),
                    getBackground().getGreen(),
                    getBackground().getBlue(),
                    getBackground().getAlpha() - alphaDif < 0 ? 0 : getBackground().getAlpha()));
            if(getBackground().getAlpha() <= 0){
                alphaMinusTimer.stop();
            }
        }
        if(e.getSource() == alphaPlusTimer){
            setBackground(new Color(getBackground().getRed(),
                    getBackground().getGreen(),
                    getBackground().getBlue(),
                    getBackground().getAlpha() + alphaDif > maxAlpha ? maxAlpha : getBackground().getAlpha() + alphaDif));
            if(getBackground().getAlpha() >= maxAlpha){
                alphaPlusTimer.stop();
            }
        }
    }
}

class StartMenu extends Menu{
    JButton startButton;
    JButton leaveButton;

    public StartMenu(){
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 0, 20, 0);
        c.fill = c.HORIZONTAL;
        c.ipady = 30;

        startButton = new JButton("Start Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Listener listener : getListeners()){
                    listener.startGame();
                }
            }
        });
        c.gridy = 0;
        addComponent(startButton, c);

        leaveButton = new JButton("Leave Game");
        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        c.gridy = 1;
        addComponent(leaveButton, c);
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
    JButton restartButton;
    JButton homeButton;

    public GameOverMenu(){
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 0, 20, 0);
        c.fill = c.HORIZONTAL;
        c.ipady = 30;

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
