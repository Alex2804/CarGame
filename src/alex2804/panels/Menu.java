package alex2804.panels;

import alex2804.MainWindow;
import alex2804.ResourceBundleEx;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

/**
* The abstract Menu class is the parent of alle shown menus. Components can easily get added with GridBagConstraints.
* By default the Menus are Semitransparent with an alpha of 150. Passed JButtons are scaled automatically.
**/
public abstract class Menu extends JPanel{
    private ArrayList<JComponent> components = new ArrayList<>();
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    public void addListener(Listener listener){
        listeners.add(listener);
    }

    public int alpha = 150;

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
        setBackground(new Color(255, 255, 255, alpha));
    }
}

//All following classes are based on Menu and add individual Components for each Menu
class StartMenu extends Menu implements LanguageListener{
    ImageIcon logo = null;
    JLabel logoLabel;
    JButton startButton;
    JButton leaveButton;

    public StartMenu(){
        ResourceBundleEx.addListener(this);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 0, 20, 0);
        c.fill = c.HORIZONTAL;
        c.ipady = 30;

        logo = new ImageIcon("res/images/logo.png");
        logo = new ImageIcon(logo.getImage().getScaledInstance((int)(logo.getIconWidth() * MainWindow.scale), (int)(logo.getIconHeight() * MainWindow.scale), BufferedImage.SCALE_SMOOTH));
        logoLabel = new JLabel(logo);
        c.gridy = 0;
        addComponent(logoLabel, c);

        startButton = new JButton(ResourceBundleEx.getWord("start_game"));
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

        leaveButton = new JButton(ResourceBundleEx.getWord("leave_game"));
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

    @Override
    public void languageChanged() {
        startButton.setText(ResourceBundleEx.getWord("start_game"));
        leaveButton.setText(ResourceBundleEx.getWord("leave_game"));
    }
}

class PauseMenu extends Menu implements LanguageListener{
    JButton continueButton;
    JButton restartButton;
    JButton homeButton;

    public PauseMenu(){
        ResourceBundleEx.addListener(this);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 0, 20, 0);
        c.fill = c.HORIZONTAL;
        c.ipady = 30;

        continueButton = new JButton(ResourceBundleEx.getWord("continue_game"));
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

        restartButton = new JButton(ResourceBundleEx.getWord("restart_game"));
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

        homeButton = new JButton(ResourceBundleEx.getWord("back_to_start_menu"));
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

    @Override
    public void languageChanged() {
        homeButton.setText(ResourceBundleEx.getWord("back_to_start_menu"));
        restartButton.setText(ResourceBundleEx.getWord("restart_game"));
        continueButton.setText(ResourceBundleEx.getWord("continue_game"));
    }
}

class GameOverMenu extends Menu implements LanguageListener{
    ImageIcon gameOverIcon = null;
    JLabel gameOverLabel;
    JButton restartButton;
    JButton homeButton;
    JLabel scoreLabel;

    public GameOverMenu(){
        ResourceBundleEx.addListener(this);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 0, 20, 0);
        c.fill = c.HORIZONTAL;
        c.ipady = 30;


        gameOverIcon = new ImageIcon("res/images/gameover.png");
        gameOverIcon = new ImageIcon(gameOverIcon.getImage().getScaledInstance((int)(gameOverIcon.getIconWidth() * MainWindow.scale), (int)(gameOverIcon.getIconHeight() * MainWindow.scale), BufferedImage.SCALE_SMOOTH));
        gameOverLabel = new JLabel(gameOverIcon);
        c.gridy = 0;
        addComponent(gameOverLabel, c);

        restartButton = new JButton(ResourceBundleEx.getWord("restart_game"));
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

        homeButton = new JButton(ResourceBundleEx.getWord("back_to_start_menu"));
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

        scoreLabel = new JLabel(ResourceBundleEx.getWord("score") + ": 0");
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, (int)(40*MainWindow.scale)));
        scoreLabel.setForeground(new Color(255, 255,255));
        c.gridy = 3;
        addComponent(scoreLabel, c);
    }

    public void setScore(int score){
        scoreLabel.setText(ResourceBundleEx.getWord("score") + ": " + score);
    }

    @Override
    public void languageChanged() {
        homeButton.setText(ResourceBundleEx.getWord("back_to_start_menu"));
        restartButton.setText(ResourceBundleEx.getWord("restart_game"));
    }
}

