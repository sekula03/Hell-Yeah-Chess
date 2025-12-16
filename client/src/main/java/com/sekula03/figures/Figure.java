package com.sekula03.figures;

import com.sekula03.GameController;
import com.sekula03.Tile;
import javafx.scene.image.Image;

import java.util.HashSet;

public abstract class Figure {

    public enum COLOR {
        WHITE, BLACK;

        public COLOR opposite() {
            return this == WHITE ? BLACK : WHITE;
        }
    };

    protected Tile tile;
    protected final COLOR color;
    protected final HashSet<Tile> moves = new HashSet<>();
    protected King king;
    protected boolean has_moved = false;
    private final Image image;

    public Figure(Tile tile, COLOR color, King king, Image image) {
        this.tile = tile;
        this.color = color;
        this.image = image;
        this.king = king;
        tile.setFigure(this);
    }

    public Image getImage() {
        return image;
    }

    public COLOR getColor() {
        return color;
    }

    public void move(Tile new_tile) {
        new_tile.setFigure(this);
        tile.setFigure(null);
        tile = new_tile;
        has_moved = true;
    }

    public HashSet<Tile> getMoves() {
        return moves;
    }

    public abstract void loadMoves();

    protected void checkRank() {
        int x = tile.getX(), y = tile.getY(), y1;
        Tile t;
        for (int i = -1; i < 2; i+=2) {
            y1 = y + i;
            t = GameController.getTile(x, y1);
            while (t != null && t.getFigure() == null) {
                if (king.isLegal(tile, t)) moves.add(t);
                y1 += i;
                t = GameController.getTile(x, y1);
            }
            if (t != null && t.getFigure().getColor() != color && king.isLegal(tile, t)) moves.add(t);
        }
    }

    protected void checkFile() {
        int x = tile.getX(), y = tile.getY(), x1;
        Tile t;
        for (int i = -1; i < 2; i+=2) {
            x1 = x + i;
            t = GameController.getTile(x1, y);
            while (t != null && t.getFigure() == null) {
                if (king.isLegal(tile, t)) moves.add(t);
                x1 += i;
                t = GameController.getTile(x1, y);
            }
            if (t != null && t.getFigure().getColor() != color && king.isLegal(tile, t)) moves.add(t);
        }
    }

    protected void checkRDiagonal() {
        int x = tile.getX(), y = tile.getY(), x1, y1;
        Tile t;
        for (int i = -1; i < 2; i+=2) {
            x1 = x + i;
            y1 = y - i;
            t = GameController.getTile(x1, y1);
            while (t != null && t.getFigure() == null) {
                if (king.isLegal(tile, t)) moves.add(t);
                x1 += i;
                y1 -= i;
                t = GameController.getTile(x1, y1);
            }
            if (t != null && t.getFigure().getColor() != color && king.isLegal(tile, t)) moves.add(t);
        }
    }

    protected void checkLDiagonal() {
        int x = tile.getX(), y = tile.getY(), x1, y1;
        Tile t;
        for (int i = -1; i < 2; i+=2) {
            x1 = x + i;
            y1 = y + i;
            t = GameController.getTile(x1, y1);
            while (t != null && t.getFigure() == null) {
                if (king.isLegal(tile, t)) moves.add(t);
                x1 += i;
                y1 += i;
                t = GameController.getTile(x1, y1);
            }
            if (t != null && t.getFigure().getColor() != color && king.isLegal(tile, t)) moves.add(t);
        }
    }

    protected void checkAround(int[][] pairs) {
        int x = tile.getX(), y = tile.getY(), x1, y1;
        Tile t;
        for (int[] pair : pairs) {
            x1 = x + pair[0];
            y1 = y + pair[1];
            t = GameController.getTile(x1, y1);
            if (t == null || (t.getFigure() != null && t.getFigure().getColor() == color)) continue;
            if (king.isLegal(tile, t)) moves.add(t);
        }
    }

}
