package com.sekula03;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;

public class LoginScreenController {

    @FXML
    private TextField username;
    @FXML
    private Label errorMessage;
    private static Stage login_stage;
    private boolean good = false;
    private final HashSet<String> taken_names = new HashSet<>();
    private static LoginScreenController instance;
    private static String name;

    @FXML
    public void initialize() {
        instance = this;
        username.textProperty().addListener((_, _, _) -> type());
    }

    public void type() {
        String input = username.getText();
        good = false;
        errorMessage.setStyle("-fx-text-fill: darkred");
        if (input.isEmpty()) errorMessage.setText("Name must have at least one character.");
        else if (input.length() > 20) errorMessage.setText("Name must have less than 20 characters.");
        else if (!Character.isLetter(input.charAt(0))) errorMessage.setText("Name must start with a letter.");
        else if (taken_names.contains(input)) errorMessage.setText("Name already taken.");
        else if (input.contains("#")) errorMessage.setText("Name cannot contain #.");
        else {
            good = true;
            errorMessage.setStyle("-fx-text-fill: lime;");
            errorMessage.setText("Looks alright...");
        }
    }

    public void submit() {
        if (!good) return;
        ChessGame.tryLogin(username.getText());
    }

    public static void start() {
        try {
            login_stage = new Stage();
            login_stage.setResizable(false);
            login_stage.initOwner(ChessGame.stage);
            login_stage.initModality(Modality.WINDOW_MODAL);
            login_stage.setOnCloseRequest(_ -> ChessGame.close());
            FXMLLoader fxmlLoader = new FXMLLoader(ChessGame.class.getResource("/login_screen.fxml"));
            Scene login_scene = new Scene(fxmlLoader.load());
            login_stage.setScene(login_scene);
            login_stage.show();
            login_stage.setX(ChessGame.stage.getX() + (ChessGame.stage.getWidth() - login_stage.getWidth()) / 2);
            login_stage.setY(ChessGame.stage.getY() + (ChessGame.stage.getHeight() - login_stage.getHeight()) / 2);
        } catch (IOException _) {}
    }

    public static void loginOutcome(boolean outcome) {
        Platform.runLater(() -> {
            if (outcome) {
                login_stage.close();
                name = instance.username.getText();
                return;
            }
            instance.good = false;
            instance.errorMessage.setStyle("-fx-text-fill: darkred;");
            instance.errorMessage.setText("Name already taken.");
            instance.taken_names.add(instance.username.getText());
        });
    }

    public static String getName() {
        return name;
    }

}
