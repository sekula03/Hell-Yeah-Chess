package com.sekula03;

import com.sekula03.net.Client;
import javafx.application.Application;
import javafx.stage.Stage;

public class ChessGame extends Application {

    public enum END_STATE {
        VICTORY, DEFEAT, DRAW;

        public String toString() {
            return switch (this) {
                case VICTORY -> "VICTORY";
                case DEFEAT -> "DEFEAT";
                case DRAW -> "DRAW";
            };
        }
    }

    public static Stage stage;
    private static final Client client = new Client();

    @Override
    public void start(Stage primary_stage) {
        stage = primary_stage;
        ChessGame.stage.setTitle("HELL YEAH CHESS!");
        stage.setResizable(false);
        stage.setOnCloseRequest(_ -> {
            client.close();
            GameController.stopSending();
            GameScreenController.stopTimer();
        });
        load();
        LoginScreenController.start();
        client.start();
    }

    public static void close() {
        client.close();
        stage.close();
    }

    public static void tryLogin(String name) {
        client.sendMsg(name);
    }

    public static void loginOutcome(boolean outcome) {
        LoginScreenController.loginOutcome(outcome);
    }

    public static void load() {
        StartScreenController.start();
        stage.show();
    }

    public static void startGame(int color, String opponent) {
        WaitingScreenController.stop();
        GameScreenController.start(color, LoginScreenController.getName(), opponent);
    }

    public static void endGame(END_STATE state) {
        client.sendMsg(Client.RESET_CODE);
        GameScreenController.endGame(state.toString());
        GameController.stopSending();
    }

    public static void forfeit() {
        client.sendMsg(Client.FORFEIT_CODE);
        endGame(END_STATE.DEFEAT);
    }

    public static void waitForOpponent() {
        WaitingScreenController.start();
    }

    public static void cancelWaiting() {
        client.cancelWaiting();
    }

    public static void chooseLevel() {
        LevelScreenController.start();
    }

    public static void sendMove(String move) {
        client.sendMsg(move);
        GameScreenController.pauseTimer();
    }

    public static void receiveMove(String move) {
        GameController.opponentMove(move);
        GameScreenController.playTimer();
    }

    public static void play(String game_mode) {
        client.setGameMode(game_mode);
        synchronized (client) { client.notifyAll(); }
    }

    public static void updateOnlineCount(int count) {
        StartScreenController.setOnline(count);
    }

    public static void startPromotion() {
        GameScreenController.createPromotion();
    }

    public static void endPromotion(char c) {
        GameController.endPromotion(c);
    }

    public static void main(String[] args) {
        launch();
    }

}
