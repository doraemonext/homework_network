package com.doraemonext.pop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.connect();

        client.sendCommand("USER test_doraemonext@126.com");
        client.sendCommand("PASS doraemonext123");

        startRepl(client);

        client.disconnect();
    }

    private static void startRepl(Client client) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Has logged in, ready for more commands: \n");
        System.out.print("> ");

        String command = null;
        while (!(command = in.readLine()).equals(":q"))
        {
            client.sendCommand(command);
            System.out.print("> ");
        }
    }
}
