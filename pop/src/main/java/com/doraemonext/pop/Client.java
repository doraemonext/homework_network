package com.doraemonext.pop;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.SocketFactory;

public class Client {

    public void connect() throws IOException {
        connect(Constants.HOST, Constants.PORT);
    }

    public void connect(String host, int port) throws IOException {
        try {
            SocketFactory socketFactory = SocketFactory.getDefault();
            clientSocket = socketFactory.createSocket(host, port);
            in = clientSocket.getInputStream();
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: " + host);
            System.exit(1);
        }

        System.out.println("Connected to " + host + " at port " + port);
        System.out.println(readResponse());
    }

    public boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected();
    }

    public void disconnect() throws IOException {
        if (!isConnected())
            throw new IllegalStateException("Not connected to a host");

        clientSocket.close();
        in.close();
        out.close();
        System.out.println("Disconnected from the host");
    }

    public String readResponse() throws IOException {
        String res = readInputStream();

        if (res.equals("")) {
            return res;
        }
        if (res.startsWith("-ERR")) {
            throw new RuntimeException("Server has returned an error: " + res.replaceFirst("-ERR ", ""));
        }

        return res;
    }

    public void sendCommand(String command) throws IOException {
        out.write(command + "\r\n");
        out.flush();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(readResponse());
    }

    private String readInputStream() throws IOException {
        String result = "";
        byte[] container = new byte[128];
        while (in.available() > 0) {
            int i = in.read(container, 0, 128);
            if (i < 0) {
                break;
            }
            result += new String(container, 0, i);
        }
        return result;
    }

    private Socket clientSocket = null;
    private InputStream in = null;
    private BufferedWriter out = null;
}