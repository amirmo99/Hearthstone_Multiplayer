package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import abstracts.Prototype;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import enums.MissionType;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class MissionHandler extends GameEffect implements Prototype {

    private MissionType missionType;

    public MissionHandler(MissionType missionType) {
        this.missionType = missionType;
    }

    private MissionHandler() { }


    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public MissionHandler cloned() {
        return new MissionHandler(missionType);
    }

    public MissionType getMissionType() {
        return missionType;
    }

    @Override
    public String toString() {
        return "MissionHandler{" +
                "missionType=" + missionType +
                ", sourcePlayerIndex=" + sourcePlayerIndex +
                '}';
    }
}
