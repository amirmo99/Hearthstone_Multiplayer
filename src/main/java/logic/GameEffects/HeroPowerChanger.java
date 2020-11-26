package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import abstracts.Prototype;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardFilter;
import models.HeroPower;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class HeroPowerChanger extends GameEffect implements Prototype {

    private HeroPower targetHeroPower;

    public HeroPowerChanger(HeroPower targetHeroPower) {
        this.targetHeroPower = targetHeroPower;
        filter = new CardFilter();
    }

    private HeroPowerChanger() {}

    public HeroPower getTargetHeroPower() {
        return targetHeroPower;
    }

    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public HeroPowerChanger cloned() {
        return new HeroPowerChanger(targetHeroPower.cloned());
    }

    @Override
    public String toString() {
        return "HeroPowerChanger{" +
                "targetHeroPower=" + targetHeroPower +
                ", sourcePlayerIndex=" + sourcePlayerIndex +
                '}';
    }
}
