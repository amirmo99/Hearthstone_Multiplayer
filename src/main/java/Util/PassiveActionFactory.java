package Util;

import abstracts.ModelAction;
//import logic.GameState;
import logic.MyGameState;
import models.Card;
import models.Heroes;

public class PassiveActionFactory {

    public static ModelAction createAction(String name) {
        switch (name) {
            case "Twice Draw":
                return Action.TwiceDraw;
            case "Off Card":
                return Action.OffCard;
            case "Warriors":
                return Action.Warriors;
            case "Nurse":
                return Action.Nurse;
            case "Free Power":
                return Action.FreePower;
            case "Mana Jump":
                return Action.ManaJump;
            case "Zombie":
                return Action.Zombie;
            default:
                return null;
        }
    }

    private enum Action implements ModelAction {
        TwiceDraw {
            @Override
            public void execute(MyGameState gameState) {
                gameState.getActivePlayer().setNumberOfNewCardsEachTurn(2);
            }
        },
        OffCard {
            @Override
            public void execute(MyGameState gameState) {
                for (Card card : gameState.getActivePlayer().getDeck()) {
                    card.setMana(Math.max(0, card.getMana() - 1));
                }
                for (Card card : gameState.getActivePlayer().getHand()) {
                    card.setMana(Math.max(0, card.getMana() - 1));
                }
            }
        },
        Warriors {
            @Override
            public void execute(MyGameState gameState) {

            }
        },
        Nurse {
            @Override
            public void execute(MyGameState gameState) {

            }
        },
        FreePower {
            @Override
            public void execute(MyGameState gameState) {
                Heroes hero = gameState.getActivePlayer().getHero();
                int max = Math.max(0, hero.getHeroPower().getMana() - 1);
                hero.getHeroPower().setMana(max);
                hero.getHeroPower().setActionEachTurn(2);
            }
        },
        ManaJump {
            @Override
            public void execute(MyGameState gameState) {
                gameState.getActivePlayer().setEachTurnMana(gameState.getActivePlayer().getEachTurnMana() + 1);
            }
        },
        Zombie {
            @Override
            public void execute(MyGameState gameState) {

            }
        }

    }
}

