package Util;

import abstracts.GameEffect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import enums.SelectingCardMethod;
import enums.Type;
import logic.Player;
import models.Card;
import models.GameModel;
import models.cards.Minion;
import models.cards.Mission;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
public class DeckFile implements Serializable {

    private String[] player1Cards, player2Cards;

    private DeckFile() { }

    public DeckFile(Player player1, Player player2) {
        List<Card> cards1 = player1.getActiveDeck().getCards();
        List<Card> cards2 = player2.getActiveDeck().getCards();

        this.player1Cards = listNames(cards1);
        this.player2Cards = listNames(cards2);
    }

    private String[] listNames(List<Card> cards) {
        String[] strings = new String[cards.size()];
        for (int i = 0; i < cards.size(); i++) {
            strings[i] = cards.get(i).getName();
        }
        return strings;
    }

    public String[] getPlayer1Cards() {
        return player1Cards;
    }

    public String[] getPlayer2Cards() {
        return player2Cards;
    }

    @JsonIgnore
    public ArrayList<Card> getPlayer1CardsAsList() { return changeToList(player1Cards); }

    @JsonIgnore
    public ArrayList<Card> getPlayer2CardsAsList() { return changeToList(player2Cards); }

    private ArrayList<Card> changeToList(String[] cardsNames) {
        ArrayList<Card> cardArrayList = new ArrayList<>();
        Random random = new Random();

        for (String cardName : cardsNames) {
            Card writtenCard;
            writtenCard = translate(cardName.trim(), random);

            if (writtenCard == null) throw new NullPointerException("Card Name \"" + cardName + " Does Not Exist.");
            cardArrayList.add(writtenCard);
        }
        return cardArrayList;
    }

    private Card translate(String cardName, Random random) {
        List<Card> cards;

        if (cardName.equalsIgnoreCase("minion")) {
            cards = Card.getAllCardOfType(Type.Minion);
        } else if (cardName.equalsIgnoreCase("spell")) {
            cards = Card.getAllCardOfType(Type.Spell);
        } else if (cardName.equalsIgnoreCase("mission")) {
            cards = Card.getAllCardOfType(Type.Mission);
        } else if (cardName.equalsIgnoreCase("weapon")) {
            cards = Card.getAllCardOfType(Type.Weapon);
        } else if (Card.getCard(cardName) != null) {
            return Card.getCard(cardName);
        } else {
            return translateMission(cardName);
        }

        if (cards.size() == 0)
            return null;
        else
            return cards.get(random.nextInt(cards.size()));
    }

    private Card translateMission(String string) {
        try {
            String[] strings = string.split("->Reward:");
            Card card = Card.getCard(strings[0].trim()).cloned();
            changeMissionEffect((Mission) card, strings[1]);
            return card;
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    private void changeMissionEffect(Mission mission, String summonCard) {
        ArrayList<GameModel> cards = new ArrayList<>();

        Card summonedCard = Card.getCard(summonCard.trim());
        if (summonedCard == null) throw new NullPointerException();

        cards.add(summonedCard.cloned());

        for (GameEffect effect : mission.getCardEffects().getMissionReward().getEffects()) {
            effect.getFilter().setSelectingCardMethod(SelectingCardMethod.DETERMINED_CARD);
            effect.getFilter().setSourceCard(cards);
        }
    }

    @Override
    public String toString() {
        return "DeckFile\n{\n" +
                "\tplayer1Cards = " + Arrays.toString(player1Cards) +
                "\n\tplayer2Cards = " + Arrays.toString(player2Cards) +
                "\n}";
    }
}
