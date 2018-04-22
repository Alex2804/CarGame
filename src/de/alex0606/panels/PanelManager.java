package de.alex0606.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;

public class PanelManager extends JPanel implements Listener, ActionListener{
    BlurGameBoard gameBoard;
    StartMenu startMenu;
    PauseMenu pauseMenu;
    GameOverMenu gameOverMenu;

    Timer startMenuTimer = new Timer(10, this);

    GridBagConstraints constraints = new GridBagConstraints();

    public PanelManager(){
        setFocusable(true);
        addKeyListener(new TAdapter());
        initUI();
        showStartMenu();
    }
    private void initUI(){
        setLayout(new GridBagLayout());
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = constraints.BOTH;
        constraints.weighty = 1;
        constraints.weightx = 1;

        initStartMenu();
        initPauseMenu();
        initGameOverMenu();

        initGameBoard(constraints);
    }
    private void initGameBoard(GridBagConstraints c){
        gameBoard = new BlurGameBoard();
        //gameBoard.addListener(this);
        gameBoard.update();
        add(gameBoard, c);
    }
    private void initStartMenu(){
        startMenu = new StartMenu();
        startMenu.addListener(this);
        add(startMenu, constraints);
        startMenu.setVisible(false);
    }
    private void initPauseMenu(){
        pauseMenu = new PauseMenu();
        pauseMenu.addListener(this);
        add(pauseMenu, constraints);
        pauseMenu.setVisible(false);
    }
    private void initGameOverMenu(){
        gameOverMenu = new GameOverMenu();
        gameOverMenu.addListener(this);
        add(gameOverMenu, constraints);
        gameOverMenu.setVisible(false);
    }

    public void showStartMenu(){
        if(!startMenu.isVisible()) {
            startMenu.setVisible(true);
            startMenuTimer.start();
            repaint();
        }
    }
    public void hideStartMenu(){
        if(startMenu.isVisible()) {
            startMenuTimer.stop();
            startMenu.setVisible(false);
            startMenu.resetBackground();
            repaint();
        }
    }
    public void showPauseMenu(){
        if(!pauseMenu.isVisible()){
            pauseMenu.setVisible(true);
            repaint();
        }
    }
    public void hidePauseMenu(){
        if(pauseMenu.isVisible()){
            pauseMenu.setVisible(false);
            repaint();
        }
    }
    public void showGameOverMenu(){
        if(!gameOverMenu.isVisible()){
            gameOverMenu.setVisible(true);
            repaint();
        }
    }
    public void hideGameOverMenu(){
        if(gameOverMenu.isVisible()){
            gameOverMenu.setVisible(false);
            repaint();
        }
    }
    public void hideMenus(){
        hideStartMenu();
        hidePauseMenu();
        hideGameOverMenu();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == startMenuTimer){
            gameBoard.update();
            repaint();
        }
    }

    @Override
    public void gameStarted() {
        gameBoard.setRadius(0);
        hideMenus();
    }
    @Override
    public void gamePaused() {
        gameBoard.resetBlur();
        showPauseMenu();
    }
    @Override
    public void gameOver() {
        gameBoard.resetBlur();
        showGameOverMenu();
    }
    @Override
    public void gameStartInitialized() {
        gameBoard.setRadius(0);
        hideMenus();
    }
    @Override
    public void startGame() {
        hideMenus();
        gameBoard.setRadius(0);
        gameBoard.initializeStart();
    }
    @Override
    public void pauseGame() {
        gameBoard.pause();
        gameBoard.resetBlur();
        showPauseMenu();
    }
    @Override
    public void continueGame() {
        gameBoard.continueGame();
        gameBoard.setRadius(0);
        hidePauseMenu();
    }
    @Override
    public void startMenu() {
        gameBoard.resetBlur();
        gameBoard.reset();
        hidePauseMenu();
        hideGameOverMenu();
        showStartMenu();
    }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyReleased(KeyEvent e) {
            if(gameBoard.getGameOver() == false)
                if(e.getKeyCode() == KeyEvent.VK_P){
                    pauseGame();
                }
                gameBoard.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if(gameBoard.getGameOver() == false)
                gameBoard.keyPressed(e);
        }
    }
}


class FastBlurFilter implements BufferedImageOp{
    private int radius;

    public FastBlurFilter(int radius) {
        this.radius = radius;
    }
    public void setRadius(int radius){
        this.radius = radius > 0 ? radius : 0;
    }
    public int getRadius() {
        return radius;
    }

    public BufferedImage filter(BufferedImage src, BufferedImage dst) {
        int width = src.getWidth();
        int height = src.getHeight();

        if (dst == null) {
            dst = createCompatibleDestImage(src, null);
        }

        int[] srcPixels = new int[width * height];
        int[] dstPixels = new int[width * height];

        getPixels(src, 0, 0, width, height, srcPixels);
        // horizontal pass
        blur(srcPixels, dstPixels, width, height, radius);
        // vertical pass
        blur(dstPixels, srcPixels, height, width, radius);
        // the result is now stored in srcPixels due to the 2nd pass
        setPixels(dst, 0, 0, width, height, srcPixels);

        return dst;
    }

    public int[] getPixels(BufferedImage img,
                                  int x, int y, int w, int h, int[] pixels) {
        int imageType = img.getType();
        if (imageType == BufferedImage.TYPE_INT_ARGB ||
                imageType == BufferedImage.TYPE_INT_RGB) {
            Raster raster = img.getRaster();
            return (int[]) raster.getDataElements(x, y, w, h, pixels);
        }

        // Unmanages the image
        return img.getRGB(x, y, w, h, pixels, 0, w);
    }

