package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardFilter;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class AddCost extends GameEffect implements Serializable {

    private int cost;

    public AddCost(int cost, CardFilter filter) {
        this.cost = cost;
        this.filter = filter;
    }

    private AddCost() {}

    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AddCost cloned() {
        return new AddCost(cost, filter.cloned());
    }

    public int getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "AddCost{" +
                "cost=" + cost +
                ", sourcePlayerIndex=" + sourcePlayerIndex +
                ", filter=" + filter.toString() +
                '}';
    }
}
