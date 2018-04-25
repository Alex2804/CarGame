package de.alex0606.panels;

public interface Listener {
    void gameStarted();
    void gamePaused();
    void gameOver(int score);
    void gameStartInitialized();
    void startGame();
    void pauseGame();
    void continueGame();
    void startMenu();
}
