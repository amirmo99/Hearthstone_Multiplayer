package Util;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;

public class ChatList implements Serializable {

    private ArrayList<String> chats;

    public ChatList() {
        chats = new ArrayList<>();
    }

    public void addMessage(String senderName, String message) {
        String chat = LocalTime.now() + "\n" + senderName + ": \n\t" + message;
        chats.add(chat);
    }

    @Override
    public String toString() {
        StringBuilder chatToString = new StringBuilder("********* Chat Room ***********\n");
        for (String chat : chats) {
            chatToString.append(chat).append("\n");
        }

        return chatToString.toString();
    }
}
