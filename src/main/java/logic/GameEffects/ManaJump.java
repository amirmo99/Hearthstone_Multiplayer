package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import abstracts.Prototype;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardFilter;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class ManaJump extends GameEffect implements Prototype {

    private int addedMana;

    public ManaJump(int addedMana) {
        this.addedMana = addedMana;
        this.filter = new CardFilter();
    }

    private ManaJump() {}

    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public GameEffect cloned() {
        return new ManaJump(addedMana);
    }

    public int getAddedMana() {
        return addedMana;
    }

    @Override
    public String toString() {
        return "ManaJump{" +
                "addedMana=" + addedMana +
                ", sourcePlayerIndex=" + sourcePlayerIndex +
                '}';
    }
}
