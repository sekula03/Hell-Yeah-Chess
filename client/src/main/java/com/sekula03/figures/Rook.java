package com.sekula03.figures;

import com.sekula03.ChessGame;
import com.sekula03.Tile;
import javafx.scene.image.Image;

public class Rook extends Figure {

    public Rook(Tile tile, COLOR color, King king) {
        super(tile, color, king,
                new Image(ChessGame.class.getResourceAsStream(String.format("/chess/%sR.png", color == COLOR.WHITE ? "w" : "b"))));
    }

    @Override
    public void loadMoves() {
        moves.clear();
        checkFile();
        checkRank();
    }

}
