package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardFilter;
import models.Card;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class AddToHand extends GameEffect implements Serializable {

    public AddToHand(CardFilter filter) {
        this.filter = filter;
    }

    private AddToHand() {}

    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AddToHand cloned() {
        return new AddToHand(filter.cloned());
    }

    @Override
    public String toString() {
        return "AddToHand{" +
                "sourcePlayerIndex=" + sourcePlayerIndex +
                ", filter=" + filter.toString() +
                '}';
    }
}
