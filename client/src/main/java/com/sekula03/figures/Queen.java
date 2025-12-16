package com.sekula03.figures;

import com.sekula03.ChessGame;
import com.sekula03.Tile;
import javafx.scene.image.Image;

public class Queen extends Figure {

    public Queen(Tile tile, COLOR color, King king) {
        super(tile, color, king,
                new Image(ChessGame.class.getResourceAsStream(String.format("/chess/%sQ.png", color == COLOR.WHITE ? "w" : "b"))));
    }

    @Override
    public void loadMoves() {
        moves.clear();
        checkFile();
        checkRank();
        checkLDiagonal();
        checkRDiagonal();
    }

}
