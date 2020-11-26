package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardFilter;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class DrawCard extends GameEffect implements Serializable {

    private int number;
    private boolean discardingBadCards;

    public DrawCard(int number, boolean discardBadCards, CardFilter filter) {
        this.number = number;
        this.discardingBadCards = discardBadCards;
        this.filter = filter;
    }

    private DrawCard() {}

    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DrawCard cloned() {
        return new DrawCard(number, discardingBadCards, filter.cloned());
    }

    public int getNumber() {
        return number;
    }

    public boolean isDiscardingBadCards() {
        return discardingBadCards;
    }

    @Override
    public String toString() {
        return "DrawCard{" +
                "number=" + number +
                ", discardingBadCards=" + discardingBadCards +
                ", sourcePlayerIndex=" + sourcePlayerIndex +
                ", filter=" + filter.toString() +
                '}';
    }
}
