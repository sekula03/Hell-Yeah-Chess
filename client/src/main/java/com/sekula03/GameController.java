package com.sekula03;

import com.sekula03.figures.*;

import static com.sekula03.ChessGame.END_STATE;
import static com.sekula03.figures.Figure.COLOR;

public class GameController {

    public static final int N = 8;
    private static final Tile[][] table_matrix = new Tile[N][N];
    private static Tile selected_tile, next_tile, promotion_tile;
    private static COLOR player_color = null;
    private static King my_king, opponent_king;
    private static boolean my_turn, send = true;
    private static final Tile[] decoded = new Tile[2];
    private static Character promoted = null;
    private static final StringBuilder encoder = new StringBuilder();

    public static void generateTable(int player) {
        COLOR color = player == 0 ? COLOR.WHITE : COLOR.BLACK;
        player_color = color;

        selected_tile = null;
        next_tile = null;
        promotion_tile = null;
        decoded[0] = null;
        decoded[1] = null;
        promoted = null;
        encoder.setLength(0);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                table_matrix[i][j] = new Tile(i, j);
            }
        }

        my_king = new King(table_matrix[7][4 - player], color);
        new Rook(table_matrix[7][0], color, my_king);
        new Knight(table_matrix[7][1], color, my_king);
        new Bishop(table_matrix[7][2], color, my_king);
        new Queen(table_matrix[7][3 + player], color, my_king);
        new Bishop(table_matrix[7][5], color, my_king);
        new Knight(table_matrix[7][6], color, my_king);
        new Rook(table_matrix[7][7], color, my_king);
        for (int j = 0; j < N; j++) new Pawn(table_matrix[6][j], color, my_king);

        color = color.opposite();

        opponent_king = new King(table_matrix[0][4 - player], color);
        new Rook(table_matrix[0][0], color, opponent_king);
        new Knight(table_matrix[0][1], color, opponent_king);
        new Bishop(table_matrix[0][2], color, opponent_king);
        new Queen(table_matrix[0][3 + player], color, opponent_king);
        new Bishop(table_matrix[0][5], color, opponent_king);
        new Knight(table_matrix[0][6], color, opponent_king);
        new Rook(table_matrix[0][7], color, opponent_king);
        for (int j = 0; j < N; j++) new Pawn(table_matrix[1][j], color, opponent_king);

        moveThread();

        my_turn = player_color == COLOR.WHITE;
        if (my_turn) newTurn();
    }

    public static Tile getTile(int x, int y) {
        return (x < 0 || x >= N || y < 0 || y >= N) ? null : table_matrix[x][y];
    }

    public static void click(Tile tile) {
        if (!my_turn) return;

        Figure end_figure = tile.getFigure(), start_figure;

        if (selected_tile == null) {
            if (end_figure != null && end_figure.getColor() == player_color) {
                tile.highlight(true);
                for (Tile t: end_figure.getMoves()) t.highlight(false);
                selected_tile = tile;
            }
            return;
        }

        start_figure = selected_tile.getFigure();
        selected_tile.highlight(true);
        for (Tile t: start_figure.getMoves()) t.highlight(false);

        if (!start_figure.getMoves().contains(tile)) {
            if (tile != selected_tile && end_figure != null && end_figure.getColor() == player_color) {
                tile.highlight(true);
                selected_tile = tile;
                for (Tile t: end_figure.getMoves()) t.highlight(false);
            }
            else selected_tile = null;
            return;
        }

        start_figure.move(tile);
        next_tile = tile;
        synchronized (encoder) { encoder.notifyAll(); }
    }

    public static void opponentMove(String move) {
        decodeMove(move);

        Figure f = decoded[0].getFigure();
        f.move(decoded[1]);

        if (promoted != null) {
            decoded[1].setFigure(promote(promoted, opponent_king, decoded[1]));
            promoted = null;
        }

        my_turn = true;
        newTurn();
    }

    public static void startPromotion(Tile t) {
        promotion_tile = t;
        ChessGame.startPromotion();
        send = false;
    }

    public static void endPromotion(char c) {
        promotion_tile.setFigure(promote(c, my_king, promotion_tile));
        promoted = c;
        send = true;
        synchronized (encoder) { encoder.notifyAll(); }
    }

    public static void stopSending() {
        my_king = null;
        synchronized (encoder) { encoder.notifyAll(); }
    }

    private static void newTurn() {
        COLOR color = my_turn ? player_color : player_color.opposite();
        boolean no_moves = true, draw = true;
        int knights = 0, bishops = 0;
        Bishop[] bishop_figures = {null, null};
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Figure f = table_matrix[i][j].getFigure();
                if (f != null) {
                    if (f.getColor() == color) {
                        f.loadMoves();
                        if (!f.getMoves().isEmpty()) no_moves = false;
                    }
                    if (f instanceof Pawn || f instanceof Queen || f instanceof Rook) draw = false;
                    else if (f instanceof Bishop) {
                        if (f.getColor() == player_color) bishop_figures[0] = (Bishop) f;
                        else bishop_figures[1] = (Bishop) f;
                        bishops++;
                    }
                    else if (f instanceof Knight) knights++;
                }
            }
        }
        if ((bishops + knights > 1) && (knights != 0 || bishops != 2 || !same_colored(bishop_figures))) draw = false;
        if (no_moves || draw) {
            King king = my_turn ? my_king : opponent_king;
            END_STATE state = draw || !king.inCheck() ? END_STATE.DRAW : (my_turn ? END_STATE.DEFEAT : END_STATE.VICTORY);
            ChessGame.endGame(state);
            my_turn = false;
        }
    }

    private static void encodeMove(Tile selectedTile, Tile tile) {
        encoder.setLength(0);
        int x1 = selectedTile.getX(), y1 = selectedTile.getY(), x2 = tile.getX(), y2 = tile.getY();
        if (player_color == COLOR.WHITE)
            encoder.append((char)('a' + y1)).append(N - x1).append((char)('a' + y2)).append(N - x2);
        else
            encoder.append((char)('h' - y1)).append(1 + x1).append((char)('h' - y2)).append(1 + x2);
        if (promoted != null) {
            encoder.append(promoted);
            promoted = null;
        }
    }

    private static void decodeMove(String move) {
        int row, col;
        for (int i = 0; i < 2; i++) {
            char file = move.charAt(i << 1), rank = move.charAt((i << 1) + 1);
            if (player_color == COLOR.WHITE) {
                col = file - 'a';
                row = N - (rank - '0');
            } else {
                col = N - 1 - (file - 'a');
                row = rank - '1';
            }
            decoded[i] = table_matrix[row][col];
        }
        if (move.length() == 5) promoted = move.charAt(4);
    }

    private static boolean same_colored(Bishop[] b) {
        return b[0] != null && b[1] != null && b[0].getSquareColor() == b[1].getSquareColor();
    }

    private static Figure promote(char c, King king, Tile tile) {
        return switch (c) {
            case 'q' -> new Queen(tile, king.getColor(), king);
            case 'r' -> new Rook(tile, king.getColor(), king);
            case 'b' -> new Bishop(tile, king.getColor(), king);
            case 'n' -> new Knight(tile, king.getColor(), king);
            default -> null;
        };
    }

    private static void moveThread() {
        new Thread(() -> {
            try {
                while (true) {
                    do {
                        synchronized (encoder) { encoder.wait(); }
                        if (my_king == null) return;
                    } while (!send);
                    encodeMove(selected_tile, next_tile);
                    ChessGame.sendMove(encoder.toString());
                    selected_tile = null;
                    my_turn = false;
                    newTurn();
                }
            } catch (InterruptedException _) {}
        }).start();
    }

}
