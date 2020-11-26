package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import abstracts.Prototype;
import logic.CardFilter;

public class DoubleSpellPowers extends GameEffect implements Prototype {

    public DoubleSpellPowers() {
        this.filter = new CardFilter();
    }

    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public DoubleSpellPowers cloned() {
        return new DoubleSpellPowers();
    }

    @Override
    public String toString() {
        return "DoubleSpellPowers{}";
    }
}
