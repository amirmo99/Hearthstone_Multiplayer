package models;

import Util.PassiveActionFactory;
import Util.Tools;
import abstracts.ModelAction;
import abstracts.Prototype;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardEffects;
import logic.GameEffects.EffectList;

import java.io.Serializable;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "models.InfoPassive")
public class InfoPassive extends GameModel implements Prototype, Serializable {

    @JsonIgnore
    private static final List<InfoPassive> allInfoPassives = Tools.getAllPassives();

    private String description;
    private CardEffects effects;

    public InfoPassive(String name, String description, CardEffects effects) {
        this.name = name;
        this.description = description;
        this.effects = effects;
    }

    public InfoPassive(String name, String description) {
        this(name, description, new CardEffects());
    }

    private InfoPassive() {
    }

    @Override
    public CardEffects findEffects() {
        return effects;
    }

    @Override
    public InfoPassive cloned() {
        return new InfoPassive(name, description, effects.cloned());
    }

    @JsonIgnore
    public static List<InfoPassive> getAllInfoPassives() {
        return allInfoPassives;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CardEffects getEffects() {
        return effects;
    }
}
