package com.sekula03.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Service {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    public Service(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMsg(String msg) {
        out.println(msg);
    }

    public String receiveMsg() throws IOException {
        return in.readLine();
    }

    public void close() {
        try {
            socket.close();
            in.close();
            out.close();
        }
        catch (Exception e) { System.out.println("Service:\n" + e.getMessage()); }
    }
}
