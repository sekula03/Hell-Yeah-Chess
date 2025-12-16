package com.sekula03;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;

import static com.sekula03.Tile.TILE_SIZE;

public class GameScreenController {

    private static Stage promotion_stage, game_over_stage;
    private static int color;
    private static Label label;
    private static Timer player_timer, opponent_timer;
    public static final String[] square_colors =
            new String[]{"-fx-background-color: #cfd8dc", "-fx-background-color: #546e7a"};

    public static void start(int c, String player, String opponent) {
        color = c;
        Platform.runLater(() -> {
            ChessGame.stage.setScene(createGameScene(player, opponent));
            ChessGame.stage.show();
            if (color == 0) player_timer.play();
            else opponent_timer.play();
        });
    }

    public static void createPromotion() {
        Platform.runLater(() -> {
            if (promotion_stage == null) {
                promotion_stage = new Stage();
                promotion_stage.setResizable(false);
                promotion_stage.initStyle(StageStyle.UNDECORATED);
                promotion_stage.initOwner(ChessGame.stage);
                promotion_stage.initModality(Modality.WINDOW_MODAL);

                Scene promotion_scene = createPromotionScene();
                promotion_stage.setScene(promotion_scene);
            }

            promotion_stage.show();

            promotion_stage.setX(ChessGame.stage.getX() - promotion_stage.getWidth());
            promotion_stage.setY(ChessGame.stage.getY() + (ChessGame.stage.getHeight() - ChessGame.stage.getScene().getHeight()) - 7);
        });
    }

    public static void endGame(String message) {
        Platform.runLater(() -> {
            label.setText(message);
            if (message.equals("VICTORY"))
                label.setStyle("-fx-text-fill: GREEN;" + "-fx-font-size: 25");
            else if (message.equals("DEFEAT"))
                label.setStyle("-fx-text-fill: RED;" + "-fx-font-size: 25");
            else
                label.setStyle("-fx-text-fill: YELLOW;" + "-fx-font-size: 25");

            if (game_over_stage == null) {
                game_over_stage = new Stage();
                game_over_stage.setResizable(false);
                game_over_stage.initStyle(StageStyle.UNDECORATED);
                game_over_stage.initOwner(ChessGame.stage);
                game_over_stage.initModality(Modality.WINDOW_MODAL);
                Button play_again = new Button("PLAY AGAIN");
                play_again.setFont(Font.font("Arial", 40));
                play_again.setStyle("-fx-background-color: yellow;" + "-fx-border-color: black;" + "-fx-border-width: 2px;");
                play_again.setOnAction(_ -> {
                    ChessGame.load();
                    game_over_stage.close();
                });
                Scene root = new Scene(play_again);
                game_over_stage.setScene(root);
            }

            game_over_stage.sizeToScene();
            game_over_stage.show();
            game_over_stage.setX(ChessGame.stage.getX() + (ChessGame.stage.getWidth() - game_over_stage.getWidth()) / 2);
            game_over_stage.setY(ChessGame.stage.getY() + (ChessGame.stage.getHeight() - game_over_stage.getHeight()) / 2);

        });
        stopTimer();
    }

    public static void pauseTimer() {
        player_timer.pause();
        opponent_timer.play();
    }

    public static void playTimer() {
        player_timer.play();
        opponent_timer.pause();
    }

    public static void stopTimer() {
        if (player_timer != null) {
            player_timer.stop();
            opponent_timer.stop();
        }
    }

    private static Scene createGameScene(String s1, String s2) {
        GameController.generateTable(color);

        GridPane gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: #000000");
        gridPane.setHgap(1);
        gridPane.setVgap(1);
        gridPane.setPadding(new Insets(0, -1, -1, 0));

        for (int i = 0; i < GameController.N; i++) {
            for (int j = 0; j < GameController.N; j++) {
                Tile tile = GameController.getTile(i, j);
                if (tile == null) continue;
                tile.setStyle(square_colors[(i+j)%2]);
                gridPane.add(tile, j, i);
            }
        }

        Pane bp = createBottomPane(s1, s2);

        BorderPane root = new BorderPane();
        root.setCenter(gridPane);
        root.setBottom(bp);
        BorderPane.setMargin(gridPane, new Insets(1));

        return new Scene(root);
    }

