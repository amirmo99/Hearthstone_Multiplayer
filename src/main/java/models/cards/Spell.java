package models.cards;//import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.fasterxml.jackson.annotation.*;
import enums.CardSituation;
import enums.Class;
import enums.Rarity;
import enums.Type;
import logic.CardEffects;
import models.Card;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class Spell extends Card implements Serializable {


    public Spell(int mana, int cardCost, String name, String description, Rarity cardRarity, Class cardClass,
                 int usedTimes) {
        this(mana, cardCost, name, description, cardRarity, cardClass, usedTimes, new CardEffects(), CardSituation.NEUTRAL);
    }

    public Spell(int mana, int cardCost, String name, String description, Rarity cardRarity, Class cardClass,
                 int usedTimes, CardEffects effects, CardSituation situation) {
        super(mana, cardCost, name, description, cardRarity, cardClass, Type.Spell, usedTimes, effects, situation);
    }

    public Spell() {

    }

    @Override
    public Spell cloned() {
        return new Spell(getMana(), getCardCost(), getName(), getDescription(), getCardRarity(),
                getCardClass(), getUsedTimes(), getCardEffects().cloned(), getSituation());
    }

}