class SettingsMenu extends Menu implements LanguageListener{
    JComboBox<String> language;
    HashMap<String, String> languages = createLanguages();
    JCheckBox policeCarCheckBox;
    JCheckBox soundCheckBox;

    private static HashMap<String, String> createLanguages() {
        HashMap<String,String> myMap = new HashMap<String,String>();
        myMap.put("English", "en");
        myMap.put("Deutsch", "ge");
        return myMap;
    }

    public SettingsMenu(){
        ResourceBundleEx.addListener(this);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(20, 0, 20, 0);

        language = new JComboBox<String>(languages.keySet().toArray(new String[languages.size()]));
        language.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ResourceBundleEx.setLanguage(languages.get(language.getSelectedItem().toString()));
            }
        });
        c.ipadx = 100;
        c.fill = c.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        addComponent(language, c);

        policeCarCheckBox = new JCheckBox(ResourceBundleEx.getWord("police_car"));
        policeCarCheckBox.setBackground(new Color(0, 0, 0, 0));
        policeCarCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    for(Listener listener : getListeners()){
                        listener.activatePoliceCar(true);
                    }
                } else {//checkbox has been deselected
                    for(Listener listener : getListeners()){
                        listener.activatePoliceCar(false);
                    }
                }
            }
        });
        policeCarCheckBox.setSelected(true);
        c.gridy = 1;
        addComponent(policeCarCheckBox, c);

        soundCheckBox = new JCheckBox(ResourceBundleEx.getWord("sound"));
        soundCheckBox.setBackground(new Color(0, 0, 0, 0));
        soundCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {//checkbox has been selected
                    for(Listener listener : getListeners()){
                        listener.activateSound(true);
                    }
                } else {//checkbox has been deselected
                    for(Listener listener : getListeners()){
                        listener.activateSound(false);
                    }
                }
            }
        });
        soundCheckBox.setSelected(false);
        soundCheckBox.setEnabled(false);
        c.gridy = 2;
        addComponent(soundCheckBox, c);

        language.setSelectedItem("English");
    }

    @Override
    public void languageChanged() {
        policeCarCheckBox.setText(ResourceBundleEx.getWord("police_car"));
        soundCheckBox.setText(ResourceBundleEx.getWord("sound"));
    }
}

class SettingsMenuControlPanel extends Menu{
    JButton homeButton;
    ImageIcon homeIcon;

    public SettingsMenuControlPanel(){
        GridBagConstraints c = new GridBagConstraints();

        homeIcon = new ImageIcon("res/images/home.png");
        homeIcon = new ImageIcon(homeIcon.getImage().getScaledInstance((int)(homeIcon.getIconWidth() * MainWindow.scale), (int)(homeIcon.getIconHeight() * MainWindow.scale), BufferedImage.SCALE_SMOOTH));
        homeButton = new JButton(homeIcon);
        homeButton.setBackground(new Color(0, 0, 0, 0));
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Listener listener : getListeners()){
                    listener.startMenu();
                }
            }
        });
        c.gridx = 1;
        c.gridy = 0;
        add(homeButton, c);

        c.weightx = 1;
        c.weighty = 1;
        c.fill = c.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        add(Box.createGlue(), c);

        alpha = 0;
        resetBackground();
    }
}


class StartMenuControlPanel extends Menu{
    JButton settingsButton;
    ImageIcon settingsIcon;

    public StartMenuControlPanel(){
        GridBagConstraints c = new GridBagConstraints();

        settingsIcon = new ImageIcon("res/images/settings.png");
        settingsIcon = new ImageIcon(settingsIcon.getImage().getScaledInstance((int)(settingsIcon.getIconWidth() * MainWindow.scale), (int)(settingsIcon.getIconHeight() * MainWindow.scale), BufferedImage.SCALE_SMOOTH));
        settingsButton = new JButton(settingsIcon);
        settingsButton.setBackground(new Color(0, 0, 0, 0));
        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(Listener listener : getListeners()){
                    listener.settings();
                }
            }
        });
        c.gridx = 1;
        c.gridy = 0;
        add(settingsButton, c);

        c.weightx = 1;
        c.weighty = 1;
        c.fill = c.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        add(Box.createGlue(), c);

        alpha = 0;
        resetBackground();
    }
}
