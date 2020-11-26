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
public class Warlock extends Heroes implements Serializable {

    public static final List<Card> allCards = Card.getAllCardsOfClass(Class.Warlock);

    public Warlock(List<Deck> myDecks, int HP, HeroPower heroPower, String specialPower) {
        this(myDecks, HP, heroPower, specialPower, CardSituation.NEUTRAL, null, new CardEffects());
    }

    private Warlock(List<Deck> decks, int HP, HeroPower heroPower, String specialPower, CardSituation situation,
                   Weapon weapon, CardEffects effectList) {
        super(decks, HP, 0, heroPower, specialPower, situation, weapon, effectList);
        name = "Warlock";

    }

    private Warlock() {
    }

    @Override
    @JsonIgnore
    public String getName() {
        return "Warlock";
    }

    @Override
    @JsonIgnore
    public List<Card> getAllCardsOfThisClass() {
        return allCards;
    }

    @Override
    public Warlock cloned() {
        List<Deck> decks = new ArrayList<>();
        for (Deck myDeck : getMyDecks()) {
            decks.add(myDeck.cloned());
        }
        return new Warlock(decks, getHP(), getHeroPower().cloned(), getSpecialPowerDescription(), getSituation(),
                (getWeapon() != null) ? getWeapon().cloned() : null, this.getSpecialPower().cloned());
    }
}
