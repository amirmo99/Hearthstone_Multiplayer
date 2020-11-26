package UI;

import UI.GUI.MainFrame;
import UI.GUI.MainPanels.MainGame.GameBoardPanel;
import Util.*;
import configs.LogicConstants;
import enums.LogType;
import logic.CardInfo;
import logic.GameActionExecutor;
import logic.MyGameState;
import models.Card;
import logic.Player;
import models.Deck;
import models.InfoPassive;
import network.api.Message;
import network.api.MessageType;
import network.client.Client;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GameMapper {

    private volatile MyGameState gameState;

    private Player player;
    private Client client;

    public GameMapper(Client client) {
        this.client = client;
    }

    public void createGame() {
        client.sendMessage(new Message(MessageType.CREATE_LOCAL_GAME));
    }

    public void destroyGame() {
        client.sendMessage(new Message(MessageType.GAME_ACTION_LEAVE_MATCH));
    }

    public void createOnlineGame() {
        client.sendMessage(new Message(MessageType.CREATE_MULTIPLAYER_GAME));
    }

    public void createGameFromFile() {
            client.sendMessage(new Message(MessageType.CREATE_FILE_GAME));
    }

    public void createGameWithCPU() {
        client.sendMessage(new Message(MessageType.CREATE_VS_AI_GAME));
    }


    public Card useRecentlyPlayedCard() {

        if (gameState.isRecentlyPlayedCard()) {
            gameState.setRecentlyPlayedCard(false);
            return (Card) gameState.getPlayingModel();
        }
        return null;
    }

    public void sendEndTurnCommand() {
        client.sendMessage(new Message(MessageType.GAME_ACTION_END_TURN));
    }

    public String readEvents() {
        return gameState.getEvents().toString();
    }

    public void playerEntry(CardInfo info) {
        client.sendMessage(new Message(MessageType.GAME_ACTION_ENTRY, info));
//        gameAction.playerEntry(info);
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public InfoPassive askForPassive(List<InfoPassive> passives) {
        return client.getMainFrame().getGameBoardPanel().askForPassive(passives);
    }

    public ArrayList<Card> askForMultipleCards(List<Card> cards, String message) {
        return client.getMainFrame().getGameBoardPanel().askForMultipleCards(cards, message);
    }

    public Card discover(List<Card> cards) {
        return client.getMainFrame().getGameBoardPanel().playerDiscover(cards);

    }

    public void timerTick() {
        if (gameState == null) return;
        MyGameState gameState = getGameState();
        gameState.getTimer().setCount(gameState.getTimer().getCount() - 1);
    }

    public MyGameState getGameState() {
        synchronized (this) {
            return gameState;
        }
    }

    public GameLogger getLogger() {
        return player.getLogger();
    }

    public void setGameState(MyGameState gameState) {
        synchronized (this) {
            this.gameState = gameState;
        }
    }
}


