package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardFilter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class RelatedEffects extends GameEffect implements Serializable {

    private ArrayList<GameEffect> effects;

    public RelatedEffects() { }

    public RelatedEffects(ArrayList<GameEffect> effects, CardFilter filter) {
        this.effects = effects;
        this.filter = filter;
    }

    public ArrayList<GameEffect> getEffects() {
        return effects;
    }

    @JsonIgnore
    @Override
    public void setSourcePlayerIndex(int index) {
        super.setSourcePlayerIndex(index);
        for (GameEffect effect : effects) {
            effect.setSourcePlayerIndex(index);
        }
    }

    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public RelatedEffects cloned() {
        ArrayList<GameEffect> effects = new ArrayList<>();
        for (GameEffect effect : this.effects) {
            effects.add(effect.cloned());
        }
        return new RelatedEffects(effects, filter.cloned());
    }

    @Override
    public String toString() {
        return "RelatedEffects{" +
                "effects=" + Arrays.toString(effects.toArray()) +
                ", sourcePlayerIndex=" + sourcePlayerIndex +
                ", filter=" + filter.toString() +
                '}';
    }
}
