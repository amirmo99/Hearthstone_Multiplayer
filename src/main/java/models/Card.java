package models;

import abstracts.Prototype;
import Util.Tools;
import enums.CardSituation;
import enums.Class;
import enums.Rarity;
import enums.Type;
import logic.CardEffects;
import models.cards.Minion;
import models.cards.Mission;
import models.cards.Spell;
import models.cards.Weapon;
import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.util.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "models.cards.Spell", value = Spell.class),
        @JsonSubTypes.Type(name = "models.cards.Minion", value = Minion.class),
        @JsonSubTypes.Type(name = "models.cards.Weapon", value = Weapon.class),
        @JsonSubTypes.Type(name = "models.cards.Mission", value = Mission.class)
})
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public abstract class Card extends GameModel implements Prototype, Comparable, Serializable {

    private int mana, cardCost;
    private String description = "";
    private Rarity cardRarity;
    private Class cardClass;
    private Type cardType;
    private int usedTimes;

    private CardEffects cardEffects;
    private CardSituation situation;

    private static final List<Card> allCards = Tools.getAllCards();

    public Card(int mana, int cardCost, String name, String description, Rarity cardRarity, Class cardClass,
                Type cardType, int usedTimes, CardEffects effects, CardSituation situation) {
        this.mana = mana;
        this.cardCost = cardCost;
        this.name = name;
        this.description = description;
        this.cardRarity = cardRarity;
        this.cardClass = cardClass;
        this.cardType = cardType;
        this.usedTimes = usedTimes;
        this.cardEffects = effects;
        this.situation = situation;
    }

    public Card() {

    }

    public static Card getCard(String name) {
        Card card;
        List<Card> all;

        for (Card value : allCards) {
            if (name.equalsIgnoreCase(value.getName())) {
                card = value;
                return card.cloned();
            }
        }
        return null;
    }

    public static List<Card> getAllFreeCards() {
        List<Card> freeCards = new ArrayList<>();
        for (Card card : allCards) {
            if (card.getCardRarity() == Rarity.Free)
                freeCards.add(card);
        }
        return freeCards;
    }

    public static List<Card> getAllCardsOfClass(Class cardClass) {
        List<Card> classedCards = new ArrayList<>();
        for (Card card : allCards) {
            if (card.getCardClass() == cardClass)
                classedCards.add(card);
        }
        return classedCards;
    }

    public static List<Card> getAllCardOfType(Type type) {
        List<Card> classedCards = new ArrayList<>();
        for (Card card : allCards) {
            if (card.getCardType() == type)
                classedCards.add(card);
        }
        return classedCards;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Card))
            return 0;
        Card card = (Card) o;
        return (usedTimes > card.getUsedTimes()) ||
                (usedTimes == card.getUsedTimes() && getCardRarity().getNumber() > card.getCardRarity().getNumber()) ||
                (usedTimes == card.getUsedTimes() && cardRarity.getNumber() == card.getCardRarity().getNumber() && mana > card.getMana()) ||
                (usedTimes == card.getUsedTimes() && cardRarity.getNumber() == card.getCardRarity().getNumber() &&
                        mana == card.getMana() && cardType == Type.Minion) ? -1 :
                (usedTimes == card.getUsedTimes() && cardRarity.getNumber() == card.getCardRarity().getNumber() &&
                        mana == card.getMana() && cardType == card.getCardType()) ? 0 : -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;
        Card card = (Card) o;
        return Objects.equals(name, card.name);
    }

    @Override
    public abstract Card cloned();

    @Override
    public CardEffects findEffects() {
        return cardEffects;
    }


    // Getters and setters

    public Type getCardType() {
        return cardType;
    }

    public void setCardType(Type cardType) {
        this.cardType = cardType;
    }

    public int getCardCost() {
        return cardCost;
    }

    public void setCardCost(int cardCost) {
        this.cardCost = cardCost;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Rarity getCardRarity() {
        return cardRarity;
    }

    public void setCardRarity(Rarity cardRarity) {
        this.cardRarity = cardRarity;
    }

    public Class getCardClass() {
        return cardClass;
    }

    public void setCardClass(Class cardClass) {
        this.cardClass = cardClass;
    }

    public static List<Card> getAllCards() {
        return allCards;
    }

    public int getUsedTimes() {
        return usedTimes;
    }

    public void setUsedTimes(int usedTimes) {
        this.usedTimes = usedTimes;
    }

    public CardSituation getSituation() {
        return situation;
    }

    public void setSituation(CardSituation situation) {
        this.situation = situation;
    }

    public CardEffects getCardEffects() {
        return cardEffects;
    }

    public void setCardEffects(CardEffects cardEffects) {
        this.cardEffects = cardEffects;
    }

}
