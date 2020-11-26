package models;

import abstracts.Prototype;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import enums.Class;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "Card", value = Card.class)
})
public class Deck implements Comparable, Prototype, Serializable {

    private String heroName;
    private int wonGames;
    private int playedGames;
    private int gainedTrophy;
    private List<Card> cards;
    private String name;

    private Deck(String heroName, String name, int wonGames, int playedGames, int gainedTrophy, List<Card> cards) {
        this.heroName = heroName;
        this.name = name;
        this.wonGames = wonGames;
        this.playedGames = playedGames;
        this.cards = cards;
        this.gainedTrophy = gainedTrophy;
    }

    public Deck(String heroName, String deckName, List<Card> cards) {
        this(heroName, deckName, 0, 0, 0, cards);
    }

    public Deck(String heroName, String name) {
        this(heroName, name, 0, 0, 0, new ArrayList<>());
    }

    private Deck() {
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public void removeCard(Card card) {
        if (isCardInDeck(card))
            cards.remove(card);
    }

    public void removeCard(String cardName) {
        for (int i = 0; i < cards.size(); i++) {
            if (cardName.equals(cards.get(i).name)) {
                cards.remove(i);
                return;
            }
        }
    }

    public void removeCard(int index) {
        cards.remove(index);
    }

    public boolean isCardInDeck(Card card) {
        for (Card myCard : cards) {
            if (myCard.equals(card))
                return true;
        }
        return false;
    }

    public int victoryRate() {
        if (playedGames == 0)
            return 0;
        else
            return wonGames / playedGames;
    }

    public double averageMana() {
        if (cards.size() == 0)
            return 0;
        int mean = 0;
        for (Card card : cards) {
            mean += card.getMana();
        }
        return (double) mean / cards.size();
    }

    public boolean hasHeroCard() {
        for (Card card : cards) {
            if (card.getCardClass() != Class.Natural)
                return true;
        }
        return false;
    }

    public Card bestCard() {
        if (cards.size() == 0)
            return null;

        cards.sort(Card::compareTo);
        return cards.get(0);
    }

    @Override
    public Deck cloned() {
        List<Card> cardList = new ArrayList<>();
        for (Card card : cards) {
            cardList.add(card.cloned());
        }
        return new Deck(heroName, name, wonGames, playedGames, gainedTrophy, cardList);
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Deck))
            return 0;
        Deck deck2 = (Deck) o;

        return ((gainedTrophy > deck2.getGainedTrophy()) ||
                (gainedTrophy == deck2.getGainedTrophy() && victoryRate() > deck2.victoryRate()) ||
                (gainedTrophy == deck2.getGainedTrophy() && victoryRate() == deck2.victoryRate() && getWonGames() > deck2.getWonGames()) ||
                (gainedTrophy == deck2.getGainedTrophy() && victoryRate() == deck2.victoryRate() && getWonGames() == deck2.getWonGames() &&
                        getPlayedGames() > deck2.getPlayedGames()) ||
                (gainedTrophy == deck2.getGainedTrophy() && victoryRate() == deck2.victoryRate() && getWonGames() == deck2.getWonGames() &&
                        getPlayedGames() == deck2.getPlayedGames() && averageMana() < deck2.averageMana()) ) ? -1 :
                (victoryRate() == deck2.victoryRate() && getWonGames() == deck2.getWonGames() &&
                        getPlayedGames() == deck2.getPlayedGames() && averageMana() == deck2.averageMana()) ? 0 : 1;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Deck)) return false;
        Deck deck = (Deck) o;
        return Objects.equals(heroName, deck.heroName) &&
                Objects.equals(name, deck.name);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @JsonIgnore
    public void setHero(Heroes hero) {
        this.heroName = hero.getName();
    }

    @JsonIgnore
    public void setHero(String heroName) {
        this.heroName = heroName;
    }

    //Getters And Setters

    public int getWonGames() {
        return wonGames;
    }

    public int getPlayedGames() {
        return playedGames;
    }

    public List<Card> getCards() {
        return cards;
    }

    public String getHeroName() {
        return heroName;
    }

    public void setHeroName(String heroName) {
        this.heroName = heroName;
    }

    public void setWonGames(int wonGames) {
        this.wonGames = wonGames;
    }

    public void setPlayedGames(int playedGames) {
        this.playedGames = playedGames;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGainedTrophy() {
        return gainedTrophy;
    }

    public void setGainedTrophy(int gainedTrophy) {
        this.gainedTrophy = gainedTrophy;
    }
}
