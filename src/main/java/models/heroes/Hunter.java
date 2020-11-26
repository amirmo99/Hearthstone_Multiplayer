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
import java.util.stream.Collectors;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class Hunter extends Heroes implements Serializable {

    private static final List<Card> allCards = Card.getAllCardsOfClass(Class.Hunter);

    public Hunter(List<Deck> myDecks, int HP, HeroPower heroPower, String specialPower) {
        this(myDecks, HP, heroPower, specialPower, CardSituation.NEUTRAL, null, new CardEffects());
    }

    private Hunter(List<Deck> decks, int HP, HeroPower heroPower, String specialPower, CardSituation situation,
                  Weapon weapon, CardEffects effectList) {
        super(decks, HP, 0, heroPower, specialPower, situation, weapon, effectList);
        name = "Hunter";
    }

    private Hunter() {
    }

    @Override
    @JsonIgnore
    public String getName() {
        return "Hunter";
    }

    @Override
    @JsonIgnore
    public List<Card> getAllCardsOfThisClass() {
        return allCards;
    }

    @Override
    public Hunter cloned() {
        List<Deck> decks = new ArrayList<>();
        for (Deck myDeck : getMyDecks()) {
            decks.add(myDeck.cloned());
        }
        return new Hunter(decks, getHP(), getHeroPower().cloned(), getSpecialPowerDescription(), getSituation(),
                (getWeapon() != null) ? getWeapon().cloned() : null, this.getSpecialPower().cloned());
    }
}
