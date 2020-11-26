package network.server;

import Util.*;
import configs.LogicConstants;
import configs.PathConfigs;
import enums.GameFieldType;
import logic.*;
import models.Card;
import models.Deck;
import models.GameModel;
import models.InfoPassive;
import network.api.Message;
import network.api.MessageType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameServer extends Thread {

    public static final int SINGLE_PLAYER_LOCAL = 0;
    public static final int AI_PLAYER = 1;
    public static final int MULTI_PLAYER = 2;
    public static final int FILE_GAME = 3;

    private ClientHandler client1, client2;
    private MainServer server;
    private Monitor monitor;
    private ChatList chatList;

    private int gameMode;
    private Player cpu;
    private MyGameState gameState;
    private GameActionExecutor executor;
    private File deckReaderFile;

    private GameModel chosenModelByPlayer;
    private ArrayList<Card> chosenCardsByPlayer;

    private MyTimer timer;
    private final int COUNT_DOWN;

    public GameServer(ClientHandler client1, ClientHandler client2, MainServer server, int gameMode) {
        this.client1 = client1;
        this.client2 = client2;
        this.server = server;
        this.gameMode = gameMode;

        chatList = new ChatList();

        this.deckReaderFile = new File(new PathConfigs().getDeckReaderFile());
        this.monitor = new Monitor();
        LogicConstants constants = new LogicConstants();
        COUNT_DOWN = constants.getPlayerLeftMatchCountDown();

        cpu = Administrator.getPlayer("cpu");
        SyncData.syncWithDataBase(cpu);
    }

    private void startTimer(ClientHandler clientHandler) {
        if (timer != null) timer.stopCounting();
        this.timer = new MyTimer(COUNT_DOWN, true);

        this.timer.setUsualRunnable(() -> clientLeaveMatch(clientHandler));

        timer.start();
    }

    public GameServer(MainServer server, ClientHandler client1, int gameMode) {
        this(client1, client1, server, gameMode);
    }

    public void gameEnded() {
        gameState.setGameOver(true);
        updateClientsState();

        server.gameEnded(client1);
        server.gameEnded(client2);
    }

    public synchronized void updateClientsState() {
        if (gameMode != SINGLE_PLAYER_LOCAL) {
            client1.sendMessage(new Message(MessageType.UPDATE_GAME_PANEL, gameState.secureCloned(1)));
            if (gameMode != AI_PLAYER)
                client2.sendMessage(new Message(MessageType.UPDATE_GAME_PANEL, gameState.secureCloned(2)));
        }
        else {
            int active = gameState.getActivePlayerIndex();
            client1.sendMessage(new Message(MessageType.UPDATE_GAME_PANEL, gameState.secureCloned(active)));
        }
        gameState.setRecentlyPlayedCard(false);
    }

    public void updateCounter() {
        client1.sendMessage(new Message(MessageType.UPDATE_COUNTER));
        if (gameMode == MULTI_PLAYER || gameMode == FILE_GAME) {
            client2.sendMessage(new Message(MessageType.UPDATE_COUNTER));
        }
    }

    @Override
    public void run() {
        createGameState();
        sendClientGameAccess();

        if (gameMode == FILE_GAME)
            handleFileGame();
        else
            startGame();

        updateClientsState();
    }

    private void handleFileGame() {
        DeckReader deckReader = new DeckReader();
        Deck p1Deck = client1.getPlayer().getActiveDeck();
        Deck p2Deck = client2.getPlayer().getActiveDeck();

        try {
            DeckFile deckFile = deckReader.readDeck(deckReaderFile);

            applyDeckFile(deckFile);
            startGame();

            client1.getPlayer().setActiveDeck(p1Deck);
            client2.getPlayer().setActiveDeck(p2Deck);
        } catch (IOException e) {
            System.out.println("Could not create Game from file: " + deckReaderFile.getAbsolutePath());
            e.printStackTrace();
            gameEnded();
        }
    }

    private void startGame() {
        executor.beginGame(gameMode == FILE_GAME, gameMode == AI_PLAYER);
    }


    private void applyDeckFile(DeckFile deckFile) {
        String deckName = "Read Deck";
        String heroName = "Mage";

        client1.getPlayer().setActiveDeck(new Deck(heroName, deckName, deckFile.getPlayer1CardsAsList()));
        client2.getPlayer().setActiveDeck(new Deck(heroName, deckName, deckFile.getPlayer2CardsAsList()));
    }

    private void createGameState() {
        if (gameMode != AI_PLAYER)
            gameState = new MyGameState(client1.getPlayer(), client2.getPlayer());
        else
            gameState = new MyGameState(client1.getPlayer(), cpu);

        executor = new GameActionExecutor(gameState, this);
    }

    private void sendClientGameAccess() {
        client1.sendMessage(new Message(MessageType.PLAY_ACCESS, gameMode, true));

        if (gameMode != SINGLE_PLAYER_LOCAL)
            client2.sendMessage(new Message(MessageType.PLAY_ACCESS, gameMode, false));
    }

    public void sendStringMessage(String error) {
        if (gameState.getActivePlayerIndex() == 1) {
            client1.sendMessage(new Message(MessageType.SHOW_MESSAGE, error));
        }
        else {
            client2.sendMessage(new Message(MessageType.SHOW_MESSAGE, error));
        }
    }

    public GameLogger getLogger(int player) {
        return (player == 1) ? client1.getLogger() : client2.getLogger();
    }

    public InfoPassive askForPassive(List<InfoPassive> passives, int player) {
        ClientHandler clientHandler = (player == 1) ? client1 : client2;
        clientHandler.sendMessage(new Message(MessageType.CHOOSE_PASSIVE, new ArrayList<>(passives)));
        monitor.doWait();

        return (InfoPassive) chosenModelByPlayer;
    }

    public ArrayList<Card> askForMultipleCards(List<Card> cards, String message, int player) {
        ClientHandler clientHandler = (player == 1) ? client1 : client2;
        clientHandler.sendMessage(new Message(MessageType.CHOOSE_CARDS, new ArrayList<>(cards), message));
        monitor.doWait();

        return chosenCardsByPlayer;
    }

    public Card discover(List<Card> cards) {
        ClientHandler clientHandler = (gameState.getActivePlayerIndex() == 1) ? client1 : client2;
        clientHandler.sendMessage(new Message(MessageType.CHOOSE_CARD, new ArrayList<>(cards)));
        monitor.doWait();

        return (Card) chosenModelByPlayer;
    }

    public void endTurnCommand(ClientHandler clientHandler) {
        if (canPerformAction(clientHandler)) {
            executor.endTurn();
        }
        else {
            clientHandler.sendMessage(new Message(MessageType.SHOW_MESSAGE, "It's Not Your Turn!!!"));
        }
    }

    public void clientLeaveMatch(ClientHandler clientHandler) {
        int playerIndex = (clientHandler == client1) ? 1 : 2;
        alertOpponent(clientHandler, "Opponent Left The Match!!! You Won :)");
        executor.playerLeftMatch(playerIndex);
    }

    public void playerEntry(ClientHandler clientHandler, CardInfo cardInfo) {
        if (canPerformAction(clientHandler)) {
            executor.playerEntry(cardInfo);
        }
        else if (cardInfo.getFieldType() != GameFieldType.other){
            clientHandler.sendMessage(new Message(MessageType.SHOW_MESSAGE, "It's Not Your Turn!!!"));
        }
    }

    private boolean canPerformAction(ClientHandler clientHandler) {
        return (clientHandler == client1 && gameState.getActivePlayerIndex() == 1) ||
                (clientHandler == client2 && gameState.getActivePlayerIndex() == 2);
    }

    public void clientDisconnected(ClientHandler disconnectedClient) {
        if (gameState.isGameOver()) return;

        alertOpponent(disconnectedClient, "Opponent Disconnected! Waiting For Opponent To Reconnect..." +
                "\nIf your opponent does not reconnect until " + COUNT_DOWN + " seconds, he will lose.");
        startTimer(disconnectedClient);
    }

    private void alertOpponent(ClientHandler disconnectedClient, String message) {
        ClientHandler opponent = (disconnectedClient == client1) ? client2 : client1;

        opponent.sendMessage(new Message(MessageType.SHOW_MESSAGE, message));
    }

    public void modelChosen(GameModel gameModel) {
        System.out.println("model received");
        this.chosenModelByPlayer = gameModel;
        monitor.doNotify();
    }

    public void cardsChosen(ArrayList<Card> cards) {
        this.chosenCardsByPlayer = cards;
        monitor.doNotify();
    }

    public void playerReconnected(ClientHandler clientHandler) {
        if (timer != null) timer.stopCounting();
        if (clientHandler.getPlayer().getUsername().equals(client1.getPlayer().getUsername()))
            client1 = clientHandler;
        else
            client2 = clientHandler;

        notifyGameResume();
    }

    private void notifyGameResume() {
        sendStringMessage("Game Can Be Continued Now !!");
    }

    public int getGameMode() {
        return gameMode;
    }

    public void chatReceived(ClientHandler clientHandler, String content) {
        chatList.addMessage(clientHandler.getPlayer().getUsername(), content);
        updateChats();

    }

    private void updateChats() {
        client2.sendMessage(new Message(MessageType.UPDATE_CHATS, chatList));
        client1.sendMessage(new Message(MessageType.UPDATE_CHATS, chatList));
    }

    public boolean isGameOver() {
        return gameState.isGameOver();
    }
}
