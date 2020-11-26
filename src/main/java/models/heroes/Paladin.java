package models.heroes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import enums.CardSituation;
import enums.Class;
import logic.CardEffects;
import logic.GameEffects.EffectList;
import models.Card;
import models.Deck;
import models.HeroPower;
import models.Heroes;
import models.cards.Weapon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class Paladin extends Heroes implements Serializable {
    private static final List<Card> allCards = Card.getAllCardsOfClass(Class.Paladin);

    private Paladin(List<Deck> decks, int HP, HeroPower heroPower, String specialPower, CardSituation situation,
                    Weapon weapon, CardEffects effectList) {
        super(decks, HP, 0, heroPower, specialPower, situation, weapon, effectList);
        name = "Paladin";
    }

    public Paladin(List<Deck> myDecks, int HP, HeroPower heroPower, String specialPower) {
        this(myDecks, HP, heroPower, specialPower, CardSituation.NEUTRAL, null, new CardEffects());
    }

    private Paladin() {
    }

    @Override
    @JsonIgnore
    public String getName() {
        return "Paladin";
    }

    @Override
    @JsonIgnore
    public List<Card> getAllCardsOfThisClass() {
        return allCards;
    }

    @Override
    public Paladin cloned() {
        List<Deck> decks = new ArrayList<>();
        for (Deck myDeck : getMyDecks()) {
            decks.add(myDeck.cloned());
        }
        return new Paladin(decks, getHP(), getHeroPower().cloned(), getSpecialPowerDescription(), getSituation(),
                (getWeapon() != null) ? getWeapon().cloned() : null, this.getSpecialPower().cloned());
    }
}
