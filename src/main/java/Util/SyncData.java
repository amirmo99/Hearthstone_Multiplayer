package Util;

import models.Card;
import models.Deck;
import models.Heroes;
import logic.Player;

import java.util.ArrayList;
import java.util.List;

public class SyncData {

    public static void syncWithDataBase(Player player) {

        List<Heroes> syncedHeroes = new ArrayList<>();

        for (Heroes myHero : player.getMyAllHeroes()) {
            for (Heroes hero : Heroes.getAllHeroes()) {
                if (hero.getName().equals(myHero.getName())) {
                    hero.setMyDecks(myHero.getMyDecks());
                    syncedHeroes.add(hero);
                }
            }
        }
        player.setMyAllHeroes(syncedHeroes);


        List<Card> syncedCards = syncCards(player.getCards());
        player.setCards(syncedCards);

        for (Deck deck : player.getMyDecks()) {
            for (Card card : deck.getCards()) {
                card.setCardEffects(Card.getCard(card.getName()).getCardEffects());
            }
        }

        Administrator.updateDataModels(player);
    }

    public static List<Card> syncCards(List<Card> cards) {
        List<Card> syncedCards = new ArrayList<>();
        for (Card card : cards) {
            syncedCards.add(Card.getCard(card.getName()));
        }
        return syncedCards;
    }

}
