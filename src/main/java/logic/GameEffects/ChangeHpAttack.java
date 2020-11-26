package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardFilter;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class ChangeHpAttack extends GameEffect implements Serializable {

    private int life, attack;
    private CardFilter targetHP, targetAttack;

    public ChangeHpAttack(int hp, int attack, CardFilter filter, int repeat, CardFilter targetHP, CardFilter targetAttack, int sourcePlayer) {
        this.life = hp;
        this.attack = attack;
        this.filter = filter;
        this.targetHP = targetHP;
        this.targetAttack = targetAttack;
        setRepeat(repeat);
        setSourcePlayerIndex(sourcePlayer);
    }

    public ChangeHpAttack(int hp, int attack, CardFilter filter, int repeat) {
        this(hp, attack, filter, repeat, null, null, 0);
    }

    public ChangeHpAttack(int hp, int attack, CardFilter filter) {
        this(hp, attack, filter, 1, null, null, 0);
    }

    private ChangeHpAttack() { }

    @Override
    public void accept(GameEffectVisitor visitor) {
        for (int i = 0; i < getRepeat(); i++) {
            visitor.visit(this);
        }
    }

    @Override
    public ChangeHpAttack cloned() {
        CardFilter target1 = (targetHP == null) ? null : targetHP.cloned();
        CardFilter target2 = (targetAttack == null) ? null : targetAttack.cloned();

        return new ChangeHpAttack(life, attack, filter.cloned(), repeat, target1, target2, getSourcePlayerIndex());
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getLife() {
        return life;
    }

    public int getAttack() {
        return attack;
    }

    public CardFilter getTargetHP() {
        return targetHP;
    }

    public void setTargetHP(CardFilter targetHP) {
        this.targetHP = targetHP;
    }

    public CardFilter getTargetAttack() {
        return targetAttack;
    }

    public void setTargetAttack(CardFilter targetAttack) {
        this.targetAttack = targetAttack;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    @Override
    public String toString() {
        return "ChangeHpAttack{" +
                "life=" + life +
                ", attack=" + attack +
                ", targetHP=" + targetHP +
                ", targetAttack=" + targetAttack +
                ", sourcePlayerIndex=" + sourcePlayerIndex +
                ", filter=" + filter.toString() +
                '}';
    }
}