    public void setPixels(BufferedImage img,
                                 int x, int y, int w, int h, int[] pixels) {
        int imageType = img.getType();
        if (imageType == BufferedImage.TYPE_INT_ARGB ||
                imageType == BufferedImage.TYPE_INT_RGB) {
            WritableRaster raster = img.getRaster();
            raster.setDataElements(x, y, w, h, pixels);
        } else {
            // Unmanages the image
            img.setRGB(x, y, w, h, pixels, 0, w);
        }
    }

    static void blur(int[] srcPixels, int[] dstPixels,
                     int width, int height, int radius) {
        int windowSize = radius * 2 + 1;
        int radiusPlusOne = radius + 1;

        int sumAlpha;
        int sumRed;
        int sumGreen;
        int sumBlue;

        int srcIndex = 0;
        int dstIndex;
        int pixel;

        int[] sumLookupTable = new int[256 * windowSize];
        for (int i = 0; i < sumLookupTable.length; i++) {
            sumLookupTable[i] = i / windowSize;
        }

        int[] indexLookupTable = new int[radiusPlusOne];
        if (radius < width) {
            for (int i = 0; i < indexLookupTable.length; i++) {
                indexLookupTable[i] = i;
            }
        } else {
            for (int i = 0; i < width; i++) {
                indexLookupTable[i] = i;
            }
            for (int i = width; i < indexLookupTable.length; i++) {
                indexLookupTable[i] = width - 1;
            }
        }

        for (int y = 0; y < height; y++) {
            sumAlpha = sumRed = sumGreen = sumBlue = 0;
            dstIndex = y;

            pixel = srcPixels[srcIndex];
            sumAlpha += radiusPlusOne * ((pixel >> 24) & 0xFF);
            sumRed   += radiusPlusOne * ((pixel >> 16) & 0xFF);
            sumGreen += radiusPlusOne * ((pixel >>  8) & 0xFF);
            sumBlue  += radiusPlusOne * ( pixel        & 0xFF);

            for (int i = 1; i <= radius; i++) {
                pixel = srcPixels[srcIndex + indexLookupTable[i]];
                sumAlpha += (pixel >> 24) & 0xFF;
                sumRed   += (pixel >> 16) & 0xFF;
                sumGreen += (pixel >>  8) & 0xFF;
                sumBlue  +=  pixel        & 0xFF;
            }

            for  (int x = 0; x < width; x++) {
                dstPixels[dstIndex] = sumLookupTable[sumAlpha] << 24 |
                        sumLookupTable[sumRed]   << 16 |
                        sumLookupTable[sumGreen] <<  8 |
                        sumLookupTable[sumBlue];
                dstIndex += height;

                int nextPixelIndex = x + radiusPlusOne;
                if (nextPixelIndex >= width) {
                    nextPixelIndex = width - 1;
                }

                int previousPixelIndex = x - radius;
                if (previousPixelIndex < 0) {
                    previousPixelIndex = 0;
                }

                int nextPixel = srcPixels[srcIndex + nextPixelIndex];
                int previousPixel = srcPixels[srcIndex + previousPixelIndex];

                sumAlpha += (nextPixel     >> 24) & 0xFF;
                sumAlpha -= (previousPixel >> 24) & 0xFF;

                sumRed += (nextPixel     >> 16) & 0xFF;
                sumRed -= (previousPixel >> 16) & 0xFF;

                sumGreen += (nextPixel     >> 8) & 0xFF;
                sumGreen -= (previousPixel >> 8) & 0xFF;

                sumBlue += nextPixel & 0xFF;
                sumBlue -= previousPixel & 0xFF;
            }

            srcIndex += width;
        }
    }

    public BufferedImage createCompatibleDestImage(BufferedImage src,
                                                   ColorModel destCM) {
        if (destCM == null) {
            destCM = src.getColorModel();
        }

        return new BufferedImage(destCM,
                destCM.createCompatibleWritableRaster(
                        src.getWidth(), src.getHeight()),
                destCM.isAlphaPremultiplied(), null);
    }
    public Rectangle2D getBounds2D(BufferedImage src) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        return (Point2D) srcPt.clone();
    }
    public RenderingHints getRenderingHints() {
        return null;
    }
}
class BlurGameBoard extends GameBoard{
    public int maximumBlurRadius = 15;
    public FastBlurFilter filter = new FastBlurFilter(maximumBlurRadius);

    public boolean reduceBlur(int count){
        if(filter.getRadius() > 0){
            filter.setRadius(filter.getRadius() - count);
            return true;
        }else
            return false;
    }
    public boolean increaseBlur(int count){
        if(filter.getRadius() < maximumBlurRadius){
            filter.setRadius(filter.getRadius() + count > maximumBlurRadius ? maximumBlurRadius : filter.getRadius() + count);
            return true;
        }else
            return false;
    }
    public void resetBlur(){
        filter.setRadius(maximumBlurRadius);
    }
    public void setRadius(int radius){
        filter.setRadius(radius);
    }
    public int getRadius(){
        return filter.getRadius();
    }

    public void update(){
        streetManager.update();
        car.moveTo(getWidth()/2 - car.getWidth()/2, getHeight()*0.6);
    }

    @Override
    public void paintComponent(Graphics g) {
        if(filter.getRadius() > 0){
            BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            super.paintComponent(bufferedImage.createGraphics());

            BufferedImage image = filter.filter(bufferedImage, null);
            g.drawImage(image, 0, 0, null);
        }
        else
            super.paintComponent(g);
    }
}

