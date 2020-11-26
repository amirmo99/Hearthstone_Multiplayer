package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardFilter;
import models.Card;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class Summon extends GameEffect implements Serializable {

    public Summon(CardFilter filter, int repeat) {
        this.filter = filter;
        this.repeat = repeat;
    }

    private Summon() {}

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getRepeat() {
        return repeat;
    }

    @Override
    public void accept(GameEffectVisitor visitor) {
        for (int i = 0; i < repeat; i++) {
            visitor.visit(this);
        }
    }

    @Override
    public Summon cloned() {
        return new Summon(filter.cloned(), repeat);
    }

    @Override
    public String toString() {
        return "Summon{" +
                "repeat=" + repeat +
                ", sourcePlayerIndex=" + sourcePlayerIndex +
                ", filter=" + filter.toString() +
                '}';
    }
}
