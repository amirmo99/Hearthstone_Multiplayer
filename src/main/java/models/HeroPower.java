package models;

import abstracts.*;
import com.fasterxml.jackson.annotation.*;
import enums.CardSituation;
import logic.CardEffects;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class HeroPower extends GameModel implements Prototype, Serializable {

    private int mana;
    private int actionEachTurn;
    private int actionsLeft;
    private String description;
    private boolean isPassive = false;
    private CardEffects effects;
    private CardSituation situation;

    public HeroPower(String name, int mana, int actionEachTurn, int actionsLeft, String description, boolean isPassive, CardEffects effects, CardSituation situation) {
        this.name = name;
        this.mana = mana;
        this.actionEachTurn = actionEachTurn;
        this.actionsLeft = actionsLeft;
        this.description = description;
        this.isPassive = isPassive;
        this.effects = effects;
        this.situation = situation;
    }

    public HeroPower(String name, int mana, String description, int actionEachTurn, boolean isPassive) {
        this(name, mana, actionEachTurn, 1, description, isPassive, new CardEffects(), CardSituation.NEUTRAL);
    }

    public HeroPower() {
    }

    public void reset() {
        actionsLeft = actionEachTurn;
    }

    @Override
    public HeroPower cloned() {
        return new HeroPower(name, mana, actionEachTurn, actionsLeft, description, isPassive, effects.cloned(), situation);
    }

    @Override
    public CardEffects findEffects() {
        return effects;
    }

    // Getters and Setters

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

    public int getActionEachTurn() {
        return actionEachTurn;
    }

    public void setActionEachTurn(int actionEachTurn) {
        this.actionEachTurn = actionEachTurn;
    }

    public int getActionsLeft() {
        return actionsLeft;
    }

    public void setActionsLeft(int actionsLeft) {
        this.actionsLeft = actionsLeft;
    }

    public CardSituation getSituation() {
        return situation;
    }

    public void setSituation(CardSituation situation) {
        this.situation = situation;
    }

    public boolean isPassive() {
        return isPassive;
    }

    public void setPassive(boolean passive) {
        isPassive = passive;
    }

    public CardEffects getEffects() {
        return effects;
    }

    public void setEffects(CardEffects effects) {
        this.effects = effects;
    }
}
