package models.cards;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import enums.CardSituation;
import enums.Class;
import enums.Rarity;
import enums.Type;
import logic.CardEffects;
import models.Card;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class Mission extends Card implements Serializable {

    private int goal;
    private int progress;
    private boolean accomplished;

    public Mission(int mana, int cardCost, String name, String description, Rarity cardRarity, Class cardClass, int goal) {

        this(mana, cardCost, name, description, cardRarity, cardClass, 0, new CardEffects(), CardSituation.NEUTRAL, goal);
    }

    public Mission(int mana, int cardCost, String name, String description, Rarity cardRarity, Class cardClass,
                   int usedTimes, CardEffects effects, CardSituation situation, int goal) {

        super(mana, cardCost, name, description, cardRarity, cardClass, Type.Mission, usedTimes, effects, situation);

        this.goal = goal;
        this.progress = 0;
        this.accomplished = false;
    }

    private Mission() { }

    private void checkIfAccomplished() {
        setAccomplished(progress >= goal);
    }

    public boolean isAccomplished() {
        checkIfAccomplished();
        return accomplished;
    }

    @Override
    public Mission cloned() {
        return new Mission(getMana(), getCardCost(), getName(), getDescription(), getCardRarity(), getCardClass(),
                getUsedTimes(), getCardEffects().cloned(), CardSituation.NEUTRAL, goal);
    }


    public int getGoal() {
        return goal;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    private void setAccomplished(boolean accomplished) {
        this.accomplished = accomplished;
    }
}
