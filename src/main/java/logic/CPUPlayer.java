package logic;

import UI.GameMapper;
import Util.Monitor;
import enums.CardSituation;
import enums.GameFieldType;
import enums.GameSituation;
import models.Card;
import models.GameModel;
import models.InfoPassive;
import models.cards.Minion;

import java.util.List;
import java.util.Random;

public class CPUPlayer extends Thread {

    private PlayerFields player;
    private MyGameState gameState;
    private GameActionExecutor executor;

    private final int updatePeriod = 1000;

    private Random random;

    private Monitor monitor;

    public CPUPlayer(MyGameState gameState, GameActionExecutor executor) {
        this.gameState = gameState;
        this.executor = executor;
        this.player = gameState.getPlayer2();

        this.random = new Random();
        this.monitor = new Monitor();
    }

    @Override
    public void run() {
        while (!gameState.isGameOver() && !isInterrupted()) {

            while (gameState.getActivePlayerIndex() == 1) monitor.doWait();


            while (!isInterrupted() && gameState.getActivePlayerIndex() == 2) {
                try {
                    doRun();
                } catch (InterruptedException e) {
                    System.out.println("CPU Interrupted");
                    break;
                }
            }
        }
    }

    public void check() {
        monitor.doNotify();
    }

    public void selectPassive(List<InfoPassive> passives) {
        gameState.getPlayer2().setPassive(passives.get(new Random().nextInt(passives.size())));
    }

    private void doRun() throws InterruptedException {
        boolean mustWait = gameState.getSituation() == GameSituation.PLAYING;

        Thread.sleep(updatePeriod);
        new Thread(this::doAction).start();

        if (mustWait)
            Thread.sleep(2000);
    }

    private void doAction() {
        if (gameState.getSituation() == GameSituation.SELECTING) {
            selectCard();
        } else if (gameState.getSituation() == GameSituation.ATTACKING) {
            attackCard();
        } else if (gameState.getSituation() == GameSituation.PLAYING) {
            placeCard();
        } else if (checkPlaying() ||
                checkAttackingMinion() ||
                checkAttackingWeapon()) {
        } else if (gameState.getActivePlayerIndex() == 2)
            executor.endTurn();
    }

    private boolean checkPlaying() {
        int n = random.nextInt(2);
        switch (n) {
            case 0:
                boolean b1 = checkPlayingCard();
                if (!b1) {
                    return checkPlayingHeroPower();
                }
                return true;
            case 1:
                boolean b2 = checkPlayingHeroPower();
                if (!b2) {
                    return checkPlayingCard();
                }
                return true;
            default:
                return false;
        }
    }

    private boolean checkPlayingCard() {
        for (Card card : player.getHand()) {
            if (card.getMana() <= player.getThisTurnMana() && (!(card instanceof Minion) || canPlayMinion())) {
                sendCommand(card);
                return true;
            }
        }
        return false;
    }

    private boolean canPlayMinion() {
        for (Minion minion : player.getPlayedCards()) {
            if (minion == null)
                return true;
        }
        return false;
    }

    private void placeCard() {
        for (int i = 0; i < player.getPlayedCards().size(); i++) {
            if (player.getPlayedCards().get(i) == null) {
                executor.playerEntry(new CardInfo(GameFieldType.P2Board, i));
            }
        }
    }

    private boolean checkPlayingHeroPower() {
        if (player.getHero().getHeroPower().getMana() <= player.getThisTurnMana() &&
                player.getHero().getHeroPower().getSituation() == CardSituation.PLAYABLE) {
            sendCommand(player.getHero().getHeroPower());
            return true;
        }
        return false;
    }

    private boolean checkAttackingMinion() {
        for (Minion minion : player.getPlayedCards()) {
            if (minion == null) continue;

            if (minion.getSituation() == CardSituation.PLAYABLE) {
                sendCommand(minion);
                return true;
            }
        }
        return false;
    }

    private boolean checkAttackingWeapon() {
        if (player.getHero().getSituation() == CardSituation.PLAYABLE) {
            sendCommand(player.getHero());
            return true;
        }
        return false;
    }

    private void selectCard() {
        List<GameModel> models = gameState.getSelectableModels();
        if (models.size() > 0) {
            sendCommand(models.get(random.nextInt(models.size())));
        }
    }

    private void attackCard() {
        if (gameState.getPlayer1().getHero().getSituation() == CardSituation.VULNERABLE) {
            sendCommand(gameState.getPlayer1().getHero());
        } else {
            for (Minion minion : gameState.getPlayer1().getPlayedCards()) {
                if (minion == null) continue;

                if (minion.getSituation() == CardSituation.VULNERABLE) {
                    sendCommand(minion);
                    return;
                }
            }
        }
    }

    private void sendCommand(GameModel model) {
        GameFieldType type;
        int index;

        if (gameState.getPlayer1().getHero() == model)
            executor.playerEntry(new CardInfo(GameFieldType.P1Hero, -1));
        else if (gameState.getPlayer2().getHero() == model)
            executor.playerEntry(new CardInfo(GameFieldType.P2Hero, -1));
        else if (gameState.getPlayer2().getHero().getHeroPower() == model)
            executor.playerEntry(new CardInfo(GameFieldType.P2HeroPower, -1));

        for (int i = 0; i < gameState.getPlayer2().getHand().size(); i++) {
            if (model == gameState.getPlayer2().getHand().get(i)) {
                executor.playerEntry(new CardInfo(GameFieldType.P2Hand, i));
                return;
            }
        }

        for (int i = 0; i < gameState.getPlayer1().getPlayedCards().size(); i++) {
            if (model == gameState.getPlayer1().getPlayedCards().get(i)) {
                executor.playerEntry(new CardInfo(GameFieldType.P1Board, i));
                return;
            }
        }

        for (int i = 0; i < gameState.getPlayer2().getPlayedCards().size(); i++) {
            if (model == gameState.getPlayer2().getPlayedCards().get(i)) {
                executor.playerEntry(new CardInfo(GameFieldType.P2Board, i));
                return;
            }
        }
    }
}
