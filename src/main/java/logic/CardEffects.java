package logic;

import abstracts.Prototype;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.GameEffects.EffectList;

import java.io.Serializable;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class CardEffects implements Prototype, Serializable {

    private EffectList passive;

    private EffectList battleCry;
    private EffectList deathRattle;
    private EffectList overKill;
    private EffectList combo;

    private EffectList onExit;
    private EffectList onTurnEnd;
    private EffectList onTurnBegin;
    private EffectList onDraw;
    private EffectList onPlay;
    private EffectList onOpponentPlay;
    private EffectList onSummon;
    private EffectList onOpponentSummon;
    private EffectList onAnyCharacterHeal;
    private EffectList onTakingDamage;
    private EffectList onAnyMinionDeath;
    private EffectList onCardReceive;
    private EffectList onTransform;

    private EffectList missionReward;

    private int sourcePlayer;

    public CardEffects() {
        passive = new EffectList();

        battleCry = new EffectList();
        deathRattle = new EffectList();
        overKill = new EffectList();
        combo = new EffectList();

        onExit = new EffectList();
        onTurnEnd = new EffectList();
        onTurnBegin = new EffectList();
        onDraw = new EffectList();
        onPlay = new EffectList();
        onOpponentPlay = new EffectList();
        onSummon = new EffectList();
        onAnyCharacterHeal = new EffectList();
        onTakingDamage = new EffectList();
        onOpponentSummon = new EffectList();
        onOpponentPlay = new EffectList();
        onAnyMinionDeath = new EffectList();
        onCardReceive = new EffectList();
        onTransform = new EffectList();

        missionReward = new EffectList();
    }

    private CardEffects(EffectList battleCry, EffectList deathRattle, EffectList overKill, EffectList onExit, EffectList combo,
                        EffectList onTurnEnd, EffectList onTurnBegin, EffectList onDraw, EffectList onPlay, EffectList onSummon, EffectList onCharacterHeal,
                        EffectList onTakingDamage, EffectList onOpponentSummon, EffectList passive, EffectList onOpponentPlay,
                        EffectList onAnyMinionDeath, EffectList missionReward, EffectList onCardReceive, EffectList onTransform) {
        this.battleCry = battleCry;
        this.deathRattle = deathRattle;
        this.overKill = overKill;
        this.onExit = onExit;
        this.combo = combo;
        this.onTurnEnd = onTurnEnd;
        this.onTurnBegin = onTurnBegin;
        this.onDraw = onDraw;
        this.onPlay = onPlay;
        this.onSummon = onSummon;
        this.onAnyCharacterHeal = onCharacterHeal;
        this.onTakingDamage = onTakingDamage;
        this.onOpponentSummon = onOpponentSummon;
        this.passive = passive;
        this.onOpponentPlay = onOpponentPlay;
        this.onAnyMinionDeath = onAnyMinionDeath;
        this.missionReward = missionReward;
        this.onCardReceive = onCardReceive;
        this.onTransform = onTransform;
    }

    public List<EffectList> allLists() {
        return List.of(battleCry, deathRattle, overKill, combo, onTurnBegin, onTurnEnd, onDraw, onPlay, onSummon, onExit,
                onAnyCharacterHeal, onTakingDamage, onOpponentSummon, passive, onOpponentPlay, onAnyMinionDeath,
                missionReward, onCardReceive, onTransform);
    }

    @JsonIgnore
    public void setSourcePlayerIndex(int index) {
        this.sourcePlayer = index;
        for (EffectList effectList : allLists()) {
            effectList.setSourcePlayerIndex(index);
        }
    }

    @Override
    public CardEffects cloned() {
        CardEffects cloned = new CardEffects(battleCry.cloned(), deathRattle.cloned(), overKill.cloned(), onExit.cloned(), combo.cloned(),
                onTurnEnd.cloned(), onTurnBegin.cloned(), onDraw.cloned(), onPlay.cloned(), onSummon.cloned(), onAnyCharacterHeal.cloned(),
                onTakingDamage.cloned(), onOpponentSummon.cloned(), passive.cloned(), onOpponentPlay.cloned(), onAnyMinionDeath.cloned(),
                missionReward.cloned(), onCardReceive.cloned(), onTransform.cloned());

        cloned.setSourcePlayerIndex(sourcePlayer);
        return cloned;
    }

    public EffectList getBattleCry() {
        return battleCry;
    }

    public EffectList getDeathRattle() {
        return deathRattle;
    }

    public EffectList getCombo() {
        return combo;
    }

    public EffectList getOverKill() {
        return overKill;
    }

    public EffectList getOnTurnEnd() {
        return onTurnEnd;
    }

    public EffectList getOnTurnBegin() {
        return onTurnBegin;
    }

    public EffectList getOnDraw() {
        return onDraw;
    }

    public EffectList getOnSummon() {
        return onSummon;
    }

    public EffectList getOnExit() {
        return onExit;
    }

    public EffectList getOnAnyCharacterHeal() {
        return onAnyCharacterHeal;
    }

    public EffectList getOnTakingDamage() {
        return onTakingDamage;
    }

    public EffectList getOnOpponentSummon() {
        return onOpponentSummon;
    }

    public int getSourcePlayer() {
        return sourcePlayer;
    }

    public EffectList getPassive() {
        return passive;
    }

    public EffectList getOnPlay() {
        return onPlay;
    }

    public EffectList getOnOpponentPlay() {
        return onOpponentPlay;
    }

    public EffectList getOnAnyMinionDeath() {
        return onAnyMinionDeath;
    }

    public EffectList getMissionReward() {
        return missionReward;
    }

    public EffectList getOnCardReceive() {
        return onCardReceive;
    }

    public EffectList getOnTransform() {
        return onTransform;
    }
}
