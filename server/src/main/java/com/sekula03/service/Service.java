package com.sekula03.service;

import java.io.*;

public abstract class Service {

    private BufferedReader in;
    private PrintWriter out;

    protected void setStreams(InputStream is, OutputStream os) {
        in = new BufferedReader(new InputStreamReader(is));
        out = new PrintWriter(os, true);
    }

    public void sendMsg(String msg) {
        out.println(msg);
    }

    public String receiveMsg() throws IOException {
        return in.readLine();
    }

    public void close() {
        try {
            in.close();
            out.close();
        }
        catch (Exception e) { System.out.println("Service: " + e.getMessage()); }
    }

}
