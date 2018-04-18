package de.alex0606.panels;

import de.alex0606.ObstacleManager;
import de.alex0606.StreetManager;
import de.alex0606.objects.cars.PlayerCar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class PanelManager extends JPanel implements ActionListener, GameBoardListener{
    GameBoard gameBoard;

    JPanel startMenuPanel = new JPanel();
    BlurGameBoard blurGameBoard;
    StartMenu startMenu;

    CardLayout cardLayout;
    JComponent current = new JPanel();

    public PanelManager(){
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
            }
        });
        initUI();
    }
    private void initUI(){
        addKeyListener(new TAdapter());
        setFocusable(true);
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        initGameBoard();
        initStartMenu();

        showStartMenu();
    }
    private void initGameBoard(){
        gameBoard = new GameBoard();
        gameBoard.addListener(this);
        add(gameBoard, "gameBoard");
    }
    private void initStartMenu(){
        startMenuPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = c.BOTH;

        startMenu = new StartMenu();
        startMenu.startButton.addActionListener(this);
        startMenuPanel.add(startMenu, c);

        blurGameBoard = new BlurGameBoard();
        startMenuPanel.add(blurGameBoard, c);

        add(startMenuPanel, "startMenu");

    }

    public void showStartMenu(){
        blurGameBoard.drawGameBoard(gameBoard);
        if(!current.equals(startMenuPanel))
            current = startMenu;
            cardLayout.show(this,"startMenu");
    }
    public void updateStartMenu(){
        blurGameBoard.drawGameBoard(gameBoard);
    }
    public void showGameBoard(){
        if(!current.equals(gameBoard))
            current = gameBoard;
            cardLayout.show(this, "gameBoard");
    }

    @Override
    public void gameStarted() {
        showGameBoard();
    }
    @Override
    public void gamePaused() {

    }
    @Override
    public void gameOver() {
        showStartMenu();
    }
    @Override
    public void gameStartInitialized() {
        showGameBoard();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == startMenu.startButton){
            gameBoard.initializeStart();
        }
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            if(current.equals(gameBoard))
                gameBoard.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if(current.equals(gameBoard))
                gameBoard.keyPressed(e);
        }
    }
}


class BlurGameBoard extends GameBoard{
    public static float[] blurKernel9 = {
            0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f,
            0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f,
            0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f,
            0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f,
            0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.10000f, 0.01125f, 0.01125f, 0.01125f, 0.01125f,
            0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f,
            0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f,
            0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f,
            0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f, 0.01125f
    };

    public static float[] blurKernel7 = {
            0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f,
            0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f,
            0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f,
            0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f,
            0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f,
            0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f,
            0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f, 0.0200f
    };

    public static float[] blurKernel5 = {
            0.0400f, 0.0400f, 0.0400f, 0.0400f, 0.0400f,
            0.0400f, 0.0400f, 0.0400f, 0.0400f, 0.0400f,
            0.0400f, 0.0400f, 0.0400f, 0.0400f, 0.0400f,
            0.0400f, 0.0400f, 0.0400f, 0.0400f, 0.0400f,
            0.0400f, 0.0400f, 0.0400f, 0.0400f, 0.0400f
    };

    public static float[] blurKernel3 = {
            0.1f, 0.1f, 0.1f,
            0.1f, 0.1f, 0.1f,
            0.1f, 0.1f, 0.1f,
    };

    public void drawGameBoard(GameBoard gameBoard){
        streetManager = gameBoard.streetManager;
        obstacles = gameBoard.obstacles;
        car = gameBoard.car;
        fuelBelowCar = gameBoard.fuelBelowCar;
        score = gameBoard.score;
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        super.paintComponent(bufferedImage.getGraphics());

        ConvolveOp op = new ConvolveOp(new Kernel(9, 9, blurKernel9));
        //ConvolveOp op = new ConvolveOp(new Kernel(7, 7, blurKernel7));
        //ConvolveOp op = new ConvolveOp(new Kernel(5, 5, blurKernel5));
        //ConvolveOp op = new ConvolveOp(new Kernel(3, 3, blurKernel3));
        Image image = op.filter(bufferedImage, null);
        g.drawImage(image, 0, 0, null);
    }
}