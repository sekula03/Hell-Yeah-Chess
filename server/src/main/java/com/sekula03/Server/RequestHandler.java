package com.sekula03.Server;

import com.sekula03.service.Service;

public class RequestHandler extends Thread {

    private final Service main, online;
    private Service opponent;
    private final Server server;
    private int color;
    private String name, opponent_name;
    private boolean reset = false;
    private static final int MULTIPLAYER = 0;
    private static final String EXIT_CODE = "_EXIT", RESET_CODE = "_RESET", FORFEIT_CODE = "_FORFEIT";

    public RequestHandler(Service service, Service service1, Server server) {
        main = service;
        online = service1;
        this.server = server;
    }

    @Override
    public void run() {
        connectionThread();

        try {
            String msg;
            while (true) {
                msg = main.receiveMsg();
                if (msg.equals(EXIT_CODE)) {
                    main.sendMsg(EXIT_CODE);
                    main.close();
                    return;
                }
                name = msg;
                if (server.login(this)) {
                    main.sendMsg(name);
                    break;
                }
                main.sendMsg(RESET_CODE);
            }

            while (true) {
                msg = main.receiveMsg();
                if (msg.equals(EXIT_CODE)) {
                    main.sendMsg(EXIT_CODE);
                    break;
                }
                int mode = Integer.parseInt(msg);

                if (mode == MULTIPLAYER) server.getPair(this);
                else server.getBot(this, mode-1);

                if (reset) {
                    reset = false;
                    main.sendMsg(RESET_CODE);
                    continue;
                }

                main.sendMsg(String.format("%d#%s", color, opponent_name));

                if (mode != MULTIPLAYER) server.startBot(this);

                while (true) {
                    msg = main.receiveMsg();
                    if (msg.equals(RESET_CODE)) {
                        main.sendMsg(RESET_CODE);
                        break;
                    }
                    if (msg.equals(FORFEIT_CODE) && mode != MULTIPLAYER) continue;
                    if (msg.equals(EXIT_CODE)) {
                        main.sendMsg(EXIT_CODE);
                        if (mode == MULTIPLAYER) opponent.sendMsg(FORFEIT_CODE);
                        server.logoff(this);
                        main.close();
                        return;
                    }
                    opponent.sendMsg(msg);
                }
            }
            server.logoff(this);
            main.close();
        } catch (Exception e) { System.out.println("Request handler: " + e.getMessage()); }
    }

    public void setOpponent(Service opp, int color, String name) {
        this.opponent = opp;
        this.color = color;
        this.opponent_name = name;
    }

    public Service getMain() {
        return main;
    }

    public Service getOnline() {
        return online;
    }

    public String getUsername() {
        return name;
    }

    public void reset() {
        reset = true;
    }

    private void connectionThread() {
        new Thread(() -> {
            try {
                while (true) {
                    String msg = online.receiveMsg();
                    if (msg.equals(RESET_CODE)) {
                        reset = true;
                        server.cancelPairingUp(this);
                    }
                    else if (msg.equals(EXIT_CODE)) {
                        online.sendMsg(EXIT_CODE);
                        break;
                    }
                }
                online.close();
            } catch (Exception e) { System.out.println("Connection thread: " + e.getMessage()); }
        }).start();
    }

}
