package network.util;

import Util.Monitor;
import network.api.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class Sender {

    private ObjectOutputStream outputStream;
    private boolean isConnected;
    private boolean isBeingUsed = false;
    private volatile Monitor monitor;

    public Sender(OutputStream outputStream) {
        try {
            this.outputStream = new ObjectOutputStream(outputStream);
            isConnected = true;
            monitor = new Monitor();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Message message) {
        synchronized (outputStream) {
            try {
                if (isConnected) {
                    outputStream.reset();
                    System.out.println("Sent: " + message.toString());
                    outputStream.writeObject(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                isConnected = false;
            }
        }
//        send2(message);
    }

    public void send2(Message message) {
        try {
            if (isConnected) {
                while (isBeingUsed) monitor.doWait();
                isBeingUsed = true;
                outputStream.reset();
                outputStream.writeObject(message);
                isBeingUsed = false;
                monitor.doNotify();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setConnected(boolean connected) {
//        this.isConnected = connected;
    }
}
