package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardFilter;
import models.Card;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class AddToDeck extends GameEffect implements Serializable {

    public AddToDeck(CardFilter filter) {
        this.filter = filter;
    }

    private AddToDeck() {}

    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AddToDeck cloned() {
        return new AddToDeck(filter.cloned());
    }

    @Override
    public String toString() {
        return "AddToDeck{" +
                "sourcePlayerIndex=" + sourcePlayerIndex +
                ", filter=" + filter.toString() +
                '}';
    }
}