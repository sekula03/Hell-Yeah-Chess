package com.sekula03;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class Timer extends Label implements Runnable {

    private int time;
    private boolean running = false, alive = true;
    private final ChessGame.END_STATE endState;

    public Timer(int minutes, int seconds, ChessGame.END_STATE endState) {
        time = minutes * 60 + seconds;
        setText(String.format("%02d:%02d", minutes, seconds));
        this.endState = endState;
    }

    @Override
    public void run() {
        try {
            while (time > 0) {
                if (!running) synchronized (this) { wait(); }
                if (!alive) break;
                Thread.sleep(1000);
                updateTime();
            }
            if (time == 0) ChessGame.endGame(endState);
        } catch (InterruptedException _) {}
    }

    private void updateTime() {
        time--;
        int minutes = time / 60;
        int seconds = time % 60;
        Platform.runLater(() -> setText(String.format("%02d:%02d", minutes, seconds)));
    }

    public synchronized void pause() {
        running = false;
    }

    public synchronized void play() {
        running = true;
        notify();
    }

    public synchronized void stop() {
        alive = false;
        running = true;
        notify();
    }
}
