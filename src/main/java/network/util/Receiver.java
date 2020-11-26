package network.util;

import abstracts.MessageReceiver;
import network.api.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class Receiver extends Thread {

    private InputStream inputStream;
    private ObjectInputStream objectInputStream;
    private MessageReceiver messageReceiver;

    private boolean alive = true;

    public Receiver(InputStream inputStream, MessageReceiver messageReceiver) {
        this.inputStream = inputStream;
        this.messageReceiver = messageReceiver;
    }

    @Override
    public void run() {
        try {
            objectInputStream = new ObjectInputStream(inputStream);
            while (!isInterrupted() && alive) {
                receiveMessage();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessage() {
        try {
            Object object = objectInputStream.readObject();
            if (!(object instanceof Message)) {
                System.out.println("Receiver got an input which not an instance of **Message**");
                return;
            }

//            new Thread(() -> messageReceiver.receive((Message) object));
            System.out.println("Received: " + object.toString());
            messageReceiver.receive((Message) object);

        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
            System.out.println(e.getMessage());
        } catch (IOException e) {
//            e.printStackTrace();
            System.out.println(e.getMessage());
            alive = false;
            messageReceiver.receive(null);
        }
    }

}
