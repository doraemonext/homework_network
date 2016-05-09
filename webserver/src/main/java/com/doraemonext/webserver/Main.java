package com.doraemonext.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        ServerSocket server = null;
        Socket client = null;

        try {
            server = new ServerSocket(Constants.PORT);
            System.out.println("Starting server on port " + server.getLocalPort());
            while (true) {
                client = server.accept();
                Thread thread = new Thread(new Communication(client));
                thread.start();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
