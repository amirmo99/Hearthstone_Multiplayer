package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardFilter;
import models.Card;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class Transform extends GameEffect implements Serializable {

    private CardFilter targetFilter;

    private Transform() {}

    public Transform(CardFilter filter, CardFilter targetFilter) {
        this.filter = filter;
        this.targetFilter = targetFilter;
    }

    public CardFilter getTargetFilter() {
        return targetFilter;
    }

    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Transform cloned() {
        return new Transform(filter.cloned(), targetFilter.cloned());
    }

    @Override
    public String toString() {
        return "Transform{" +
                "targetFilter=" + targetFilter.toString() +
                ", sourcePlayerIndex=" + sourcePlayerIndex +
                ", filter=" + filter.toString() +
                '}';
    }
}