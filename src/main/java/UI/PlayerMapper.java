package UI;

import Util.GameLogger;
import enums.LogType;
import logic.Player;
import models.Card;
import models.Deck;
import models.Heroes;
import network.api.DeckInformation;
import network.api.Message;
import network.api.MessageType;
import network.client.Client;

import java.util.List;

public class PlayerMapper {

    private Player player;
    private Deck selectedDeck;

    private Client client;

    public PlayerMapper(Client client) {
        this.client = client;
    }

    public boolean isPlayerLoggedIn() {
        return player != null;
    }

    public boolean isCardLocked(Card card) {
        return !player.getCards().contains(card);
    }

    public List<Card> getPlayerUnownedCards() {
        return player.getUnownedCards();
    }

    public List<Card> getPlayerSellableCards() {
        return player.getSellableCards();
    }

    public int getPlayerGems() {
        return player.getGems();
    }

    public String getPlayerName() {
        return player.getUsername();
    }

    public List<Deck> playerDecks() {
        return player.getMyDecks();
    }

    public List<Card> filteredCards(List<Card> cards, int mana, String searchText, String ownedCardsOnly) {
        return player.getFilteredCards(cards, mana, searchText, ownedCardsOnly);
    }

    public List<Heroes> playerHeroes() {
        return player.getMyAllHeroes();
    }

    public Deck getSelectedDeck() {
        return selectedDeck;
    }

    public void setSelectedDeck(int index) {
        this.selectedDeck = player.getMyDecks().get(index);
    }

    public List<Deck> getSortedDecks() {
        List<Deck> sortedDecks = playerDecks();
        sortedDecks.sort(Deck::compareTo);
        return sortedDecks;
    }

    public Deck getDeck(int index) {
        return player.getMyDecks().get(index);
    }

    public GameLogger getLogger() {
        return player.getLogger();
    }

    public int getDecksSize() {
        return player.getMyDecks().size();
    }

    public void signPlayerOut() {
        player = null;
        sendMessage(MessageType.SIGN_OUT);
    }

    public void buyAttempt(Card card) {
        sendMessage(MessageType.BUY_CARD, card.getName());
    }

    public void sellAttempt(Card card) {
        sendMessage(MessageType.SELL_CARD, card.getName());
    }

    public void removeDeck(int index) {
        sendMessage(MessageType.REMOVE_DECK, index);
    }

    public void removeCardFromDeck(int index) {
        if (selectedDeck == null) return;

        String[] data = new String[]{selectedDeck.getName(), selectedDeck.getCards().get(index).getName()};
        sendMessage(MessageType.REMOVE_CARD_FROM_DECK, data);
    }

    public void createNewDeck(String deckName, String heroName) {
        DeckInformation deckInfo = new DeckInformation(deckName, heroName, 0);
        sendMessage(MessageType.CREATE_DECK, deckInfo);
    }

    public void addCardToDeck(Card card) {
        if (selectedDeck == null) {
            showError("You have to select a deck first");
        } else {

            String[] data = new String[]{selectedDeck.getName(), card.getName()};
            sendMessage(MessageType.ADD_CARD_TO_DECK, data);
        }
    }

    public boolean loginRequest(String username, String password) {
        String[] data = new String[]{username, password};
        sendMessage(MessageType.SIGN_IN, data);
        return false;
    }

    public boolean signUpRequest(String username, String password) {
        String[] data = new String[]{username, password};
        sendMessage(MessageType.SIGN_UP, data);
        return false;
    }

    public void setPlayer(Player player) {
        this.player = player;
        client.getGameMapper().setPlayer(player);
    }

    public void showError(String error) {
        client.getMainFrame().showError(error);
        if (player != null) {
            getLogger().writeLog(LogType.ERROR, "Message : " + error);
        }
    }

    public void resetSelectedDeck() {
        selectedDeck = null;
    }

    public String isDeckActive(Deck deck) {
        return (deck.equals(player.getActiveDeck())) ? "Active" : "";
    }

    public String isDeckActive(int index) {
        return isDeckActive(getDeck(index));
    }

    public void activateDeck(int index) {
        sendMessage(MessageType.ACTIVATE_DECK, index);
    }

    public void changeDeckDetails(String name, String heroName, int index) {

        DeckInformation deckInfo = new DeckInformation(name, heroName, index);
        sendMessage(MessageType.CHANGE_DECK_DETAILS, deckInfo);
    }

    public void deleteRequest() {
        sendMessage(MessageType.DELETE_PLAYER);
    }

    private void sendMessage(MessageType type, Object data) {
        Message message = new Message(type, data);
        client.sendMessage(message);
    }

    private void sendMessage(MessageType type) {
        sendMessage(type, null);
    }
}
