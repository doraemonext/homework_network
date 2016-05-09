package com.doraemonext.webserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;

class Communication implements Runnable {

    Communication(Socket _client) {
        this.client = _client;
    }

    public void run() {
        try {
            String clientIP = this.client.getInetAddress().toString();
            int clientPort = this.client.getPort();
            PrintStream out = new PrintStream(client.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            String msg = in.readLine();

            String fileName = getResourcePath(msg);
            ClassLoader classLoader = getClass().getClassLoader();
            URL url = getClass().getClassLoader().getResource(fileName);
            if (url == null) {
                System.out.println("[ERROR] Get resource " + fileName + " from " + clientIP + ":" + clientPort + ", but not found");
                out.println("HTTP/1.1 404 Not Found");
                out.println("Content-Type:text/plain");
                out.println("Content-Length:9");
                out.println("");
                out.println("Not Found");
            } else {
                File file = new File(url.getFile());
                System.out.println("[INFO] Get resource " + fileName + " from " + clientIP + ":" + clientPort);
                out.println("HTTP/1.1 200 OK");
                out.println("Content-Type:text/html");
                int len = (int) file.length();
                out.println("Content-Length:" + len);
                out.println("");
                sendFile(out, file);
            }
            out.flush();
            client.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private String getResourcePath(String s) {
        String str = s.substring(s.indexOf(' ') + 1);
        str = str.substring(1, str.indexOf(' '));

        if (str.equals("")) {
            str = "index.html";
        }
        return str;
    }

    private void sendFile(PrintStream out, File file) {
        try {
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            int len = (int) file.length();
            byte[] buf = new byte[len];
            in.readFully(buf);
            out.write(buf, 0, len);
            out.flush();
            in.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private Socket client;
}
