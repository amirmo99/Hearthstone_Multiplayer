package logic;

import abstracts.SecurePrototype;
import configs.LogicConstants;
import enums.GameSituation;
import models.Card;
import models.GameModel;
import models.cards.Minion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyGameState implements Serializable, SecurePrototype {

    private final PlayerFields player1, player2;
    private GameSituation situation;
    private boolean gameOver;
    private int activePlayerIndex;
    private int winner;

    private List<String> events;
    private MyTimer timer;

    private ArrayList<Minion> deadMinions;
    private List<GameModel> selectableModels;

    private boolean recentlyPlayedCard;
    private int recentlyPlayedCardIndexOnBoard;
    private boolean comboEnabled;

    private GameModel playingModel;
    private GameModel selectedModel;
    private GameModel affectingModel;
    private GameModel summoningModel;
    private GameModel transformedModel;
    private Card receivedCard;

    public MyGameState(Player player1, Player player2) {
        this.player1 = new PlayerFields(player1);
        this.player2 = new PlayerFields(player2);
        init();
    }

    private MyGameState(PlayerFields player1, PlayerFields player2, GameSituation situation, boolean gameOver, int activePlayerIndex,
                       int winner, List<String> events, List<GameModel> selectableModels, boolean recentlyPlayedCard, GameModel playingModel,
                        MyTimer timer, int recentlyPlayedCardIndexOnBoard) {
        this.player1 = player1;
        this.player2 = player2;
        this.situation = situation;
        this.gameOver = gameOver;
        this.activePlayerIndex = activePlayerIndex;
        this.winner = winner;
        this.events = events;
        this.selectableModels = selectableModels;
        this.recentlyPlayedCard = recentlyPlayedCard;
        this.playingModel = playingModel;
        this.timer = timer;
        this.recentlyPlayedCardIndexOnBoard = recentlyPlayedCardIndexOnBoard;
    }

    @Override
    public MyGameState secureCloned(int visiblePlayer) {
        if (visiblePlayer == 1) {
            return new MyGameState(player1, player2.secureCloned(0), situation, gameOver, activePlayerIndex, winner, events,
                    selectableModels, recentlyPlayedCard, playingModel, timer.securedTimer(), recentlyPlayedCardIndexOnBoard);
        }
        else {
            return new MyGameState(player1.secureCloned(0), player2, situation, gameOver, activePlayerIndex, winner, events,
                    selectableModels, recentlyPlayedCard, playingModel, timer.securedTimer(), recentlyPlayedCardIndexOnBoard);
        }
    }

    private void init() {
       events = new ArrayList<>();
       gameOver = false;
       activePlayerIndex = 1;
       situation = GameSituation.NORMAL;
       selectableModels = new ArrayList<>();
       deadMinions = new ArrayList<>();
       timer = new MyTimer(new LogicConstants().getEachTurnTime());
    }

    public void refreshMana() {
        getActivePlayer().setThisTurnMana(getActivePlayer().getEachTurnMana());
    }

    public void addManaLimit(int maxMana) {
        getActivePlayer().setEachTurnMana(Math.min(maxMana, getActivePlayer().getEachTurnMana() + 1));
    }

    public void changeActivePlayer() {
        activePlayerIndex = (activePlayerIndex == 1) ? 2 : 1;
    }

    public PlayerFields getPlayer(int index) {
        switch (index) {
            case 1:
                return player1;
            case 2:
                return player2;
            default:
                return null;
        }
    }

    public PlayerFields getActivePlayer() {
        return (activePlayerIndex == 1) ? player1 : player2;
    }

    public PlayerFields getNotActivePlayer() {
        return (activePlayerIndex == 1) ? player2 : player1;
    }

    public int getActivePlayerIndex() {
        return activePlayerIndex;
    }

    public int getNotActivePlayerIndex() {
        return 3 - activePlayerIndex;
    }

    public void addEvent(String event) {
        synchronized (this) {
            events.add(0, event);
        }
    }

    /////// Getters & setters

    public List<String> getEvents() {
        synchronized (this) {
            return events;
        }
    }

    public PlayerFields getPlayer1() {
        return player1;
    }

    public PlayerFields getPlayer2() {
        return player2;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public GameSituation getSituation() {
        return situation;
    }

    public void setSituation(GameSituation situation) {
        this.situation = situation;
    }

    public ArrayList<Minion> getDeadMinions() {
        return deadMinions;
    }

    public void setDeadMinions(ArrayList<Minion> deadMinions) {
        this.deadMinions = deadMinions;
    }

    public GameModel getPlayingModel() {
        return playingModel;
    }

    public void setPlayingModel(GameModel playingModel) {
        this.playingModel = playingModel;
    }

    public GameModel getSelectedModel() {
        return selectedModel;
    }

    public void setSelectedModel(GameModel selectedModel) {
        this.selectedModel = selectedModel;
    }

    public GameModel getAffectingModel() {
        return affectingModel;
    }

    public void setAffectingModel(GameModel affectingModel) {
        this.affectingModel = affectingModel;
    }

    public GameModel getSummoningModel() {
        return summoningModel;
    }

    public void setSummoningModel(GameModel summoningModel) {
        this.summoningModel = summoningModel;
    }

    public List<GameModel> getSelectableModels() {
        return selectableModels;
    }

    public void setSelectableModels(List<GameModel> selectableModels) {
        this.selectableModels = selectableModels;
    }

    public boolean isRecentlyPlayedCard() {
        return recentlyPlayedCard;
    }

    public void setRecentlyPlayedCard(boolean recentlyPlayedCard) {
        this.recentlyPlayedCard = recentlyPlayedCard;
    }

    public int getRecentlyPlayedCardIndexOnBoard() {
        return recentlyPlayedCardIndexOnBoard;
    }

    public void setRecentlyPlayedCardIndexOnBoard(int recentlyPlayedCardIndexOnBoard) {
        this.recentlyPlayedCardIndexOnBoard = recentlyPlayedCardIndexOnBoard;
    }

    public Card getReceivedCard() {
        return receivedCard;
    }

    public void setReceivedCard(Card receivedCard) {
        this.receivedCard = receivedCard;
    }

    public MyTimer getTimer() {
        return timer;
    }

    public void setTimer(MyTimer timer) {
        this.timer = timer;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public boolean isComboEnabled() {
        return comboEnabled;
    }

    public void setComboEnabled(boolean comboEnabled) {
        this.comboEnabled = comboEnabled;
    }

    public GameModel getTransformedModel() {
        return transformedModel;
    }

    public void setTransformedModel(GameModel transformedModel) {
        this.transformedModel = transformedModel;
    }
}
