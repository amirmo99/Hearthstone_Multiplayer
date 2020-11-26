package logic;

import Util.GameLogger;
import com.fasterxml.jackson.annotation.*;
import configs.LogicConstants;
import enums.Class;
import enums.LogType;
import enums.Rarity;
import models.Card;
import models.Deck;
import models.Heroes;

import java.io.Serializable;
import java.util.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class Player implements Serializable, Comparable {
    public final static int SUCCESS = 0;
    public final static int TWO_SAME_CARD_LIMIT = 1;
    public final static int NOT_OWNED_CARD = 2;
    public final static int DECK_SIZE_LIMIT = 3;
    public final static int DIFFERENT_HERO_ERROR = 4;

    public final static int CARD_IN_DECKS = 4;
    public final static int NOT_ENOUGH_GEMS = 5;

    public final static int NO_ACTIVE_DECK = 6;
    public final static int NOT_ENOUGH_CARD = 7;

    private final LogicConstants constants;

    private String id;
    private String username;
    private String password;
    private int gems;
    private int trophies;
    private List<Heroes> myAllHeroes;
    private List<Card> cards = new ArrayList<>();
    private Deck activeDeck;

    public Player(String id, String username, String password, int gems, List<Heroes> myAllHeroes, List<Card> cards) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.gems = gems;
        this.trophies = 0;
        this.myAllHeroes = myAllHeroes;
        this.cards = cards;
        this.constants = new LogicConstants();
    }

    public Player() {
        constants = new LogicConstants();
    }

    public int canStartGame() {

        if (activeDeck == null) {
            return NO_ACTIVE_DECK;
        } else if (activeDeck.getCards().size() < constants.getMinCardsForGame()) {
            return NOT_ENOUGH_CARD;
        } else {
//            PlayerLogger.writeLog("successful Action", "Play access granted");
            getLogger().writeLog(LogType.SUCCESSFUL_ACTION, "Play Access Granted");
            return SUCCESS;
        }
    }

    public boolean buyCard(Card card) {
        if (getGems() >= card.getCardCost() && !this.cards.contains(card)) {
            gems -= card.getCardCost();
            cards.add(card);
//            PlayerLogger.writeLog("Buy Card", "Card : " + card.getName());
            getLogger().writeLog(LogType.SUCCESSFUL_ACTION, "Buy Card: " + card.getName());
            return true;
        }
        return false;
    }

    public int sellCard(Card card) {

        if (!cards.contains(card)) {
            return NOT_OWNED_CARD;
        } else if (isCardInDecks(card)) {
            return CARD_IN_DECKS;
        } else {
//            PlayerLogger.writeLog("successful action", "Bought Card : " + card.getName());
            getLogger().writeLog(LogType.SUCCESSFUL_ACTION, "Bought Card : " + card.getName());
            gems += card.getCardCost();
            cards.remove(card);
            return SUCCESS;
        }
    }


    public List<Card> getFilteredCards(List<Card> cards, int mana, String cardName, String ownedCardsOnly) {
        List<Card> filtered = new ArrayList<>();
        cardName = cardName.trim();

        for (Card card : cards) {
            if ((mana == -1 || card.getMana() == mana) && card.getName().contains(cardName) &&
                    (ownedCardsOnly.equals("All Cards") ||
                            ((ownedCardsOnly.equals("Only Owned Cards")) == this.cards.contains(card)))) {
                filtered.add(card);
            }
        }
        return filtered;
    }

    @JsonIgnore
    public List<Deck> getMyDecks() {
        List<Deck> decks = new ArrayList<>();
        for (Heroes hero : myAllHeroes) {
            decks.addAll(hero.getMyDecks());
        }

        return decks;
    }

    public void addDeck(String deckName, String heroName) {
        Heroes hero = Heroes.getHero(this, heroName);
        if (hero != null)
            hero.addDeck(deckName);
//        PlayerLogger.writeLog("New Deck added", "deck : " + deckName + ", hero : " + heroName);
        getLogger().writeLog(LogType.REPORT, "Deck Name: " + deckName + ", hero : " + heroName);
    }

    public int addCardToDeck(Card card, Deck deck) {

        if (!cards.contains(card)) {
            return NOT_OWNED_CARD;
        } else if (Collections.frequency(deck.getCards(), card) >= 2) {
            return TWO_SAME_CARD_LIMIT;
        } else if (deck.getCards().size() > constants.getDeckMaxSize()) {
            return DECK_SIZE_LIMIT;
        } else if (card.getCardClass() != Class.Natural &&
                !deck.getHeroName().equalsIgnoreCase(card.getCardClass().toString())) {
            return DIFFERENT_HERO_ERROR;
        } else {
//            PlayerLogger.writeLog("add card to deck", "Successfully added card to  deck");
            getLogger().writeLog(LogType.SUCCESSFUL_ACTION, "Card " + card.getName() + " added to deck " + deck.getName());
            deck.addCard(card);
            return SUCCESS;
        }
    }

    public void removeDeck(Deck badDeck) {
        for (Heroes hero : myAllHeroes) {
            List<Deck> decks = new ArrayList<>();
            for (Deck deck : hero.getMyDecks()) {
                if (deck != badDeck)
                    decks.add(deck);
            }
            hero.setMyDecks(decks);
        }
//        PlayerLogger.writeLog("removed deck", "deck : " + badDeck.getName() + " is removed");
        getLogger().writeLog(LogType.SUCCESSFUL_ACTION, "Deck : " + badDeck.getName() + " is removed");
    }

    public void removeDeck(int index) {
        Deck deck = getMyDecks().get(index);
        if (deck == activeDeck)
            activeDeck = null;
        removeDeck(deck);
    }

    public boolean existsDeckName(String name) {
        for (Deck deck : getMyDecks()) {
            if (deck.getName().equals(name))
                return true;
        }
        return false;
    }

    @JsonIgnore
    public List<Card> getUnownedCards() {
        List<Card> cards = new ArrayList<>();
        for (Card card : Card.getAllCards()) {
            if (!this.cards.contains(card))
                cards.add(card);
        }
        return cards;
    }

    @JsonIgnore
    public List<Card> getSellableCards() {
        List<Card> cards = new ArrayList<>();
        for (Card card : this.cards) {
            if (card.getCardRarity() != Rarity.Free)
                cards.add(card);
        }
        return cards;
    }

    @JsonIgnore
    public boolean isCardInDecks(Card card) {
        for (Deck deck : getMyDecks()) {
            if (deck.isCardInDeck(card))
                return true;
        }
        return false;
    }

    public void winGame(boolean updateDeck) {
        if (updateDeck) {
            getActiveDeck().setPlayedGames(getActiveDeck().getPlayedGames() + 1);
            getActiveDeck().setWonGames(getActiveDeck().getWonGames() + 1);
            getActiveDeck().setGainedTrophy(getActiveDeck().getGainedTrophy() + constants.getWinnerTrophies());
        }
        trophies = trophies + constants.getWinnerTrophies();
    }

    public void loseGame(boolean updateDeck) {
        if (updateDeck) {
            getActiveDeck().setPlayedGames(getActiveDeck().getPlayedGames() + 1);
            getActiveDeck().setGainedTrophy(getActiveDeck().getGainedTrophy() - Math.min(constants.getLoserTrophies(), trophies));
        }
        trophies = Math.max(trophies - constants.getLoserTrophies(), 0);
    }

    ////// Getters and Setters

    public String getId() {
        return id;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public List<Heroes> getMyAllHeroes() {
        return myAllHeroes;
    }

    public void setMyAllHeroes(List<Heroes> myAllHeroes) {
        this.myAllHeroes = myAllHeroes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGems() {
        return gems;
    }

    public void setGems(int gems) {
        this.gems = gems;
    }

    public Deck getActiveDeck() {
        return activeDeck;
    }

    public void setActiveDeck(Deck activeDeck) {
        this.activeDeck = activeDeck;
    }

    @JsonIgnore
    public GameLogger getLogger() {
        return GameLogger.getInstance(this);
    }

    @JsonIgnore
    public Deck getDeck(String deckName) {
        if (existsDeckName(deckName))
            return findDeck(deckName);
        else
            return null;
    }

    @JsonIgnore
    private Deck findDeck(String deckName) {
        for (Deck deck : getMyDecks()) {
            if (deck.getName().equals(deckName))
                return deck;
        }
        return null;
    }

    public int getTrophies() {
        return trophies;
    }

    @Override
    public int compareTo(Object o) {
        if (! (o instanceof Player))
            return 0;
        Player player = (Player) o;

        return Integer.compare(player.getTrophies(), trophies);
    }
}



