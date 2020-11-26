package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardEffects;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "models.Card", value = Card.class),
        @JsonSubTypes.Type(name = "models.Heroes", value = Heroes.class),
        @JsonSubTypes.Type(name = "models.HeroPower", value = HeroPower.class),
        @JsonSubTypes.Type(name = "models.InfoPassive", value = InfoPassive.class)
})
public abstract class GameModel implements Serializable {
    protected String name;

    public abstract CardEffects findEffects();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public int getSourcePlayerIndex() {
        return findEffects().getSourcePlayer();
    }
}