    private static Pane createBottomPane(String player, String opponent) {
        Pane bp = new Pane();
        bp.setPrefHeight(80);
        bp.setPrefWidth(600);

        BorderPane p1 = new BorderPane(), p2 = new BorderPane();
        p1.setPrefHeight(25);
        p1.setPrefWidth(300);
        p2.setPrefHeight(25);
        p2.setPrefWidth(300);
        p1.setLayoutX(30);
        p1.setLayoutY(10);
        p2.setLayoutX(30);
        p2.setLayoutY(45);
        p1.setStyle("-fx-background-color: #888888");
        p2.setStyle("-fx-background-color: #888888");

        Label player_name = new Label(player), opponent_name = new Label(opponent);
        player_name.setFont(Font.font("Arial", 25));
        opponent_name.setFont(Font.font("Arial", 25));

        player_timer = new Timer(15, 0, ChessGame.END_STATE.DEFEAT);
        opponent_timer = new Timer(15, 0, ChessGame.END_STATE.VICTORY);
        player_timer.setFont(Font.font("Arial", 25));
        opponent_timer.setFont(Font.font("Arial", 25));

        new Thread(player_timer).start();
        new Thread(opponent_timer).start();

        p1.setLeft(player_name);
        p2.setLeft(opponent_name);
        p1.setRight(player_timer);
        p2.setRight(opponent_timer);

        label = new Label("...");
        label.setPrefSize(100, 25);
        label.setFont(Font.font("Arial", 25));
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        label.setLayoutX(500);
        label.setLayoutY(45);

        ImageView white_flag = new ImageView(new Image(ChessGame.class.getResourceAsStream("/game_screen/surrender.png")));
        white_flag.setFitHeight(25);
        white_flag.setFitWidth(25);
        Button resign = new Button("resign", white_flag);
        resign.setPrefSize(100, 25);
        resign.setLayoutX(500);
        resign.setLayoutY(10);
        resign.setOnAction(_ -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Action");
            alert.setHeaderText("Are you sure you want to resing?");
            alert.setContentText("If you click OK the game will end instantly?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) ChessGame.forfeit();
        });

        bp.getChildren().addAll(p1, p2, label, resign);
        return bp;
    }

    private static Scene createPromotionScene() {
        Pane[] panes = new Pane[4];
        for (int i = 0; i < 4; i++) {
            panes[i] = new Pane();
            panes[i].setPrefSize(TILE_SIZE, TILE_SIZE);
            panes[i].setStyle(square_colors[0]);
        }

        String c = color == 0 ? "w" : "b";

        ImageView queen = new ImageView(new Image(ChessGame.class.getResourceAsStream(String.format("/chess/%sQ.png", c))));
        queen.setFitHeight(TILE_SIZE);
        queen.setFitWidth(TILE_SIZE);
        queen.setPreserveRatio(true);
        queen.setOnMouseClicked(_ -> {
            ChessGame.endPromotion('q');
            promotion_stage.close();
        });
        panes[0].getChildren().add(queen);
        ImageView rook = new ImageView(new Image(ChessGame.class.getResourceAsStream(String.format("/chess/%sR.png", c))));
        rook.setFitHeight(TILE_SIZE);
        rook.setFitWidth(TILE_SIZE);
        rook.setPreserveRatio(true);
        rook.setOnMouseClicked(_ -> {
            ChessGame.endPromotion('r');
            promotion_stage.close();
        });
        panes[1].getChildren().add(rook);
        ImageView bishop = new ImageView(new Image(ChessGame.class.getResourceAsStream(String.format("/chess/%sB.png", c))));
        bishop.setFitHeight(TILE_SIZE);
        bishop.setFitWidth(TILE_SIZE);
        bishop.setPreserveRatio(true);
        bishop.setOnMouseClicked(_ -> {
            ChessGame.endPromotion('b');
            promotion_stage.close();
        });
        panes[2].getChildren().add(bishop);
        ImageView knight = new ImageView(new Image(ChessGame.class.getResourceAsStream(String.format("/chess/%sN.png", c))));
        knight.setFitHeight(TILE_SIZE);
        knight.setFitWidth(TILE_SIZE);
        knight.setPreserveRatio(true);
        knight.setOnMouseClicked(_ -> {
            ChessGame.endPromotion('n');
            promotion_stage.close();
        });
        panes[3].getChildren().add(knight);

        VBox root = new VBox(1);
        root.setStyle("-fx-background-color: #000000");
        root.getChildren().addAll(panes[0], panes[1], panes[2], panes[3]);

        return new Scene(root);
    }
}
