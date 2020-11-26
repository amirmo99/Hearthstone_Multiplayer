package models.heroes;

import enums.CardSituation;
import enums.Class;
import logic.CardEffects;
import logic.GameEffects.EffectList;
import models.Card;
import com.fasterxml.jackson.annotation.*;
import models.Deck;
import models.HeroPower;
import models.Heroes;
import models.cards.Weapon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class Mage extends Heroes implements Serializable {

    private static final List<Card> allCards = Card.getAllCardsOfClass(Class.Mage);

    public Mage(List<Deck> myDecks, int HP, HeroPower heroPower, String specialPower) {
        this(myDecks, HP, heroPower, specialPower, CardSituation.NEUTRAL, null, new CardEffects());
    }

    private Mage(List<Deck> decks, int HP, HeroPower heroPower, String specialPower, CardSituation situation,
                Weapon weapon, CardEffects effectList) {
        super(decks, HP, 0, heroPower, specialPower, situation, weapon, effectList);
        name = "Mage";
    }

    private Mage() {
    }

    @Override
    @JsonIgnore
    public String getName() {
        return "Mage";
    }

    @Override
    @JsonIgnore
    public List<Card> getAllCardsOfThisClass() {
        return allCards;
    }

    @Override
    public Mage cloned() {
        List<Deck> decks = new ArrayList<>();
        for (Deck myDeck : getMyDecks()) {
            decks.add(myDeck.cloned());
        }
        return new Mage(decks, getHP(), getHeroPower().cloned(), getSpecialPowerDescription(), getSituation(),
                (getWeapon() != null) ? getWeapon().cloned() : null, this.getSpecialPower().cloned());
    }
}
