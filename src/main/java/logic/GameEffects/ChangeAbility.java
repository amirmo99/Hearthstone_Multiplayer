package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import enums.MinionAbility;
import logic.CardFilter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class ChangeAbility extends GameEffect implements Serializable {

    private ArrayList<MinionAbility> addedAbilities;
    private ArrayList<MinionAbility>  removedAbilities;

    private ChangeAbility() {}

    public ChangeAbility(CardFilter filter, ArrayList<MinionAbility>  addedAbilities, ArrayList<MinionAbility>  removedAbilities) {
        this.filter = filter;
        this.addedAbilities = addedAbilities;
        this.removedAbilities = removedAbilities;
    }

    public ArrayList<MinionAbility>  getAddedAbilities() {
        return addedAbilities;
    }

    public ArrayList<MinionAbility>  getRemovedAbilities() {
        return removedAbilities;
    }

    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ChangeAbility cloned() {
        return new ChangeAbility(filter.cloned(), new ArrayList<>(addedAbilities), new ArrayList<>(removedAbilities));
    }

    @Override
    public String toString() {
        return "ChangeAbility{" +
                "addedAbilities=" + addedAbilities +
                ", removedAbilities=" + removedAbilities +
                ", sourcePlayerIndex=" + sourcePlayerIndex +
                ", filter=" + filter.toString() +
                '}';
    }
}
