package abstracts;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardFilter;
import logic.GameEffects.*;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "logic.GameEffects.AddCost", value = AddCost.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.AddToDeck", value = AddToDeck.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.AddToHand", value = AddToHand.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.ChangeAbility", value = ChangeAbility.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.ChangeHpAttack", value = ChangeHpAttack.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.Discover", value = Discover.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.DrawCard", value = DrawCard.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.EffectList", value = EffectList.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.GroupEffect", value = RelatedEffects.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.Summon", value = Summon.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.Transform", value = Transform.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.RestoreLife", value = RestoreLife.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.Attack", value = Attack.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.HeroPowerModifier", value = HeroPowerModifier.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.ManaJump", value = ManaJump.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.MultipleDraw", value = MultipleDraw.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.HeroPowerChanger", value = HeroPowerChanger.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.Steal", value = Steal.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.DefenseAdder", value = DefenseAdder.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.MissionHandler", value = MissionHandler.class),
        @JsonSubTypes.Type(name = "logic.GameEffects.ReviveDead", value = ReviveDead.class)
})
public abstract class GameEffect implements Prototype, Serializable{

    protected int repeat = 1;
    protected int sourcePlayerIndex = 0;

    protected CardFilter filter;

    public abstract void accept(GameEffectVisitor visitor);

    public CardFilter getFilter() {
        return filter;
    }

    public void setFilter(CardFilter filter) { this.filter = filter; }

    @Override
    public abstract GameEffect cloned();

    public int getSourcePlayerIndex() {
        return sourcePlayerIndex;
    }

    public void setSourcePlayerIndex(int sourcePlayerIndex) {
        this.sourcePlayerIndex = sourcePlayerIndex;
    }


}



