package com.sekula03.figures;

import com.sekula03.ChessGame;
import com.sekula03.GameController;
import com.sekula03.Tile;
import javafx.scene.image.Image;

import static com.sekula03.GameController.N;

public class King extends Figure {

    private static final int[][] pairs = {{0,1}, {0,-1}, {1,0}, {1,1}, {1,-1}, {-1,0}, {-1,1}, {-1,-1}};
    private final boolean direction;

    public King(Tile tile, COLOR color) {
        super(tile, color, null,
                new Image(ChessGame.class.getResourceAsStream(String.format("/chess/%sK.png", color == COLOR.WHITE ? "w" : "b"))));
        this.king = this;
        this.direction = tile.getX() == N - 1;
    }

    @Override
    public void loadMoves() {
        moves.clear();
        checkAround(pairs);
        if (has_moved || inCheck()) return;

        boolean valid = true;
        int x = tile.getX();
        int y = tile.getY();

        Tile t = GameController.getTile(x, 0);
        if (t != null) {
            Figure f = t.getFigure();
            if (f instanceof Rook && f.getColor() == color && !f.has_moved) {
                for (int j = y - 1; j > 0; j--) {
                    t = GameController.getTile(x, j);
                    if (t == null || t.getFigure() != null || !isLegal(tile, t)) {
                        valid = false;
                        break;
                    }
                }
                if (valid) moves.add(t);
            }
        }

        valid = true;
        t = GameController.getTile(x, N - 1);
        if (t != null) {
            Figure f = t.getFigure();
            if (f instanceof Rook && f.getColor() == color && !f.has_moved) {
                for (int j = y + 1; j < N - 1; j++) {
                    t = GameController.getTile(x, j);
                    if (t == null || t.getFigure() != null || !isLegal(tile, t)) {
                        valid = false;
                        break;
                    }
                }
                if (valid) moves.add(t);
            }
        }
    }

    @Override
    public void move(Tile new_tile) {
        if (Math.abs(tile.getY() - new_tile.getY()) > 1) {
            Tile t1, t2;
            if (tile.getY() > new_tile.getY()) {
                t1 =  GameController.getTile(tile.getX(), 0);
                t2 =  GameController.getTile(tile.getX(), tile.getY() - 1);
            }
            else {
                t1 =  GameController.getTile(tile.getX(), N - 1);
                t2 =  GameController.getTile(tile.getX(), tile.getY() + 1);
            }
            if (t1 != null && t2 != null) t1.getFigure().move(t2);
        }
        super.move(new_tile);
    }

    public boolean inCheck() {
        return !checkAllDirections();
    }

    boolean isLegal(Tile start_tile, Tile end_tile) {
        Figure start_figure = start_tile.getFigure(), end_figure = end_tile.getFigure();

        start_tile.setFigure(null);
        end_tile.setFigure(start_figure);
        start_figure.tile = end_tile;

        boolean result = checkAllDirections();

        start_tile.setFigure(start_figure);
        end_tile.setFigure(end_figure);
        start_figure.tile = start_tile;

        return result;
    }

    boolean checkAllDirections() {
        int x = tile.getX(), y = tile.getY();
        Tile t;
        Figure f;

        for (int[] pair : pairs) {
            int i = x + pair[0], j = y + pair[1];
            t = GameController.getTile(i, j);
            if (t != null && t.getFigure() instanceof King) return false;

            while (t != null && t.getFigure() == null) {
                i += pair[0];
                j += pair[1];
                t = GameController.getTile(i, j);
            }
            boolean p = (pair[0] & pair[1]) == 0;
            if (t != null) {
                f = t.getFigure();
                if (f.getColor() != color && (f instanceof Queen || (p && f instanceof Rook) || (!p && f instanceof Bishop))) return false;
            }
        }

        int dir = direction ? -1 : 1;
        for (int i = -1; i < 2; i+=2) {
            t = GameController.getTile(x + dir, y + i);
            if (t != null && t.getFigure() instanceof Pawn && t.getFigure().getColor() != color) return false;
        }

        for (int[] pair : Knight.pairs) {
            t = GameController.getTile(x + pair[0], y + pair[1]);
            if (t != null && t.getFigure() instanceof Knight && t.getFigure().getColor() != color) return false;
        }

        return true;
    }

}
