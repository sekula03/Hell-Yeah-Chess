package com.sekula03;

import com.sekula03.figures.Figure;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends Pane {

    private Figure figure;
    private final int x, y;
    private final Rectangle rect;
    private final ImageView imageView;
    private int border = 3;
    public static final int TILE_SIZE = 75;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        setOnMouseClicked(e -> GameController.click(this));
        setPrefSize(TILE_SIZE, TILE_SIZE);
        rect = new Rectangle(TILE_SIZE-2, TILE_SIZE-2, Color.TRANSPARENT);
        rect.setLayoutX(1);
        rect.setLayoutY(1);
        rect.setStrokeWidth(0);
        imageView = new ImageView();
        imageView.setFitHeight(TILE_SIZE);
        imageView.setFitWidth(TILE_SIZE);
        getChildren().addAll(imageView, rect);
    }

    public void highlight(boolean selected) {
        rect.setStroke(selected ? Color.RED : Color.YELLOW);
        rect.setStrokeWidth(border);
        border = ~border & 3;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setFigure(Figure figure) {
        this.figure = figure;
        imageView.setImage(figure == null ? null : figure.getImage());
    }

    public Figure getFigure() {
        return figure;
    }

}
