package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardFilter;

import java.io.Serializable;
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class RestoreLife extends GameEffect implements Serializable {

    private int life;

    public RestoreLife(CardFilter filter, int life) {
        this.filter = filter;
        this.life = life;
    }

    private RestoreLife() { }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public RestoreLife cloned() {
        return new RestoreLife(filter.cloned(), life);
    }

    @Override
    public String toString() {
        return "RestoreLife{" +
                "life=" + life +
                ", sourcePlayerIndex=" + sourcePlayerIndex +
                ", filter=" + filter.toString() +
                '}';
    }
}
