package com.sekula03.Server;

import com.sekula03.service.Service;
import com.sekula03.service.SocketService;
import com.sekula03.service.Stockfish;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Server {

    private final HashSet<Service> online_services = new HashSet<>();
    private final HashSet<String> names = new HashSet<>();
    private final HashMap<RequestHandler, Stockfish> bots = new HashMap<>();
    private RequestHandler waiting = null;
    private final Random random = new Random();

    public static void main(String[] args) {
        new Server().run();
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(5555)) {
            while (true) {
                Socket socket = serverSocket.accept();
                Service service = new SocketService(socket);

                ServerSocket serverSocket1 = new ServerSocket(0);
                int port = serverSocket1.getLocalPort();

                service.sendMsg(Integer.toString(port));

                Socket socket1 = serverSocket1.accept();
                Service service1 = new SocketService(socket1);

                serverSocket1.close();

                new RequestHandler(service, service1, this).start();
            }
        } catch (Exception e) { System.out.println("Server: " + e.getMessage()); }
    }

    public synchronized boolean login(RequestHandler rh) {
        String name = rh.getUsername();
        if (names.contains(name)) return false;
        names.add(name);
        online_services.add(rh.getOnline());
        update();
        return true;
    }

    public synchronized void logoff(RequestHandler rh) {
        names.remove(rh.getUsername());
        online_services.remove(rh.getOnline());
        update();
        if (bots.containsKey(rh)) {
            bots.get(rh).close();
            bots.remove(rh);
        }
    }

    public synchronized void getPair(RequestHandler rh) throws InterruptedException {
        if (waiting == null) {
            waiting = rh;
            wait();
        }
        else {
            int color = random.nextInt(2);
            waiting.setOpponent(rh.getMain(), color, rh.getUsername());
            rh.setOpponent(waiting.getMain(), 1 - color, waiting.getUsername());

            notifyAll();
            waiting = null;
        }
    }

    public synchronized void cancelPairingUp(RequestHandler rh) {
        if (waiting == rh) {
            notifyAll();
            waiting = null;
        }
    }

    public void getBot(RequestHandler rh, int lvl) {
        int color = random.nextInt(2);
        Stockfish stockfish = bots.get(rh);
        if (stockfish == null) {
            try {
                stockfish = new Stockfish(rh.getMain());
            } catch (Exception _) {
                rh.reset();
                return;
            }
            bots.put(rh, stockfish);
        }
        stockfish.setAttributes(lvl, 1 - color);
        rh.setOpponent(stockfish, color, stockfish.getUsername());
    }

    public void startBot(RequestHandler rh) {
        bots.get(rh).newGame();
    }

    private void update() {
        for (Service s : online_services) s.sendMsg(Integer.toString(online_services.size()));
    }
}
