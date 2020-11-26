package UI.GUI.MainPanels.MainGame;

import Util.ChatList;
import configs.GameGraphicConstants;
import network.api.Message;
import network.api.MessageType;
import network.client.Client;

import javax.swing.*;
import java.awt.*;

public class ChatRoom extends JFrame {

    private ChatList chatList;
    private Client client;

    private JTextField messageField;
    private JTextArea messageList;
    private JButton send;

    private GameGraphicConstants constants;

    public ChatRoom(Client client) {
        super("Chat Room");
        this.client = client;
        constants = new GameGraphicConstants();
        init();
        initComponents();
        placeComponents();
    }

    private void initComponents() {
        chatList = new ChatList();
        messageField = new JTextField();
        messageList = new JTextArea();
        send = new JButton("Send");

        send.addActionListener(e -> {
            String content = messageField.getText();
            client.sendMessage(new Message(MessageType.CHAT_MESSAGE, content));
            messageField.setText("");
        });
    }

    private void placeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;

        // Row 1
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridx = 0;
        gc.gridy = 0;
        gc.gridwidth = 2;
        add(messageList, gc);
        // Row 2
        gc.weighty = 0.06;
        gc.gridy = 1;
        gc.gridwidth = 1;
        // Row 2 Column 1
        gc.weightx = 3;
        gc.gridx = 0;
        add(messageField, gc);
        // Row 2 Column 2
        gc.weightx = 1;
        gc.gridx = 1;
        add(send, gc);
    }

    private void init() {
        setSize((int) constants.getGameWidth() / 3, (int) constants.getGameHeight() / 2);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(false);
    }


    public void updateChats(ChatList chatList) {
        this.chatList = chatList;
        applyChanges();
    }

    private void applyChanges() {
        messageList.setText(chatList.toString());
    }


}
