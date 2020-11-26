package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardFilter;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class Attack extends GameEffect implements Serializable {

    public Attack(CardFilter filter) {
        this.filter = filter;
    }

    private Attack() { }

    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public GameEffect cloned() {
        return new Attack(filter.cloned());
    }

    @Override
    public String toString() {
        return "Attack{" +
                "sourcePlayerIndex=" + sourcePlayerIndex +
                ", filter=" + filter.toString() +
                '}';
    }
}
