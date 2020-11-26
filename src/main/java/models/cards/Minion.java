package models.cards;//import org.codehaus.jackson.annotate.JsonTypeInfo;

import abstracts.Defender;
import abstracts.Attacker;
import abstracts.GameEffectVisitor;
import com.fasterxml.jackson.annotation.*;
import enums.*;
import enums.Class;
import logic.CardEffects;
import models.Card;

import java.io.Serializable;
import java.util.HashSet;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class Minion extends Card implements Attacker, Defender, Serializable {

    private int attack, HP;
    private int mainAttack, mainHP;
    private HashSet<MinionAbility> abilities, mainAbilities;
    private int remainedAttacks;

    public Minion(int mana, int cardCost, String name, String description, Rarity cardRarity, Class cardClass, int usedTimes, int attack, int HP) {
        this(mana, cardCost, name, description, cardRarity, cardClass, usedTimes,attack, HP,
                new CardEffects(), CardSituation.NEUTRAL, new HashSet<>(), 0);
    }

    public Minion(int mana, int cardCost, String name, String description, Rarity cardRarity, Class cardClass,
                  int usedTimes, int attack, int HP, CardEffects effects, CardSituation situation, HashSet<MinionAbility> abilities, int remainedAttacks) {
        super(mana, cardCost, name, description, cardRarity, cardClass, Type.Minion, usedTimes, effects, situation);
        this.mainAbilities = new HashSet<>(abilities);
        this.abilities = abilities;
        this.mainAttack = attack;
        this.attack = attack;
        this.mainHP = HP;
        this.HP = HP;
        this.remainedAttacks = remainedAttacks;
    }

    private Minion() {

    }

    @Override
    public void doDefend(Attacker attacker, GameEffectVisitor visitor) {
        if (attacker instanceof Minion && ((Minion) attacker).getAbilities().contains(MinionAbility.POISONOUS)) {
            takeDamage(getHP(), visitor);
        }
        else {
            takeDamage(attacker.getAttack(), visitor);
        }
    }

    @Override
    public void takeDamage(int damage, GameEffectVisitor visitor) {
        if (!abilities.contains(MinionAbility.DIVINE_SHIELD)) {
            setHP(getHP() - damage);
            if (damage > 0)
                this.getCardEffects().getOnTakingDamage().accept(visitor);
        }
        else
            abilities.remove(MinionAbility.DIVINE_SHIELD);
    }

    @Override
    public void doAttack(Defender defender, GameEffectVisitor visitor) {
        if (defender instanceof Minion) {
            Attacker attacker = (Attacker) defender;
            doDefend(attacker, visitor);
        }
        abilities.remove(MinionAbility.STEALTH);
        abilities.remove(MinionAbility.CHARGE);
        abilities.remove(MinionAbility.RUSH);
        remainedAttacks-- ;
    }

    @Override
    public Minion cloned() {
        return new Minion(getMana(), getCardCost(), getName(), getDescription(), getCardRarity(), getCardClass(),
                getUsedTimes(), mainAttack, mainHP, getCardEffects().cloned(), CardSituation.NEUTRAL, new HashSet<>(mainAbilities), 0);

    }

    public void resetAttacks() {
        if (abilities.contains(MinionAbility.WIND_FURY))
            remainedAttacks = 2;
        else
            remainedAttacks = 1;
    }

    @JsonIgnore
    public boolean isDamaged() {
        return HP < mainHP;
    }

    @JsonIgnore
    public boolean isHealed() {
        return HP > mainHP;
    }

    @JsonIgnore
    public boolean isEmpowered() {
        return mainAttack < attack;
    }
    //Getters and Setters

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    @Override
    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public int getMainAttack() {
        return mainAttack;
    }

    public int getMainHP() {
        return mainHP;
    }

    public HashSet<MinionAbility> getAbilities() {
        return abilities;
    }

    @Override
    public int getRemainedAttacks() {
        return remainedAttacks;
    }

    public void setRemainedAttacks(int remainedAttacks) {
        this.remainedAttacks = remainedAttacks;
    }

    public void setMainAttack(int mainAttack) {
        this.mainAttack = mainAttack;
    }

    public void setMainHP(int mainHP) {
        this.mainHP = mainHP;
    }

    public HashSet<MinionAbility> getMainAbilities() {
        return mainAbilities;
    }

}
