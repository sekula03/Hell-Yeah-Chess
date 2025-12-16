package com.sekula03.net;

import com.sekula03.ChessGame;

import java.io.IOException;
import java.net.Socket;

public class Client extends Thread {

    private Service main, online;
    private boolean terminated = false;
    private String game_mode;

    public static final String EXIT_CODE = "_EXIT", RESET_CODE = "_RESET", FORFEIT_CODE = "_FORFEIT";

    @Override
    public void run() {
        try {
            Socket socket = new Socket("localhost", 5555);
            main = new Service(socket);

            onlineThread();

            String msg;
            boolean reset = true;
            while (reset) {
                msg = main.receiveMsg();
                if (msg.equals(EXIT_CODE)) {
                    main.close();
                    return;
                }
                reset = msg.equals(RESET_CODE);
                ChessGame.loginOutcome(!reset);
            }

            while (true) {

                synchronized (this) { wait(); }

                if (terminated) break;

                main.sendMsg(game_mode);

                msg = main.receiveMsg();
                if (msg.equals(EXIT_CODE)) break;
                if (msg.equals(RESET_CODE)) continue;

                String[] parts = msg.split("#");
                ChessGame.startGame(Integer.parseInt(parts[0]), parts[1]);

                while (true) {
                    msg = main.receiveMsg();
                    if (msg.equals(EXIT_CODE)) {
                        main.close();
                        return;
                    }
                    else if (msg.equals(FORFEIT_CODE)) {
                        ChessGame.endGame(ChessGame.END_STATE.VICTORY);
                        continue;
                    }
                    else if (msg.equals(RESET_CODE)) break;
                    ChessGame.receiveMove(msg);
                }
            }
            main.close();
        } catch (Exception e) { System.out.println("Client: " + e.getMessage()); }
    }

    public void sendMsg(String msg) {
        main.sendMsg(msg);
    }

    public void close() {
        main.sendMsg(EXIT_CODE);
        online.sendMsg(EXIT_CODE);
        terminated = true;
        synchronized (this) { notifyAll(); }
    }

    public void cancelWaiting() {
        online.sendMsg(RESET_CODE);
    }

    public void setGameMode(String game_mode) {
        this.game_mode = game_mode;
    }

    private void onlineThread() throws IOException {
        int port = Integer.parseInt(main.receiveMsg());
        Socket socket = new Socket("localhost", port);
        online = new Service(socket);

        new Thread(() -> {
            try {
                while (true) {
                    String msg = online.receiveMsg();
                    if (msg.equals(EXIT_CODE)) break;
                    ChessGame.updateOnlineCount(Integer.parseInt(msg));
                }
                online.close();
            } catch (Exception e) { System.out.println("Online thread: " + e.getMessage()); }
        }).start();
    }

}
