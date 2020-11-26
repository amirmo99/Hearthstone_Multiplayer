package logic.GameEffects;

import abstracts.GameEffect;
import abstracts.GameEffectVisitor;
import abstracts.Prototype;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import logic.CardFilter;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
public class MultipleDraw extends GameEffect implements Prototype {

    private int drawEachTurn;

    public MultipleDraw(int drawEachTurn) {
        this.drawEachTurn = drawEachTurn;
        this.filter = new CardFilter();
    }

    private MultipleDraw() {}

    public int getDrawEachTurn() {
        return drawEachTurn;
    }

    @Override
    public void accept(GameEffectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public MultipleDraw cloned() {
        return new MultipleDraw(drawEachTurn);
    }

    @Override
    public String toString() {
        return "MultipleDraw{" +
                "drawEachTurn=" + drawEachTurn +
                ", sourcePlayerIndex=" + sourcePlayerIndex +
                '}';
    }
}
