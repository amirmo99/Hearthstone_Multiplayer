package network.server;

import Util.BundleRankTableModels;
import Util.RankTableModel;
import Util.Tools;
import configs.LogicConstants;
import logic.CardInfo;
import logic.Player;
import models.Card;
import models.GameModel;
import network.api.Message;
import network.api.MessageType;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class MainServer extends Thread {

    private ArrayList<Player> onlinePlayers;
    private HashMap<Player, GameServer> gameServerMap;
    private final LogicConstants constants;

    private ServerSocket serverSocket;

    private LinkedList<ClientHandler> fileGameQueue, multiplayerGameQueue;

    public MainServer(int port) throws IOException {
        super();
        serverSocket = new ServerSocket(port);
        onlinePlayers = new ArrayList<>();
        gameServerMap = new HashMap<>();
        constants = new LogicConstants();

        fileGameQueue = new LinkedList<>();
        multiplayerGameQueue = new LinkedList<>();
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            Socket socket;
            try {
                System.out.println("Server is listening ...");
                socket = serverSocket.accept();
                createClient(socket);
                System.out.println("Client found: " + socket.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleMessage(ClientHandler clientHandler, Message message) {
        switch (message.getType()) {
            case CREATE_FILE_GAME:
                createFileGame(clientHandler);
                break;
            case CREATE_LOCAL_GAME:
                createLocalGame(clientHandler);
                break;
            case CREATE_VS_AI_GAME:
                createAIGame(clientHandler);
                break;
            case CREATE_MULTIPLAYER_GAME:
                createMultiPlayerGame(clientHandler);
                break;
            case GAME_ACTION_LEAVE_MATCH:
                new Thread(() -> gameServerForPlayer(clientHandler.getPlayer()).clientLeaveMatch(clientHandler)).start();
                break;
            case GAME_ACTION_END_TURN:
                new Thread(() -> gameServerForPlayer(clientHandler.getPlayer()).endTurnCommand(clientHandler)).start();
                break;
            case GAME_ACTION_ENTRY:
                new Thread(() -> gameServerForPlayer(clientHandler.getPlayer()).playerEntry(clientHandler, (CardInfo) message.getData())).start();
                break;
            case CARD_CHOSE:
            case PASSIVE_CHOSE:
                new Thread(() -> gameServerForPlayer(clientHandler.getPlayer()).modelChosen((GameModel) message.getData())).start();
                break;
            case CARDS_CHOSE:
                new Thread(() -> gameServerForPlayer(clientHandler.getPlayer()).cardsChosen((ArrayList<Card>) message.getData())).start();
                break;
            case RANKS_UPDATE_REQUEST:
                BundleRankTableModels tableModels = createRanksTableModels(clientHandler);
                clientHandler.sendMessage(new Message(MessageType.RANKS_TABLE_UPDATE, tableModels));
                break;
            case CHAT_MESSAGE:
                String content = (String) message.getData();
                if (gameServerMap.containsKey(clientHandler.getPlayer())) {
                    gameServerMap.get(clientHandler.getPlayer()).chatReceived(clientHandler, content);
                }
                break;
        }
    }

    private BundleRankTableModels createRanksTableModels(ClientHandler clientHandler) {
        List<Player> allPlayers = Tools.getAllPlayers();
        if (allPlayers == null || findPlayerRank(allPlayers, clientHandler.getPlayer()) == -1) return null;

        allPlayers.sort(Player::compareTo);
        int rank = findPlayerRank(allPlayers, clientHandler.getPlayer());

        RankTableModel topTen = new RankTableModel(0, Math.min(9, allPlayers.size() - 1), allPlayers);
        RankTableModel aroundPlayer = new RankTableModel(Math.max(0, rank - 5), Math.min(rank + 5, allPlayers.size() - 1), allPlayers);

        return new BundleRankTableModels(topTen, aroundPlayer);
    }

    private int findPlayerRank(List<Player> sortedPlayers, Player player) {
        for (int i = 0; i < sortedPlayers.size(); i++) {
            if (sortedPlayers.get(i).getId().equals(player.getId()))
                return i;
        }
        return -1;
    }

    public boolean playerLoggedIn(ClientHandler clientHandler) {

        GameServer gameServer = gameServerForPlayer(clientHandler.getPlayer());

        if (isPlayerOnline(clientHandler.getPlayer())) {
            clientHandler.sendMessage(new Message(MessageType.SHOW_MESSAGE, "You are online with another device."));
            return false;
        }
        else if (gameServer != null) {
            gameServer.playerReconnected(clientHandler);
            clientHandler.sendMessage(new Message(MessageType.PLAY_ACCESS, gameServer.getGameMode(), true));
        }
        else
            clientHandler.sendMessage(new Message(MessageType.SUCCESSFUL_LOGIN));

        onlinePlayers.add(clientHandler.getPlayer());
        return true;
    }

    private boolean isPlayerOnline(Player player) {
        for (Player onlinePlayer : onlinePlayers) {
            if (player.getUsername().equals(onlinePlayer.getUsername()))
                return true;
        }
        return false;
    }

    private GameServer gameServerForPlayer(Player player) {
        for (Map.Entry<Player, GameServer> entry : gameServerMap.entrySet()) {
            if (entry.getKey().getUsername().equals(player.getUsername())) {
                if (!entry.getValue().isGameOver())
                    return entry.getValue();
            }
        }
        return null;
    }

    private void createClient(Socket socket) {
        if (socket != null) {
            ClientHandler clientHandler = new ClientHandler(this, socket);
            clientHandler.start();
        }
    }

    public void disconnectClient(ClientHandler clientHandler) {
        onlinePlayers.remove(clientHandler.getPlayer());

        if (gameServerMap.containsKey(clientHandler.getPlayer())) {
            gameServerMap.get(clientHandler.getPlayer()).clientDisconnected(clientHandler);
        }
        while (fileGameQueue.contains(clientHandler))
            fileGameQueue.remove(clientHandler);
        while (multiplayerGameQueue.contains(clientHandler))
            multiplayerGameQueue.remove(clientHandler);
    }

    public boolean canRunGame(ClientHandler clientHandler) {
        int report = clientHandler.getPlayer().canStartGame();
        switch (report) {
            case Player.NOT_ENOUGH_CARD:
                clientHandler.showError("Cards in your deck must be at least " + constants.getMinCardsForGame());
                return false;
            case Player.NO_ACTIVE_DECK:
                clientHandler.showError("Activate a deck in collections for playing");
                return false;
            case Player.SUCCESS:
                return true;
        }
        return false;
    }

    private void createGame(ClientHandler client1, ClientHandler client2, int gameMode) {
        GameServer gameServer = new GameServer(client1, client2, this, gameMode);
        gameServerMap.put(client1.getPlayer(), gameServer);
        gameServerMap.put(client2.getPlayer(), gameServer);

        gameServer.start();
    }

    private void createLocalGame(ClientHandler clientHandler) {
        if (canRunGame(clientHandler)) {
            GameServer gameServer = new GameServer(this, clientHandler, GameServer.SINGLE_PLAYER_LOCAL);
            gameServerMap.put(clientHandler.getPlayer(), gameServer);

            gameServer.start();
        }
    }

    private void createAIGame(ClientHandler clientHandler) {
        if (canRunGame(clientHandler)) {
            GameServer gameServer = new GameServer(this, clientHandler, GameServer.AI_PLAYER);
            gameServerMap.put(clientHandler.getPlayer(), gameServer);

            gameServer.start();
        }
    }

    private void createFileGame(ClientHandler clientHandler) {
//        clientHandler.setFileDeck(deck);
        fileGameQueue.push(clientHandler);
        while (multiplayerGameQueue.contains(clientHandler))
            multiplayerGameQueue.remove(clientHandler);

        sendClientWaitingMessage(clientHandler);
        checkForFileGame();
    }

    private void createMultiPlayerGame(ClientHandler clientHandler) {
        if (canRunGame(clientHandler)) {
            multiplayerGameQueue.push(clientHandler);
            while (fileGameQueue.contains(clientHandler))
                fileGameQueue.remove(clientHandler);

            sendClientWaitingMessage(clientHandler);
            checkMultiPlayerGame();
        }
    }

    private void sendClientWaitingMessage(ClientHandler clientHandler) {
        clientHandler.sendMessage(new Message(MessageType.SHOW_MESSAGE, "Waiting for another player to join the match"));
    }

    private synchronized void checkForFileGame() {
        if (fileGameQueue.size() >= 2) {
            ClientHandler client1 = fileGameQueue.pollLast();
            ClientHandler client2 = fileGameQueue.pollLast();

            createGame(client1, client2, GameServer.FILE_GAME);
        }
    }

    private void checkMultiPlayerGame() {
        if (multiplayerGameQueue.size() >= 2) {
            ClientHandler client1 = multiplayerGameQueue.pollLast();
            ClientHandler client2 = multiplayerGameQueue.pollLast();

            createGame(client1, client2, GameServer.MULTI_PLAYER);
        }
    }

    public void gameEnded(ClientHandler clientHandler) {
        while (gameServerMap.containsKey(clientHandler.getPlayer()))
            gameServerMap.remove(clientHandler.getPlayer());
    }
}
