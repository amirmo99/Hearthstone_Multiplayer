package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import abstracts.Prototype;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class EffectList extends GameEffect implements Serializable, Prototype {

    private List<GameEffect> effects = new ArrayList<>();
    private boolean chooseRandom;

    public EffectList(List<GameEffect> effects, boolean chooseRandom) {
        this.effects = effects;
        this.chooseRandom = chooseRandom;
    }

    public EffectList(List<GameEffect> effects) {
        this(effects, false);
    }

    public EffectList() {
    }

    public List<GameEffect> getEffects() {
        return effects;
    }

    @Override
    public void setSourcePlayerIndex(int index) {
        super.setSourcePlayerIndex(index);
        for (GameEffect effect : effects) {
            effect.setSourcePlayerIndex(index);
        }
    }

    @Override
    public void accept(GameEffectVisitor visitor) {
        if (!chooseRandom) {
            for (GameEffect effect : effects) {
                effect.accept(visitor);
            }
        }
        else if (effects.size() > 0) {
            effects.get(new Random().nextInt(effects.size())).accept(visitor);
        }
    }

    @Override
    public EffectList cloned() {
        ArrayList<GameEffect> effects = new ArrayList<>();
        for (GameEffect effect : this.effects) {
            effects.add(effect.cloned());
        }

        return new EffectList(effects);
    }

    public boolean isChooseRandom() {
        return chooseRandom;
    }

    public void setChooseRandom(boolean chooseRandom) {
        this.chooseRandom = chooseRandom;
    }

    @Override
    public String toString() {
        return "EffectList{" +
                "effects=" + Arrays.toString(effects.toArray()) +
                ", chooseRandom=" + chooseRandom +
                ", sourcePlayerIndex=" + sourcePlayerIndex +
                '}';
    }
}
