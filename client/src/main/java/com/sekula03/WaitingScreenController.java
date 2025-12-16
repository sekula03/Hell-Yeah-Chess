package com.sekula03;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class WaitingScreenController {

    private static SequentialTransition white_transition, black_transition;
    private static Stage waiting_stage;
    private static final int SQUARE_SIZE = 35;
    private static final double ANIMATION_DURATION = 8;
    private static boolean on = false;

    public static void start() {
        if (waiting_stage == null) {
            waiting_stage = new Stage();
            waiting_stage.setResizable(false);
            waiting_stage.initStyle(StageStyle.UNDECORATED);
            waiting_stage.initOwner(ChessGame.stage);
            waiting_stage.initModality(Modality.WINDOW_MODAL);

            Scene waiting_scene = createWaitScene();
            waiting_stage.setScene(waiting_scene);
        }

        waiting_stage.show();
        on = true;

        waiting_stage.setX(ChessGame.stage.getX() + (ChessGame.stage.getWidth() - waiting_stage.getWidth()) / 2);
        waiting_stage.setY(ChessGame.stage.getY() + (ChessGame.stage.getHeight() - waiting_stage.getHeight()) / 2);

        white_transition.play();
        black_transition.play();
    }

    public static void stop() {
        Platform.runLater(() -> {
            if (on) {
                on = false;
                white_transition.stop();
                black_transition.stop();
                waiting_stage.close();
            }
        });
    }

    private static Scene createWaitScene() {
        Pane pane;
        GridPane gridPane = new GridPane();
        for (int i = 0; i < GameController.N; i++) {
            for (int j = 0; j < GameController.N; j++) {
                pane = new Pane();
                pane.setPrefSize(SQUARE_SIZE, SQUARE_SIZE);
                pane.setStyle(GameScreenController.square_colors[(i+j)%2]);
                gridPane.add(pane, j, i);
            }
        }

        Label label = new Label("WAITING FOR OPPONENT...");
        label.setFont(Font.font("Arial", 18));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        label.setPrefHeight(50);
        label.setPrefWidth(GameController.N * SQUARE_SIZE);

        Button button = new Button("CANCEL");
        button.setStyle(
                "-fx-background-color: #cc0000;" +
                "-fx-font-size: 16px;" +
                "-fx-border-color: black;" +
                "-fx-border-width: 2px;" +
                "-fx-border-radius: 10px;" +
                "-fx-background-radius: 12px;"
        );
        button.setOnAction(_ -> {
            ChessGame.cancelWaiting();
            stop();
        });

        ImageView white_rook = new ImageView(new Image(ChessGame.class.getResourceAsStream("/chess/wR.png")));
        white_rook.setFitHeight(SQUARE_SIZE);
        white_rook.setFitWidth(SQUARE_SIZE);
        white_rook.setPreserveRatio(true);

        ImageView black_rook = new ImageView(new Image(ChessGame.class.getResourceAsStream("/chess/bR.png")));
        black_rook.setFitHeight(SQUARE_SIZE);
        black_rook.setFitWidth(SQUARE_SIZE);
        black_rook.setPreserveRatio(true);

        pane = new Pane();
        pane.setStyle("-fx-background-color: transparent;");
        pane.getChildren().addAll(white_rook, black_rook);
        white_rook.setLayoutX((double) SQUARE_SIZE /2);
        white_rook.setLayoutY((double) SQUARE_SIZE /2);
        black_rook.setLayoutX((double) SQUARE_SIZE /2);
        black_rook.setLayoutY((double) SQUARE_SIZE /2);
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(gridPane, pane, button);

        BorderPane root = new BorderPane();
        root.setCenter(stackPane);
        root.setBottom(label);
        BorderPane.setAlignment(label, Pos.CENTER);
        root.setStyle("-fx-border-color: black;" + "-fx-border-width: 2px;");

        setUpAnimation(white_rook, black_rook);

        return new Scene(root);
    }

    private static void setUpAnimation(ImageView white_rook, ImageView black_rook) {
        double sideLength = (GameController.N - 1) * SQUARE_SIZE;

        PathTransition top = createSideTransition(white_rook, 0, 0, sideLength, 0);
        PathTransition right = createSideTransition(white_rook, sideLength, 0, sideLength, sideLength);
        PathTransition bottom = createSideTransition(white_rook, sideLength, sideLength, 0, sideLength);
        PathTransition left = createSideTransition(white_rook, 0, sideLength, 0, 0);
        white_transition = new SequentialTransition();
        white_transition.getChildren().addAll(top, right, bottom, left);
        white_transition.setCycleCount(Animation.INDEFINITE);

        top = createSideTransition(black_rook, 0, 0, sideLength, 0);
        right = createSideTransition(black_rook, sideLength, 0, sideLength, sideLength);
        bottom = createSideTransition(black_rook, sideLength, sideLength, 0, sideLength);
        left = createSideTransition(black_rook, 0, sideLength, 0, 0);
        black_transition = new SequentialTransition();
        black_transition.getChildren().addAll(bottom, left, top, right);
        black_transition.setCycleCount(Animation.INDEFINITE);
    }

    private static PathTransition createSideTransition(ImageView rook, double startX, double startY, double endX, double endY) {
        Path path = new Path();
        path.getElements().add(new MoveTo(startX, startY));
        path.getElements().add(new LineTo(endX, endY));

        PathTransition side = new PathTransition();
        side.setNode(rook);
        side.setPath(path);
        side.setDuration(Duration.seconds(ANIMATION_DURATION/4));

        side.setInterpolator(new Interpolator() {
            @Override
            protected double curve(double t) {
                if (t < 0.5) return 4 * t * t * t;
                double x = 1 - (t - 0.5) * 2;
                return 1 - 0.5 * x * x * x;
            }
        });

        return side;
    }

}
