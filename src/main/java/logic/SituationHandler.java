package logic;

import enums.CardSituation;
import enums.MinionAbility;
import models.Card;
import models.GameModel;
import models.HeroPower;
import models.Heroes;
import models.cards.Minion;

import java.util.List;

public class SituationHandler {

    private MyGameState gameState;
    private GameActionExecutor executor;
    private List<GameModel> selectableModels;

    public SituationHandler(MyGameState gameState, GameActionExecutor executor) {
        this.executor = executor;
        this.gameState = gameState;
    }

    private void initialize() {
        this.selectableModels = gameState.getSelectableModels();
    }

    public void handleCardsSituation() {
        initialize();
        switch (gameState.getSituation()) {
            case NORMAL:
                normalSituation();
                break;
            case ATTACKING:
                attackSituation();
                break;
            case PLAYING:
                placeSituation();
                break;
            case SELECTING:
                selectSituation();
                break;
        }
    }
    private void normalSituation() {
        ///// Minions
        for (Minion minion : gameState.getNotActivePlayer().getPlayedCards()) {
            if (minion != null) {
                minion.setSituation(CardSituation.NEUTRAL);
            }
        }

        for (Minion minion : gameState.getActivePlayer().getPlayedCards()) {
            if (minion != null) {
                if (executor.canAttack(minion)) {
                    minion.setSituation(CardSituation.PLAYABLE);
                } else {
                    minion.setSituation(CardSituation.NEUTRAL);
                }
            }
        }
        ///// Heroes
        gameState.getNotActivePlayer().getHero().setSituation(CardSituation.NEUTRAL);

        if (executor.canAttack(gameState.getActivePlayer().getHero())) {
            gameState.getActivePlayer().getHero().setSituation(CardSituation.PLAYABLE);
        }
        else {
            gameState.getActivePlayer().getHero().setSituation(CardSituation.NEUTRAL);
        }
        ////// Hero Powers
        gameState.getNotActivePlayer().getHero().getHeroPower().setSituation(CardSituation.NEUTRAL);

        if (gameState.getActivePlayer().getHero().getHeroPower().getActionsLeft() > 0 &&
                gameState.getActivePlayer().getHero().getHeroPower().getMana() <= gameState.getActivePlayer().getThisTurnMana() &&
                !gameState.getActivePlayer().getHero().getHeroPower().isPassive())
            gameState.getActivePlayer().getHero().getHeroPower().setSituation(CardSituation.PLAYABLE);
        else
            gameState.getActivePlayer().getHero().getHeroPower().setSituation(CardSituation.NEUTRAL);
    }
    private void attackSituation() {
        if (executor.hasTaunt(gameState.getNotActivePlayer().getPlayedCards())) {
            attackSituationWithTaunt();
        }
        else {
            attackSituationNoTaunt();
        }

        for (Minion minion : gameState.getActivePlayer().getPlayedCards()) {
            if (minion != null) {
                if (minion == gameState.getPlayingModel()) {
                    minion.setSituation(CardSituation.ATTACKING);
                } else
                    minion.setSituation(CardSituation.NEUTRAL);
            }
        }

        if (gameState.getPlayingModel() == gameState.getActivePlayer().getHero())
            gameState.getActivePlayer().getHero().setSituation(CardSituation.ATTACKING);
        else
            gameState.getActivePlayer().getHero().setSituation(CardSituation.NEUTRAL);
    }

    private void attackSituationNoTaunt() {
        for (Minion minion : gameState.getNotActivePlayer().getPlayedCards()) {
            if (minion != null && !minion.getAbilities().contains(MinionAbility.STEALTH)) {
                minion.setSituation(CardSituation.VULNERABLE);
            }
            else if (minion != null)
                minion.setSituation(CardSituation.NEUTRAL);
        }

        if (gameState.getPlayingModel() instanceof Minion && (
                ((Minion) gameState.getPlayingModel()).getRemainedAttacks() == 0 && ((Minion) gameState.getPlayingModel()).getAbilities().contains(MinionAbility.RUSH)))
            gameState.getNotActivePlayer().getHero().setSituation(CardSituation.NEUTRAL);
        else
            gameState.getNotActivePlayer().getHero().setSituation(CardSituation.VULNERABLE);
    }

    private void attackSituationWithTaunt() {
        for (Minion minion : gameState.getNotActivePlayer().getPlayedCards()) {
            if (minion != null && minion.getAbilities().contains(MinionAbility.TAUNT)) {
                if (!minion.getAbilities().contains(MinionAbility.STEALTH))
                    minion.setSituation(CardSituation.VULNERABLE);
            } else if (minion != null) {
                minion.setSituation(CardSituation.NEUTRAL);
            }
        }
        gameState.getNotActivePlayer().getHero().setSituation(CardSituation.NEUTRAL);
    }

    private void selectSituation() {
        for (Minion playedCard : gameState.getNotActivePlayer().getPlayedCards()) {
            if (playedCard != null) {
                if (selectableModels.contains(playedCard) && !playedCard.getAbilities().contains(MinionAbility.STEALTH))
                    playedCard.setSituation(CardSituation.SELECTABLE);
                else
                    playedCard.setSituation(CardSituation.NEUTRAL);
            }
        }
        Heroes hero = gameState.getNotActivePlayer().getHero();
        hero.getHeroPower().setSituation(CardSituation.NEUTRAL);
        hero.setSituation((selectableModels.contains(hero)) ? CardSituation.SELECTABLE : CardSituation.NEUTRAL);

        for (Card playedCard : gameState.getActivePlayer().getPlayedCards()) {
            if (playedCard != null) {
                if (selectableModels.contains(playedCard))
                    playedCard.setSituation(CardSituation.SELECTABLE);
                else
                    playedCard.setSituation(CardSituation.NEUTRAL);
            }
        }
        hero = gameState.getActivePlayer().getHero();
        hero.setSituation((selectableModels.contains(hero)) ? CardSituation.SELECTABLE : CardSituation.NEUTRAL);
        if (hero.getHeroPower() == gameState.getPlayingModel())
            hero.getHeroPower().setSituation(CardSituation.ATTACKING);
        else
            hero.getHeroPower().setSituation(CardSituation.NEUTRAL);

    }

    private void placeSituation() {
        for (Card playedCard : gameState.getNotActivePlayer().getPlayedCards()) {
            if (playedCard != null) {
                playedCard.setSituation(CardSituation.NEUTRAL);
            }
        }
        gameState.getNotActivePlayer().getHero().setSituation(CardSituation.NEUTRAL);

        for (Card playedCard : gameState.getActivePlayer().getPlayedCards()) {
            if (playedCard != null) {
                playedCard.setSituation(CardSituation.NEUTRAL);
            }
        }
        gameState.getActivePlayer().getHero().setSituation(CardSituation.NEUTRAL);
    }
}
