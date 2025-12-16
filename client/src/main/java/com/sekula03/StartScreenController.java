package com.sekula03;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import java.io.IOException;

public class StartScreenController {

    @FXML
    private Label online_label;
    private static StartScreenController instance;
    private static Scene scene;

    @FXML
    private void initialize() {
        instance = this;
    }

    public void playMultiplayer() {
        ChessGame.play("0");
        ChessGame.waitForOpponent();
    }

    public void playSingleplayer() {
        ChessGame.chooseLevel();
    }

    public static void setOnline(int online) {
        Platform.runLater(() -> instance.online_label.setText(String.format("%d online", online)));
    }

    public static void start() {
        try {
            if (scene == null) {
                FXMLLoader fxmlLoader = new FXMLLoader(ChessGame.class.getResource("/start_screen.fxml"));
                scene = new Scene(fxmlLoader.load());
            }
            ChessGame.stage.setScene(scene);
        } catch (IOException _) {}
    }

}
