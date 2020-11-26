package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import abstracts.Prototype;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardFilter;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class Steal extends GameEffect implements Prototype {

    private int number;

    public Steal(int number) {
        this.number = number;
        this.filter = new CardFilter();
    }

    private Steal() {}

    public int getNumber() {
        return number;
    }

    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Steal cloned() {
        return new Steal(number);
    }

    @Override
    public String toString() {
        return "Steal{" +
                "number=" + number +
                ", sourcePlayerIndex=" + sourcePlayerIndex +
                ", filter=" + filter.toString() +
                '}';
    }
}
