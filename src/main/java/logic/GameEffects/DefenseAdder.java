package logic.GameEffects;

import abstracts.Defender;
import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import abstracts.Prototype;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardFilter;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class DefenseAdder extends GameEffect implements Prototype {

    private int addedDefense;

    public DefenseAdder(int addedDefense) {
        this.addedDefense = addedDefense;
        this.filter = new CardFilter();
    }

    private DefenseAdder() {}

    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DefenseAdder cloned() {
        return new DefenseAdder(addedDefense);
    }

    public int getAddedDefense() {
        return addedDefense;
    }

    @Override
    public String toString() {
        return "DefenseAdder{" +
                "addedDefense=" + addedDefense +
                ", sourcePlayerIndex=" + sourcePlayerIndex +
                ", filter=" + filter.toString() +
                '}';
    }
}
