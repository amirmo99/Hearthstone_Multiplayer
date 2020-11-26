package network.server;

import Util.Administrator;
import Util.GameLogger;
import abstracts.MessageReceiver;
import configs.LogicConstants;
import enums.LogType;
import logic.Player;
import models.Card;
import models.Deck;
import network.api.DeckInformation;
import network.api.Message;
import network.api.MessageType;
import network.util.Receiver;
import network.util.Sender;

import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;

public class ClientHandler extends Thread implements MessageReceiver {

    private Player player;
    private MainServer server;
//    private Deck fileDeck;

    private Socket socket;
    private Receiver receiver;
    private Sender sender;

    private int token;

    public ClientHandler(MainServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            initSocket();
            setToken();
            sendClientToLoginPanel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendClientToLoginPanel() {
        sendMessage(new Message(MessageType.GO_TO_LOGIN_PANEL));
    }

    private void setToken() {
        token = new SecureRandom().nextInt();
    }

    private void initSocket() throws IOException {
        receiver = new Receiver(socket.getInputStream(), this);
        sender = new Sender(socket.getOutputStream());

        receiver.start();
    }

    private boolean isTokenValid(Message message) {
        if (message.getType() == MessageType.SIGN_IN || message.getType() == MessageType.SIGN_UP)
            return true;
        else
            return message.getToken() == token;
    }
    @Override
    public void receive(Message message) {

        if (message == null) {
            server.disconnectClient(this);
        }
        else if (isTokenValid(message)){

            switch (message.getType()) {
                case SIGN_IN:
                    String[] signInData = (String[]) message.getData();
                    if (loginRequest(signInData[0], signInData[1])) {
                        notifyPlayerToLogin();
                        notifyServer();
                    }

                    break;
                case SIGN_UP:
                    String[] signUpInfo = (String[]) message.getData();
                    if (signUpRequest(signUpInfo[0], signUpInfo[1])) {
                        notifyPlayerToLogin();
                        notifyServer();
                    }

                    break;
                case SIGN_OUT:
                    signPlayerOut();
                    break;
                case DELETE_PLAYER:
                    deleteRequest();
                    break;
                case BUY_CARD:
                    String cardNameToBuy = (String) message.getData();
                    buyAttempt(cardNameToBuy);
                    break;
                case SELL_CARD:
                    String cardNameToSell = (String) message.getData();
                    sellAttempt(cardNameToSell);
                    break;
                case CREATE_DECK:
                    DeckInformation deckInformationToCreate = (DeckInformation) message.getData();
                    createNewDeck(deckInformationToCreate.getName(), deckInformationToCreate.getHeroName());
                    break;
                case REMOVE_DECK:
                    int removeIndex = (int) message.getData();
                    removeDeck(removeIndex);
                    break;
                case ACTIVATE_DECK:
                    int activateIndex = (int) message.getData();
                    activateDeck(activateIndex);
                    break;
                case ADD_CARD_TO_DECK:
                    String[] addCardData = (String[]) message.getData();
                    addCardToDeck(addCardData[0], addCardData[1]);
                    break;
                case REMOVE_CARD_FROM_DECK:
                    String[] removeCardData = (String[]) message.getData();
                    removeCardFromDeck(removeCardData[0], removeCardData[1]);
                    break;
                case CHANGE_DECK_DETAILS:
                    DeckInformation deckInfo = (DeckInformation) message.getData();
                    changeDeckDetails(deckInfo.getName(), deckInfo.getHeroName(), deckInfo.getIndex());
                    break;
                case PLAYER_UPDATE_REQUEST:
                    System.out.println("sending player info...");
                    sendMessage(new Message(MessageType.PLAYER_UPDATE, player));
                    System.out.println("player info sent.");
                    break;

                default:
                    server.handleMessage(this, message);
            }
        }
    }

    private boolean notifyServer() {
        return server.playerLoggedIn(this);
    }

    private void notifyPlayerToLogin() {
        sendMessage(new Message(MessageType.AUTH_TOKEN));
        sendMessage(new Message(MessageType.PLAYER_UPDATE, player));
    }

    public void sendMessage(Message message) {
        if (message.getType() == MessageType.AUTH_TOKEN)
            message.setToken(token);

        if (socket.isConnected() && !socket.isOutputShutdown())
            sender.send(message);
    }

    public Deck getDeck(int index) {
        return player.getMyDecks().get(index);
    }

    public GameLogger getLogger() {
        return player.getLogger();
    }

    public void signPlayerOut() {
        getLogger().writeLog(LogType.SIGN_OUT);
        server.disconnectClient(this);
        player = null;
    }

    public void buyAttempt(String cardName) {
        Card card = Card.getCard(cardName);
        if (card == null)
            showError("This Card Does Not Exist");
        else {
            boolean success = player.buyCard(card);
            writePlayerData();
            if (!success)
                showError("You Don't Have Enough Gems");
        }
    }

    public void sellAttempt(String cardName) {
        Card card = Card.getCard(cardName);

        int report = player.sellCard(card);
        switch (report) {
            case Player.CARD_IN_DECKS:
                showError("Card is in one of your decks");
                break;
            case Player.NOT_OWNED_CARD:
                showError("You do not have this card");
                break;
            case Player.SUCCESS:
                writePlayerData();
                break;
        }
    }

    public void removeDeck(int index) {
        player.removeDeck(index);
        writePlayerData();
    }

    public void removeCardFromDeck(String selectedDeckName, String cardName) {
        Deck selectedDeck = player.getDeck(selectedDeckName);

        selectedDeck.removeCard(cardName);
        writePlayerData();
    }

    public void createNewDeck(String deckName, String heroName) {
        if (player.existsDeckName(deckName)) {
            showError("A deck with this name already exists");
        } else if (deckName.length() < new LogicConstants().getDeckNameMinLength()) {
            showError("Deck name is too short");
        } else {
            player.addDeck(deckName, heroName);
            writePlayerData();
        }
    }

    public void addCardToDeck(String selectedDeckName, String cardName) {

        Card card = Card.getCard(cardName);
        Deck selectedDeck = player.getDeck(selectedDeckName);

        if (selectedDeck == null || card == null) {
            showError("Invalid Entries!");
        } else {
            int report = player.addCardToDeck(card, selectedDeck);
            switch (report) {
                case Player.NOT_OWNED_CARD:
                    sendMessage(new Message(MessageType.SHOW_GO_SHOP_OPTION));
                    break;
                case Player.DIFFERENT_HERO_ERROR:
                    showError("Deck hero and card class does not match");
                    break;
                case Player.DECK_SIZE_LIMIT:
                    showError("Deck Size Limit");
                    break;
                case Player.TWO_SAME_CARD_LIMIT:
                    showError("You Can't Have More Than 2 Cards of This Same Cards");
                    break;
                case Player.SUCCESS:
                    break;
            }
            writePlayerData();
        }
    }

    private void writePlayerData() {
        Administrator.updateDataModels(player);
    }

    public boolean loginRequest(String username, String password) {
        boolean success = Administrator.loginRequest(username, password);
        if (success) {
            setPlayer(Administrator.getPlayer(username));
        } else
            showError("Sorry!!! Wrong username or password.");
        return success;
    }

    public boolean signUpRequest(String username, String password) {
        int report = Administrator.signUpRequest(username, password);
        switch (report) {
            case Administrator.SHORT_PASSWORD:
                showError("Password Is Too Short");
                break;
            case Administrator.TAKEN_USER:
                showError("This Username Is Already Taken");
                break;
            case Administrator.SUCCESS:
                setPlayer(Administrator.getPlayer(username));
                return true;
        }
        return false;
    }

    private void setPlayer(Player player) {
        this.player = player;
    }

    public void showError(String error) {
        sendMessage(new Message(MessageType.SHOW_MESSAGE, error));
        if (player != null) {
            getLogger().writeLog(LogType.ERROR, "Message : " + error);
        }
    }

    private void activateDeck(int index) {
        player.setActiveDeck(getDeck(index));
        getLogger().writeLog(LogType.REPORT, "Deck Activated -> Deck name : " + player.getActiveDeck().getName());
        writePlayerData();
    }

    private void changeDeckDetails(String name, String heroName, int index) {
        if (player.existsDeckName(name) && !getDeck(index).getName().equals(name)) {
            showError("A deck with this name already exists.");
        } else if (name.length() < new LogicConstants().getDeckNameMinLength()) {
            showError("Deck name is too short.");
        } else if (!heroName.equals(getDeck(index).getHeroName()) && getDeck(index).hasHeroCard()) {
            showError("Cant change models.heroes. There are some NOT Natural models.cards in this deck.");
        } else {
            getLogger().writeLog(LogType.REPORT, "Deck Details changed -> Name: " + name + ", Hero: " + heroName);
            getDeck(index).setName(name);
            getDeck(index).setHero(heroName);
        }
        writePlayerData();
    }

    private void deleteRequest() {
        Administrator.deletePlayer(this.player);
        player = null;
    }

    public Player getPlayer() {
        return player;
    }

}
