package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import abstracts.Prototype;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class ReviveDead extends GameEffect implements Prototype {

    public ReviveDead() { }


    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public GameEffect cloned() {
        return new ReviveDead();
    }

    @Override
    public String toString() {
        return "ReviveDead{" +
                "sourcePlayerIndex=" + sourcePlayerIndex +
                '}';
    }
}
