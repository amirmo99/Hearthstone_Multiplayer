package models.cards;

import abstracts.Defender;
import com.fasterxml.jackson.annotation.*;
import enums.CardSituation;
import enums.Class;
import enums.Rarity;
import enums.Type;
import logic.CardEffects;
import models.Card;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class Weapon extends Card implements Serializable {

    private int durability;
    private int attack;
    private int remainedAttacks;

    public Weapon(int mana, int cardCost, String name, String description, Rarity cardRarity, Class cardClass,
                  int usedTimes, int durability, int attack) {
        this(mana, cardCost, name, description, cardRarity, cardClass, usedTimes, durability, attack,
                new CardEffects(), CardSituation.NEUTRAL, 0);
    }

    public Weapon(int mana, int cardCost, String name, String description, Rarity cardRarity, Class cardClass,
                  int usedTimes, int durability, int attack, CardEffects effects, CardSituation situation, int remainedAttacks) {
        super(mana, cardCost, name, description, cardRarity, cardClass, Type.Weapon, usedTimes, effects, situation);
        this.durability = durability;
        this.attack = attack;
        this.remainedAttacks = remainedAttacks;
    }

    public Weapon() {

    }

    public void doAttack(Defender defender) {
        durability--;
        remainedAttacks--;
    }

    @Override
    public Weapon cloned() {
        return new Weapon(getMana(), getCardCost(), getName(), getDescription(), getCardRarity(), getCardClass(),
                getUsedTimes(), durability, attack, getCardEffects().cloned(), getSituation(), remainedAttacks);
    }

    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getRemainedAttacks() {
        return remainedAttacks;
    }

    public void resetAttacks() {
        remainedAttacks = 1;
    }

    public void setRemainedAttacks(int remainedAttacks) {
        this.remainedAttacks = remainedAttacks;
    }
}
