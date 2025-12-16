package com.sekula03.figures;

import com.sekula03.ChessGame;
import com.sekula03.GameController;
import com.sekula03.Tile;
import javafx.scene.image.Image;

public class Pawn extends Figure {

    private boolean en_passant = false;
    private final boolean direction;

    public Pawn(Tile tile, COLOR color, King king) {
        super(tile, color, king,
                new Image(ChessGame.class.getResourceAsStream(String.format("/chess/%sP.png", color == COLOR.WHITE ? "w" : "b"))));
        direction = tile.getX() == 6;
    }

    @Override
    public void loadMoves() {
        moves.clear();
        en_passant = false;

        int dir = direction ? -1 : 1, ep = direction ? 3 : 4;

        int x = tile.getX(), y = tile.getY(), y1;
        Tile t;
        Figure f;

        t = GameController.getTile(x + dir, y);
        boolean empty = t != null && t.getFigure() == null;
        if (empty && king.isLegal(tile, t)) moves.add(t);


        t = GameController.getTile(x + (dir << 1), y);
        if (empty && t != null && t.getFigure() == null && !has_moved && king.isLegal(tile, t)) moves.add(t);

        for (int i = -1; i < 2; i+=2) {
            y1 = y + i;

            t = GameController.getTile(x + dir, y1);
            if (t == null) continue;
            f = t.getFigure();
            if (f != null && f.getColor() != color && king.isLegal(tile, t)) moves.add(t);

            if (x != ep || f != null) continue;
            Tile t1 = GameController.getTile(ep, y1);
            if (t1 == null) continue;
            f = t1.getFigure();
            if (f instanceof Pawn && ((Pawn)f).en_passant) {
                t1.setFigure(null);
                if (king.isLegal(tile, t)) moves.add(t);
                t1.setFigure(f);
            }
        }
    }

    @Override
    public void move(Tile new_tile) {
        if (Math.abs(tile.getX() - new_tile.getX()) == 2) en_passant = true;
        else if (tile.getY() != new_tile.getY() && new_tile.getFigure() == null) {
            Tile t = GameController.getTile(tile.getX(), new_tile.getY());
            if (t != null) t.setFigure(null);
        }
        super.move(new_tile);
        if (tile.getX() == 0) GameController.startPromotion(tile);
    }

}
