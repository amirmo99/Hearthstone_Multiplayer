package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import abstracts.Prototype;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardFilter;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class HeroPowerModifier extends GameEffect implements Prototype {

    private int addedMana, usageNumber;

    public HeroPowerModifier(int addedMana, int usageNumber) {
        this.addedMana = addedMana;
        this.usageNumber = usageNumber;
        this.filter = new CardFilter();
    }

    private HeroPowerModifier() { }

    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public HeroPowerModifier cloned() {
        return new HeroPowerModifier(addedMana, usageNumber);
    }

    public int getAddedMana() {
        return addedMana;
    }

    public int getUsageNumber() {
        return usageNumber;
    }

    @Override
    public String toString() {
        return "HeroPowerModifier{" +
                "addedMana=" + addedMana +
                ", usageNumber=" + usageNumber +
                ", sourcePlayerIndex=" + sourcePlayerIndex +
                '}';
    }
}
