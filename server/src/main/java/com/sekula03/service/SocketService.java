package com.sekula03.service;

import java.io.IOException;
import java.net.Socket;

public class SocketService extends Service {

    private final Socket socket;

    public SocketService(Socket socket) throws IOException {
        setStreams(socket.getInputStream(), socket.getOutputStream());
        this.socket = socket;
    }

    @Override
    public void close() {
        try {
            socket.close();
            super.close();
        } catch (IOException e) { System.out.println("SocketService: " + e.getMessage()); }
    }

}
