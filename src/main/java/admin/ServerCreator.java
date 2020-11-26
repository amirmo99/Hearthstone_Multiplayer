package admin;

import network.server.MainServer;

import java.io.IOException;

public class ServerCreator {

    public static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            new MainServer(PORT).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
