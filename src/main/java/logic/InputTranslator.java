package logic;

import UI.GameMapper;
import abstracts.Attacker;
import enums.CardSituation;
import enums.GameFieldType;
import enums.GameSituation;
import models.Card;
import models.GameModel;
import models.HeroPower;
import models.Heroes;
import models.cards.Minion;

import java.util.List;

public class InputTranslator {

    private MyGameState gameState;
    private GameActionExecutor executor;

    public InputTranslator(MyGameState gameState, GameActionExecutor executor) {
        this.executor = executor;
        this.gameState = gameState;
    }

    public void select(CardInfo info) {
        Card card;

        switch (info.getFieldType()) {
            case P1Board:
                card = gameState.getPlayer1().getPlayedCards().get(info.getIndex());
                if (card != null && card.getSituation() == CardSituation.SELECTABLE) {
                    gameState.setSelectedModel(card);
                    gameState.setSituation(GameSituation.NORMAL);
                    executor.getMonitor().doNotify();
                }
                break;
            case P2Board:
                card = gameState.getPlayer2().getPlayedCards().get(info.getIndex());
                if (card != null && card.getSituation() == CardSituation.SELECTABLE) {
                    gameState.setSelectedModel(card);
                    gameState.setSituation(GameSituation.NORMAL);
                    executor.getMonitor().doNotify();
                }
                break;
            case P1Hero:
                Heroes hero = gameState.getPlayer1().getHero();
                if (hero.getSituation() == CardSituation.SELECTABLE) {
                    gameState.setSelectedModel(hero);
                    gameState.setSituation(GameSituation.NORMAL);
                    executor.getMonitor().doNotify();
                }
                break;
            case P2Hero:
                Heroes hero2 = gameState.getPlayer2().getHero();
                if (hero2.getSituation() == CardSituation.SELECTABLE) {
                    gameState.setSelectedModel(hero2);
                    gameState.setSituation(GameSituation.NORMAL);
                    executor.getMonitor().doNotify();
                }
                break;
        }

    }
    public void normalPlay(CardInfo info) {
        int infoPlayerIndex = info.getFieldType().playerIndex();

        if (gameState.getActivePlayerIndex() == infoPlayerIndex) {
            switch (info.getFieldType()) {
                case P1Hand:
                case P2Hand:
                    handClick(info);
                    break;
                case P1Board:
                case P2Board:
                    boardClick(info);
                    break;
                case P1HeroPower:
                case P2HeroPower:
                    powerClick(info);
                    break;
                case P1Hero:
                case P2Hero:
                    heroClick(info);
                    break;
            }
        }
    }

    private void handClick(CardInfo info) {
        gameState.setPlayingModel(getHand().get(info.getIndex()));
        if (gameState.getPlayingModel() instanceof Minion)
            gameState.setSituation(GameSituation.PLAYING);
        else if (gameState.getPlayingModel() != null) {
            executor.playCard((Card) gameState.getPlayingModel(), -1);
            gameState.setSituation(GameSituation.NORMAL);
        }
    }

    private void boardClick(CardInfo info) {
        gameState.setPlayingModel(getPlayedCards().get(info.getIndex()));
        if (gameState.getPlayingModel() != null)
            if (executor.canAttack((Attacker) gameState.getPlayingModel())) {
                gameState.setSituation(GameSituation.ATTACKING);
            } else
                gameState.setSituation(GameSituation.NORMAL);
    }

    private void heroClick(CardInfo info) {
        gameState.setPlayingModel((info.getFieldType() == GameFieldType.P1Hero) ? gameState.getPlayer1().getHero() : gameState.getPlayer2().getHero());
        if (executor.canAttack(getHero()) && info.getFieldType().playerIndex() == gameState.getActivePlayerIndex()) {
            gameState.setSituation(GameSituation.ATTACKING);
        }
    }

    private void powerClick(CardInfo info) {
        if (info.getFieldType().playerIndex() == gameState.getActivePlayerIndex()) {
            HeroPower power = getHero().getHeroPower();
            gameState.setPlayingModel(power);

            executor.powerRequest(power);
        }
        gameState.setSituation(GameSituation.NORMAL);
    }

    public void attack(CardInfo info) {
        GameModel model = null;
        switch (info.getFieldType()) {
            case P1Hero:
                model = gameState.getPlayer1().getHero();
                break;
            case P2Hero:
                model = gameState.getPlayer2().getHero();
                break;
            case P1Board:
                model = gameState.getPlayer1().getPlayedCards().get(info.getIndex());
                break;
            case P2Board:
                model = gameState.getPlayer2().getPlayedCards().get(info.getIndex());
                break;
        }
        if (model != null) {
            if (info.getFieldType().playerIndex() != gameState.getActivePlayerIndex()) {
                executor.attackRequest(model);
            }
        }

        // Stuff
        gameState.setSituation(GameSituation.NORMAL);
    }

    public void placeCard(CardInfo info) {
        switch (info.getFieldType()) {
            case P1Board:
            case P2Board:
                if (gameState.getActivePlayerIndex() == info.getFieldType().playerIndex()) {
                    executor.playCard((Card) gameState.getPlayingModel(), info.getIndex());
                    gameState.setSituation(GameSituation.NORMAL);
                }
                break;
        }
        gameState.setSituation(GameSituation.NORMAL);
    }


    ////////////////////////////////  Active Player Fast call
    private List<Card> getHand() {
        return gameState.getActivePlayer().getHand();
    }

    private List<Card> getDeck() {
        return gameState.getActivePlayer().getDeck();
    }

    private List<Minion> getPlayedCards() {
        return gameState.getActivePlayer().getPlayedCards();
    }

    private int getManaLimit() {
        return gameState.getActivePlayer().getEachTurnMana();
    }

    private int getManaLeft() {
        return gameState.getActivePlayer().getThisTurnMana();
    }

    private Heroes getHero() {
        return gameState.getActivePlayer().getHero();
    }

    private void setHand(List<Card> hand) {
        gameState.getActivePlayer().setHand(hand);
    }

    private void setDeck(List<Card> deck) {
        gameState.getActivePlayer().setDeck(deck);
    }

    private void setPlayedCards(List<Minion> playedCards) {
        gameState.getActivePlayer().setPlayedCards(playedCards);
    }

    private void setManaLimit(int manaLimit) {
        gameState.getActivePlayer().setEachTurnMana(manaLimit);
    }

    private void setManaLeft(int manaLeft) {
        gameState.getActivePlayer().setThisTurnMana(manaLeft);
    }
}
