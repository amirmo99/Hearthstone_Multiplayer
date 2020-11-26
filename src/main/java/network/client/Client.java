package network.client;

import UI.GUI.MainFrame;
import UI.GameMapper;
import UI.PlayerMapper;
import Util.BundleRankTableModels;
import Util.ChatList;
import Util.Monitor;
import abstracts.MessageReceiver;
import logic.MyGameState;
import logic.Player;
import models.Card;
import models.InfoPassive;
import network.api.Message;
import network.api.MessageType;
import network.server.GameServer;
import network.util.Receiver;
import network.util.Sender;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client extends Thread implements MessageReceiver {

    private Socket socket;
    private Receiver receiver;
    private Sender sender;

    private MainFrame mainFrame;
    private GameMapper gameMapper;
    private PlayerMapper playerMapper;

    private int authToken;
    private Monitor monitor;

    public Client() {
        authToken = 0;
        gameMapper = new GameMapper(this);
        playerMapper = new PlayerMapper(this);
        monitor = new Monitor();
    }

    public boolean connect(int port, String ip) {
        try {
            socket = new Socket(ip, port);
            System.out.println("Socket connected: " + socket.toString());
            initSocket();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void initSocket() throws IOException {
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        this.receiver = new Receiver(inputStream, this);
        this.sender = new Sender(outputStream);

        receiver.start();
    }

    @Override
    public void run() {
        Client self = this;

        new Thread(() -> SwingUtilities.invokeLater(() -> mainFrame = new MainFrame(self))).start();
    }

    @Override
    public void receive(Message message) {
        if (message == null) {
            System.out.println("Message is null. reconnecting...");
            mainFrame.connectToServer();
        }
        else {
            switch (message.getType()) {
                case SHOW_MESSAGE:
                    mainFrame.showError((String) message.getData());
                    break;
                case PLAYER_UPDATE:
                    updatePlayer((Player) message.getData());
                    break;
                case SHOW_GO_SHOP_OPTION:
                    mainFrame.showGoToShopOption();
                    break;
                case AUTH_TOKEN:
                    authToken = message.getToken();
                    break;
                case GO_TO_LOGIN_PANEL:
                    if (mainFrame != null) mainFrame.goToLoginPanel();
                    break;
                case SUCCESSFUL_LOGIN:
                    new Thread(() -> mainFrame.goToHomePanel()).start();
                    break;
                case PLAY_ACCESS:
                    makeGame((int) message.getData(), (boolean) message.getData2());
                    break;
                case UPDATE_GAME_PANEL:
                    MyGameState gameState = (MyGameState) message.getData();
                    gameMapper.setGameState(gameState);
                    if (mainFrame.getGameBoardPanel() != null) {
                        if (!mainFrame.getGameBoardPanel().isStarted()) {
                            mainFrame.getGameBoardPanel().setGameState(gameState);
                            mainFrame.getGameBoardPanel().start();
                        }
                        mainFrame.getGameBoardPanel().update();
                    }
                    break;
                case CHOOSE_CARD:
                    List<Card> cards = (List<Card>) message.getData();
                    Card card = gameMapper.discover(cards);
                    sendMessage(new Message(MessageType.CARD_CHOSE, card));
                    break;
                case CHOOSE_PASSIVE:
                    List<InfoPassive> passives = (List<InfoPassive>) message.getData();
                    InfoPassive passive = gameMapper.askForPassive(passives);
                    sendMessage(new Message(MessageType.PASSIVE_CHOSE, passive));
                    break;
                case CHOOSE_CARDS:
                    List<Card> cards1 = (List<Card>) message.getData();
                    String title = (String) message.getData2();

                    ArrayList<Card> cards2 = gameMapper.askForMultipleCards(cards1, title);
                    sendMessage(new Message(MessageType.CARDS_CHOSE, cards2));
                    break;
                case RANKS_TABLE_UPDATE:
                    mainFrame.updateRankPanel((BundleRankTableModels) message.getData());
                    break;
                case UPDATE_COUNTER:
                    gameMapper.timerTick();
                    mainFrame.getGameBoardPanel().update();
                    break;
                case UPDATE_CHATS:
                    ChatList chatList = (ChatList) message.getData();
                    mainFrame.getGameBoardPanel().updateChats(chatList);
                    break;
            }
        }
    }

    private void makeGame(int mode, boolean isPlayerOne) {
        mainFrame.makeGamePanel(mode == GameServer.SINGLE_PLAYER_LOCAL, isPlayerOne);
    }

    public void sendMessage(Message message) {
        message.setToken(authToken);
        sender.send(message);
    }

    public void requestPlayerUpdate() {
        sendMessage(new Message(MessageType.PLAYER_UPDATE_REQUEST));
        System.out.println("Waiting for update");
        monitor.doWait(this);
        System.out.println("Waiting is over");
    }

    private void updatePlayer(Player player) {
        playerMapper.setPlayer(player);
        gameMapper.setPlayer(player);

        System.out.println("Notifying");
        monitor.doNotify(this);
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public GameMapper getGameMapper() {
        return gameMapper;
    }

    public PlayerMapper getPlayerMapper() {
        return playerMapper;
    }
}
