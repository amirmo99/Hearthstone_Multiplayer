package Util;

import abstracts.ModelAction;
//import logic.GameStateV1;
import logic.MyGameState;
import logic.PlayerFields;
import models.HeroPower;

public class HeroPowerActionFactory {

    public static ModelAction getAction(String name) {
        switch (name) {
            case "Fire Blast":
                return actions.FireBlast;
            case "Heal":
                return actions.Heal;
            case "Life Tap":
                return actions.LifeTap;
            case "The Silver Hands":
                return actions.TheSilverHands;
            case "Thief":
                return actions.Thief;
            case "Caltrops":
                return actions.Caltrops;
            default:
                return null;
        }
    }

    private enum actions implements ModelAction {
        FireBlast {
            @Override
            public void execute(MyGameState gameState) {
                reduceCost(gameState);
            }
        },
        Heal {
            @Override
            public void execute(MyGameState gameState) {
                reduceCost(gameState);
            }
        },
        LifeTap {
            @Override
            public void execute(MyGameState gameState) {
                reduceCost(gameState);
            }
        },
        TheSilverHands {
            @Override
            public void execute(MyGameState gameState) {
                reduceCost(gameState);
            }
        },
        Thief {
            @Override
            public void execute(MyGameState gameState) {
                reduceCost(gameState);
            }
        },
        Caltrops {
            @Override
            public void execute(MyGameState gameState) {
                reduceCost(gameState);
            }
        };

        void reduceCost(MyGameState gameState) {
            PlayerFields player = gameState.getActivePlayer();
            HeroPower power = player.getHero().getHeroPower();
            player.setThisTurnMana(player.getThisTurnMana() - power.getMana());
            power.setActionsLeft(power.getActionsLeft() - 1);
        }
    }
}

