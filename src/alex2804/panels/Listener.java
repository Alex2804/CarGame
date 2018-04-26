package alex2804.panels;

public interface Listener {
    void gameStarted();
    void gamePaused();
    void gameOver(int score);
    void gameStartInitialized();
    void startGame();
    void pauseGame();
    void continueGame();
    void startMenu();
    void settings();
    void activatePoliceCar(boolean policeCar);
    void activateSound(boolean sound);
}
