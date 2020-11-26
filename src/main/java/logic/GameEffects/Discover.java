package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardFilter;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class Discover extends GameEffect implements Serializable {

    public Discover(CardFilter filter) {
        this.filter = filter;
    }

    public Discover() {}

    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public Discover cloned() {
        return new Discover(filter.cloned());
    }

    @Override
    public String toString() {
        return "Discover{" +
                "sourcePlayerIndex=" + sourcePlayerIndex +
                ", filter=" + filter +
                '}';
    }
}
