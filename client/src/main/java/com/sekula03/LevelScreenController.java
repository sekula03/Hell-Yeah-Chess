package com.sekula03;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LevelScreenController {

    private static Stage levels_stage;

    public static void start() {
        try {
            if (levels_stage == null) {
                levels_stage = new Stage();
                levels_stage.setResizable(false);
                levels_stage.initOwner(ChessGame.stage);
                levels_stage.initModality(Modality.WINDOW_MODAL);
                FXMLLoader fxmlLoader = new FXMLLoader(ChessGame.class.getResource("/levels_screen.fxml"));
                Scene levels_scene = new Scene(fxmlLoader.load());
                levels_stage.setScene(levels_scene);
            }
            levels_stage.show();
            levels_stage.setX(ChessGame.stage.getX() + (ChessGame.stage.getWidth() - levels_stage.getWidth()) / 2);
            levels_stage.setY(ChessGame.stage.getY() + (ChessGame.stage.getHeight() - levels_stage.getHeight()) / 2);

        } catch (Exception _) {}
    }

    public void play(ActionEvent event) {
        String id = ((Button)event.getSource()).getId();
        ChessGame.play(id.replaceAll("[^0-9]", ""));
        levels_stage.close();
    }
}
