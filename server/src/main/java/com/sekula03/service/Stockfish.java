package com.sekula03.service;

import java.io.IOException;

public class Stockfish extends Service{

    private enum COLOR { WHITE, BLACK }

    private static final String[][]  levels = new String[5][];
    private final Process stockfishProcess;
    private final Service service;
    private final StringBuilder game_history = new StringBuilder();
    private int move_time, wait_time;
    private COLOR color;
    private String name;

    static {
        levels[0] = new String[]{"0", "800", "10", "Triceratops"};
        levels[1] = new String[]{"5", "1200", "100", "Tyrannosaurus Rex"};
        levels[2] = new String[]{"10", "1600", "500", "Ankylosaurus"};
        levels[3] = new String[]{"15", "2000", "1000", "Brachiosaurus"};
        levels[4] = new String[]{"20", "2500", "2000", "Spinosaurus"};
    }

    public Stockfish(Service service) throws IOException {
        stockfishProcess = new ProcessBuilder("./stockfish-windows-x86-64-avx2.exe").start();
        setStreams(stockfishProcess.getInputStream(), stockfishProcess.getOutputStream());
        this.service = service;
        super.sendMsg("uci");
    }

    public void setAttributes(int level, int color) {
        super.sendMsg("setoption name Skill Level value " + levels[level][0]);
        super.sendMsg("setoption name UCI_Elo value " + levels[level][1]);
        move_time = Integer.parseInt(levels[level][2]);
        wait_time = Math.max(move_time, 1000);
        name = levels[level][3];
        this.color = color == 0 ? COLOR.WHITE : COLOR.BLACK;
    }

    public void newGame() {
        super.sendMsg("ucinewgame");
        game_history.setLength(0);
        game_history.append("position startpos");
        String response = "";
        if (color == COLOR.WHITE) response = " " + getMove();
        game_history.append(" moves").append(response);
    }

    @Override
    public void sendMsg(String move) {
        game_history.append(" ").append(move);
        String response = getMove();
        game_history.append(" ").append(response);
    }

    @Override
    public String receiveMsg() throws IOException {
        String line;
        String bestMove = null;
        while ((line = super.receiveMsg()) != null) {
            if (line.startsWith("bestmove")) {
                bestMove = line.split(" ")[1];
                break;
            }
        }
        return bestMove;
    }

    @Override
    public void close() {
        sendMsg("quit");
        stockfishProcess.destroy();
        super.close();
    }

    public String getUsername() {
        return name;
    }

    private String getMove() {
        String response = null;
        super.sendMsg(game_history.toString());
        super.sendMsg(String.format("go movetime %d", move_time));
        try {
            Thread.sleep(wait_time);
            response = receiveMsg();
            service.sendMsg(response);
        } catch (Exception e) { System.out.println("Stockfish: " + e.getMessage()); }
        return response;
    }

}
