package logic;

import abstracts.Prototype;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import enums.CardGroup;
import enums.Class;
import enums.SelectingCardMethod;
import enums.Type;
import models.Card;
import models.GameModel;
import models.cards.Minion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@type")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class CardFilter implements Serializable, Prototype {

    private SelectingCardMethod selectingCardMethod;
    private CardGroup cardGroup;
    private Type[] type;
    private Class cardClass;
    private ArrayList<GameModel> sourceCard;

    public CardFilter() { }

    public CardFilter(Type[] type) {
        this.type = type;
    }

    public CardFilter(SelectingCardMethod method, CardGroup group) {
        this.selectingCardMethod = method;
        this.cardGroup = group;
    }

    public CardFilter(SelectingCardMethod method, CardGroup group, Type[] type, Class cardClass) {
        this.selectingCardMethod = method;
        this.cardGroup = group;
        this.type = type;
        this.cardClass = cardClass;
    }

    public CardFilter(Type[] type, CardGroup group) {
        this.type = type;
        this.cardGroup = group;
    }

    public CardFilter(SelectingCardMethod selectingCardMethod, CardGroup cardGroup, Type[] type, Class cardClass, ArrayList<GameModel> sourceCard) {
        this.selectingCardMethod = selectingCardMethod;
        this.cardGroup = cardGroup;
        this.type = type;
        this.cardClass = cardClass;
        this.sourceCard = sourceCard;
    }

    /////// Getters & Setters
    public SelectingCardMethod getSelectingCardMethod() {
        return selectingCardMethod;
    }

    public void setSelectingCardMethod(SelectingCardMethod selectingCardMethod) {
        this.selectingCardMethod = selectingCardMethod;
    }

    public CardGroup getCardGroup() {
        return cardGroup;
    }

    public void setCardGroup(CardGroup cardGroup) {
        this.cardGroup = cardGroup;
    }

    public Type[] getType() {
        return type;
    }

    public void setType(Type[] type) {
        this.type = type;
    }

    public ArrayList<GameModel> getSourceCard() {
        return sourceCard;
    }

    public void setSourceCard(ArrayList<GameModel> sourceCard) {
        this.sourceCard = sourceCard;
    }

    public Class getCardClass() {
        return cardClass;
    }

    public void setCardClass(Class cardClass) {
        this.cardClass = cardClass;
    }

    @Override
    public CardFilter cloned() {
        ArrayList<GameModel> models = null;
        if (sourceCard != null) {
            models = new ArrayList<>();
            for (GameModel gameModel : sourceCard) {
                if (gameModel instanceof Minion)
                    models.add(((Minion) gameModel).cloned());
                else
                    models.add(gameModel);
            }
        }

        return new CardFilter(selectingCardMethod, cardGroup, type, cardClass, models);
    }

    @Override
    public String toString() {
        return "CardFilter{" +
                "selectingCardMethod=" + selectingCardMethod +
                ", cardGroup=" + cardGroup +
                ", type=" + ((type != null) ? Arrays.toString(type): "null") +
                ", cardClass=" + cardClass +
                ", sourceCard=" + sourceCard +
                '}';
    }
}
