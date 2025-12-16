package com.sekula03.figures;

import com.sekula03.ChessGame;
import com.sekula03.Tile;
import javafx.scene.image.Image;

public class Knight extends Figure {

    static final int[][] pairs = {{1,2}, {1,-2}, {-1,2}, {-1,-2}, {2,1}, {2,-1}, {-2,1}, {-2,-1}};

    public Knight(Tile tile, COLOR color, King king) {
        super(tile, color, king,
                new Image(ChessGame.class.getResourceAsStream(String.format("/chess/%sN.png", color == COLOR.WHITE ? "w" : "b"))));
    }

    @Override
    public void loadMoves() {
        moves.clear();
        checkAround(pairs);
    }

}
