package UI.GUI.MainPanels.MainGame;

import UI.GUI.MyPanel;
import UI.GUI.SubPanel.BackAndExitPanel;
import Util.ChatList;
import UtilSwing.AnimatorThread;
import UtilSwing.CircleButton;
import UtilSwing.ModelPanels.HeroPanel;
import UtilSwing.ModelPanels.HeroPowerPanel;
import UtilSwing.MyOptionPane;
import configs.LogicConstants;
import enums.GameFieldType;
import enums.GameSituation;
import enums.LogType;
import logic.CardInfo;
import logic.MyGameState;
import models.Card;
import models.InfoPassive;
import network.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GameBoardPanel extends MyPanel {

    private MyGameState gameState;
    private boolean isGameLocal;
    private boolean isPlayerOne;

    private boolean started = false;

    private AnimatorThread animatorThread;
    private MyOptionPane myOptionPane;
    private MouseAdapter mouseListener;

    private ChatRoom chatRoom;
    private JButton endTurn;
    private JButton showChatRoom;
    private BackAndExitPanel backAndExitPanel;
    private JTextArea events;

    private JTextArea cardsLeft1;
    private HeroPanel player1Hero;
    private HeroPowerPanel player1Power;
    private PlayerHandViewer player1Hand;
    private PlayerBoardViewer player1Board;
    private CircleButton player1MissionsButton;

    private JTextArea cardsLeft2;
    private HeroPanel player2Hero;
    private HeroPowerPanel player2Power;
    private PlayerHandViewer player2Hand;
    private PlayerBoardViewer player2Board;
    private CircleButton player2MissionsButton;

    private GameDrawer drawer;

    public GameBoardPanel(Client client, boolean isGameLocal, boolean isPlayerOne) {
        super(client);
        this.isGameLocal = isGameLocal;
        this.isPlayerOne = isPlayerOne;
        initBoardStuff();
        drawBackground(pathConfigs.getGameBGImage());
    }

    private void initBoardStuff() {
        animatorThread = new AnimatorThread(this);

        myOptionPane = new MyOptionPane(client);
        mouseListener = new InGameListener();

        chatRoom = new ChatRoom(client);
        backAndExitPanel = new BackAndExitPanel(client);
        backAndExitPanel.getBack().addActionListener(actionEvent -> client.getGameMapper().destroyGame());
        events = new JTextArea();
        endTurn = new JButton("End Turn");
        showChatRoom = new JButton("Chat Room");
    }

    public void start() {
        started = true;
        initGameStuff();
        configureElements();
        placeComponents();

        update();
        repaint();
    }

    private void initGameStuff() {
        // Player 1
        player1Hero = new HeroPanel(() -> (gameState.getPlayer1().getHero()));
        player1Power = new HeroPowerPanel(() -> gameState.getPlayer1().getHero().getHeroPower());
        player1Hand = new PlayerHandViewer(graphicConstants.getHandWidth(), () -> new ArrayList<>(gameState.getPlayer1().getHand()));
        player1Board = new PlayerBoardViewer(new LogicConstants().getMaxPlayedCards(), graphicConstants.getBoardWidth(),
                () -> new ArrayList<>(gameState.getPlayer1().getPlayedCards()));
        cardsLeft1 = new JTextArea();
        player1MissionsButton = new CircleButton("!");

        // Player 2
        player2Hero = new HeroPanel(() -> gameState.getPlayer2().getHero());
        player2Power = new HeroPowerPanel(() -> gameState.getPlayer2().getHero().getHeroPower());
        player2Hand = new PlayerHandViewer(graphicConstants.getHandWidth(), () -> new ArrayList<>(gameState.getPlayer2().getHand()));
        player2Board = new PlayerBoardViewer(7, graphicConstants.getBoardWidth(),
                () -> new ArrayList<>(gameState.getPlayer2().getPlayedCards()));
        cardsLeft2 = new JTextArea();
        player2MissionsButton = new CircleButton("!");
    }

    private void configureElements() {
        ButtonsListener buttonsListener = new ButtonsListener();

        endTurn.setFont(new Font(panelsConfigs.getFont(), Font.PLAIN, panelsConfigs.getHomeFontSize()));
        endTurn.addActionListener(buttonsListener);
        endTurn.setBackground(Color.GREEN);
        showChatRoom.setFont(new Font(panelsConfigs.getFont(), Font.PLAIN, panelsConfigs.getHomeFontSize()));
        showChatRoom.addActionListener(buttonsListener);
        showChatRoom.setBackground(Color.RED);

        cardsLeft1.setBackground(Color.CYAN);
        cardsLeft1.setEditable(false);
        cardsLeft2.setBackground(Color.CYAN);
        cardsLeft2.setEditable(false);

        player1MissionsButton.addActionListener(buttonsListener);
        player2MissionsButton.addActionListener(buttonsListener);

        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        gameState = client.getGameMapper().getGameState();

        if (drawer == null)
            drawer = new GameDrawer((int) graphicConstants.getGameWidth(), (int) graphicConstants.getGameHeight());
        drawer.setGraphics(g);

        if (gameState == null || !gameState.isGameOver()) {
            drawer.drawGameState(gameState);
        } else {
            showGameOver();
            drawer.drawGameOver((gameState.getWinner() == 1 && isPlayerOne) || (gameState.getWinner() == 2 && !isPlayerOne));
        }
    }

    private void placeComponents() {
        addComponents();

        // Panel Components
        setObjectBounds(backAndExitPanel, 0, 0, graphicConstants.getGameWidth(), 40);
        setObjectBounds(endTurn, graphicConstants.getEndX(), graphicConstants.getEndY(), graphicConstants.getEndWidth(), graphicConstants.getEndHeight());
        setObjectBounds(events, graphicConstants.getEventX(), graphicConstants.getEventY(), graphicConstants.getEventWidth(), graphicConstants.getEventHeight());
        setObjectBounds(showChatRoom, graphicConstants.getChatX(), graphicConstants.getChatY(), graphicConstants.getChatWidth(), graphicConstants.getChatHeight());

        // Player 1 Components
        setObjectBounds(player1Hand, graphicConstants.getHand1X(), graphicConstants.getHand1Y(), graphicConstants.getHandWidth(), graphicConstants.getHandHeight());
        setObjectBounds(player1Board, graphicConstants.getBoard1X(), graphicConstants.getBoard1Y(), graphicConstants.getBoardWidth(), graphicConstants.getBoardHeight());
        setObjectBounds(player1Hero, graphicConstants.getHero1X(), graphicConstants.getHero1Y(),
                graphicConstants.getHeroWidth() + graphicConstants.getCardWidth(), graphicConstants.getHeroHeight());
        setObjectBounds(player1Power, graphicConstants.getPower1X(), graphicConstants.getPower1Y(), graphicConstants.getPowerWidth(), graphicConstants.getPowerHeight());
        setObjectBounds(cardsLeft1, graphicConstants.getDeck1X(), graphicConstants.getDeck1Y(), graphicConstants.getDeckWidth(), graphicConstants.getDeckHeight());
        setObjectBounds(player1MissionsButton, graphicConstants.getMission1X(), graphicConstants.getMission1Y(), graphicConstants.getMissionDiameter(), graphicConstants.getMissionDiameter());

        player1Hand.setBorder(null);
        player1Board.setBorder(null);

        // Player 2 Components
        setObjectBounds(player2Hand, graphicConstants.getHand2X(), graphicConstants.getHand2Y(), graphicConstants.getHandWidth(), graphicConstants.getHandHeight());
        setObjectBounds(player2Board, graphicConstants.getBoard2X(), graphicConstants.getBoard2Y(), graphicConstants.getBoardWidth(), graphicConstants.getBoardHeight());
        setObjectBounds(player2Hero, graphicConstants.getHero2X(), graphicConstants.getHero2Y(),
                graphicConstants.getHeroWidth() + graphicConstants.getCardWidth(), graphicConstants.getHeroHeight());
        setObjectBounds(player2Power, graphicConstants.getPower2X(), graphicConstants.getPower2Y(), graphicConstants.getPowerWidth(), graphicConstants.getPowerHeight());
        setObjectBounds(cardsLeft2, graphicConstants.getDeck2X(), graphicConstants.getDeck2Y(), graphicConstants.getDeckWidth(), graphicConstants.getDeckHeight());
        setObjectBounds(player2MissionsButton, graphicConstants.getMission2X(), graphicConstants.getMission2Y(), graphicConstants.getMissionDiameter(), graphicConstants.getMissionDiameter());

        player2Hand.setBorder(null);
        player2Board.setBorder(null);

    }

    private void setObjectBounds(JComponent component, double x, double y, double width, double height) {
        component.setBounds((int) x, (int) y, (int) width, (int) height);
    }

    private void addComponents() {
        setLayout(null);
        // PLayer 1
        add(player1MissionsButton);
        add(player1Hand);
        add(player1Board);
        add(player1Hero);
        add(player1Power);
        add(cardsLeft1);

        // PLayer 2
        add(player2MissionsButton);
        add(player2Hand);
        add(player2Board);
        add(player2Hero);
        add(player2Power);
        add(cardsLeft2);

        // General
        add(backAndExitPanel);
        add(events);
        add(endTurn);
        add(showChatRoom);
    }

    public void update(boolean forceAll) {
        // PLayer 1
        gameState = client.getGameMapper().getGameState();

        player1Hand.setCardsVisibility(gameState.getActivePlayerIndex() == 1 || (isPlayerOne && !isGameLocal));
        cardsLeft1.setText("Card in deck : " + gameState.getPlayer1().getDeck().size());

        player1Hand.repaint();
        if (forceAll || !animatorThread.isAlive()) {
            player1Board.update(gameState.getSituation() == GameSituation.PLAYING && gameState.getActivePlayerIndex() == 1);
            player1Hero.update();
//            player1Power.setHeroPower(gameState.getPlayer1().getHero().getHeroPower());
        }

        // Player 2
        player2Hand.setCardsVisibility(gameState.getActivePlayerIndex() == 2 || (!isPlayerOne && !isGameLocal));
        cardsLeft2.setText("Card in deck : " + gameState.getPlayer2().getDeck().size());

        player2Hand.repaint();
        if (forceAll || !animatorThread.isAlive()) {
            player2Board.update(gameState.getSituation() == GameSituation.PLAYING && gameState.getActivePlayerIndex() == 2);
            player2Hero.update();
//            player2Power.setHeroPower(gameState.getPlayer2().getHero().getHeroPower());
        }

        // General
        events.setText(client.getGameMapper().readEvents());
//        endTurn.setEnabled((gameState.getSituation() != GameSituation.SELECTING) &&
//                ((gameState.getActivePlayerIndex() == 1 && isPlayerOne) ||(gameState.getActivePlayerIndex() == 2 && !isPlayerOne)));

        repaint();
        revalidate();
    }

    public void update() {
        if (gameState != null)
            checkNeedForAnimation();

        update(false);
    }

    private void clicked(GameFieldType type, Point point) {
        client.getGameMapper().getLogger().writeLog(LogType.REPORT, "Clicked On Point: " + point.toString() + ", " + type);

        switch (type) {
            case P1Hand:
                handClick(1);
                break;
            case P1HeroPower:
                powerClick(1);
                break;
            case P1Board:
                boardClick(1, point);
                break;
            case P1Hero:
                heroClick(1);
                break;
            case P2Hand:
                handClick(2);
                break;
            case P2Hero:
                heroClick(2);
                break;
            case P2Board:
                boardClick(2, point);
                break;
            case P2HeroPower:
                powerClick(2);
                break;
            case other:
                client.getGameMapper().playerEntry(new CardInfo(GameFieldType.other, -1));
                break;
        }

        update();

    }

    private void checkNeedForAnimation() {
        double x1, y1, x2, y2;
        Card card = client.getGameMapper().useRecentlyPlayedCard();
        int index;// = indexOfPlayedCard(card);
        index = gameState.getRecentlyPlayedCardIndexOnBoard();

        if (card != null) {
            if (gameState.getActivePlayerIndex() == 1) {
                x1 = (graphicConstants.getHand1X() + graphicConstants.getHandWidth()) / 2;
                y1 = graphicConstants.getHand1Y();
                x2 = player1Board.getCardX(index) + graphicConstants.getBoard1X();
                y2 = graphicConstants.getBoard1Y();
            } else {
                x1 = (graphicConstants.getHand2X() + graphicConstants.getHandWidth()) / 2;
                y1 = graphicConstants.getHand2Y();
                x2 = player2Board.getCardX(index) + graphicConstants.getBoard2X();
                y2 = graphicConstants.getBoard2Y();
            }

            animatorThread = new AnimatorThread(this);
            animatorThread.animateMovement(card, (int) x1, (int) y1, (int) x2, (int) y2);
        }
    }

    private int indexOfPlayedCard(Card card) {
        for (int i = 0; i < gameState.getActivePlayer().getPlayedCards().size(); i++) {
            if (gameState.getActivePlayer().getPlayedCards().get(i) == card)
                return i;
        }
        return -1;
    }


    private void boardClick(int playerIndex, Point point) {
        int index = (playerIndex == 1) ? player1Board.getIndex(point) : player2Board.getIndex(point);
        GameFieldType type = (playerIndex == 1) ? GameFieldType.P1Board : GameFieldType.P2Board;

        client.getGameMapper().playerEntry(new CardInfo(type, index));
    }

    private void handClick(int playerIndex) {
        if (gameState.getActivePlayerIndex() == playerIndex) {
            List<Card> cards = gameState.getActivePlayer().getHand();

            client.getGameMapper().getLogger().writeLog(LogType.REPORT, "Showing Card Chooser");
            Card card = myOptionPane.showCardChooser(cards, "Select to play a Card");
            client.getGameMapper().getLogger().writeLog(LogType.REPORT, "Selected -> " + card);

            int index = findIndex(cards, card);

            if (card != null) {
                GameFieldType type = (playerIndex == 1) ? GameFieldType.P1Hand : GameFieldType.P2Hand;
                client.getGameMapper().playerEntry(new CardInfo(type, index));
            }
        }
    }

    private int findIndex(List<?> list, Object object) {
        for (int i = 0; i < list.size(); i++) {
            if (object == list.get(i))
                return i;
        }
        return -1;
    }

    private void heroClick(int playerIndex) {
        GameFieldType type = (playerIndex == 1) ? GameFieldType.P1Hero : GameFieldType.P2Hero;
        int index = -1;

        client.getGameMapper().playerEntry(new CardInfo(type, index));
    }

    private void powerClick(int playerIndex) {
        GameFieldType type = (playerIndex == 1) ? GameFieldType.P1HeroPower : GameFieldType.P2HeroPower;
        int index = -1;

        client.getGameMapper().playerEntry(new CardInfo(type, index));
    }

    public void showGameOver() {
        removeMouseListener(mouseListener);
        removeMouseMotionListener(mouseListener);
        remove(endTurn);
        remove(showChatRoom);
        remove(player2Board);
        remove(player1Board);
        setToolTipText("");
    }

    public InfoPassive askForPassive(List<InfoPassive> passives) {
        return myOptionPane.showPassivesChooser(passives);
    }

    public ArrayList<Card> askForMultipleCards(List<Card> cards, String message) {
        return myOptionPane.showMultipleCardChooser(cards, message);
    }

    public Card playerDiscover(List<Card> cards) {
        return myOptionPane.showCardChooser(cards, "Choose one card");
    }

    public void updateChats(ChatList chatList) {
        chatRoom.updateChats(chatList);
    }

    class ButtonsListener implements ActionListener {

        private final MissionsFrame missionsFrame;

        public ButtonsListener() {
            missionsFrame = new MissionsFrame("Missions", client);
            missionsFrame.setSize((int) (graphicConstants.getGameWidth() * 0.4), (int) (graphicConstants.getGameHeight() * 0.2));
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JButton button = (JButton) actionEvent.getSource();
            if (button == endTurn) {
                endTurn();
            } else if (button == player1MissionsButton) {
                missions1();
            } else if (button == player2MissionsButton) {
                missions2();
            } else if (button == showChatRoom) {
                showChat();
            }
        }

        private void showChat() {
            chatRoom.setVisible(true);
        }

        private void endTurn() {
            client.getGameMapper().sendEndTurnCommand();
            update();
        }

        private void missions1() {
            if (gameState.getActivePlayerIndex() == 1)
                missionsFrame.showMissions(gameState.getPlayer1().getActiveMissions());
        }

        private void missions2() {
            if (gameState.getActivePlayerIndex() == 2)
                missionsFrame.showMissions(gameState.getPlayer2().getActiveMissions());
        }
    }

    class InGameListener extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    leftClick(mouseEvent);
                }
            };

            new Thread(runnable).start();
        }

        private void leftClick(MouseEvent mouseEvent) {
            Point original = mouseEvent.getPoint();
            GameFieldType type = getType(original);

            Point converted = convertToType(original, type);
//            if (!isPlayer2CPU || gameState.getActivePlayerIndex() == 1)
                clicked(type, converted);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            Point original = e.getPoint();
            GameFieldType type = getType(original);

            String message = findInfo(type, convertToType(original, type));
            setToolTipText(message);
        }

        private String findInfo(GameFieldType type, Point point) {
            switch (type) {
                case P1Board:
                    return player1Board.getCardInfo(point);
                case P2Board:
                    return player2Board.getCardInfo(point);
                case P1Hero:
                    String passiveDescription1 = (gameState.getPlayer1().getPassive() == null) ? "" : gameState.getPlayer1().getPassive().getDescription();
                    return gameState.getPlayer1().getHero().getSpecialPowerDescription() + ".\n  " +
                            "InfoPassive: " + passiveDescription1;
                case P2Hero:
                    String passiveDescription2 = (gameState.getPlayer2().getPassive() == null) ? "" : gameState.getPlayer2().getPassive().getDescription();
                    return gameState.getPlayer2().getHero().getSpecialPowerDescription() + ".\n  " +
                            "InfoPassive: " + passiveDescription2;
                case P1HeroPower:
                    return gameState.getPlayer1().getHero().getHeroPower().getDescription();
                case P2HeroPower:
                    return gameState.getPlayer2().getHero().getHeroPower().getDescription();
            }
            return "";
        }

        private GameFieldType getType(Point point) {
            if (player1Board.contains(SwingUtilities.convertPoint(null, point, player1Board)))
                return GameFieldType.P1Board;
            if (player1Hand.contains(SwingUtilities.convertPoint(null, point, player1Hand)))
                return GameFieldType.P1Hand;
            if (player1Hero.contains(SwingUtilities.convertPoint(null, point, player1Hero)))
                return GameFieldType.P1Hero;
            if (player1Power.contains(SwingUtilities.convertPoint(null, point, player1Power)))
                return GameFieldType.P1HeroPower;
            if (player2Board.contains(SwingUtilities.convertPoint(null, point, player2Board)))
                return GameFieldType.P2Board;
            if (player2Hand.contains(SwingUtilities.convertPoint(null, point, player2Hand)))
                return GameFieldType.P2Hand;
            if (player2Hero.contains(SwingUtilities.convertPoint(null, point, player2Hero)))
                return GameFieldType.P2Hero;
            if (player2Power.contains(SwingUtilities.convertPoint(null, point, player2Power)))
                return GameFieldType.P2HeroPower;

            return GameFieldType.other;
        }

        private Point convertToType(Point point, GameFieldType type) {
            switch (type) {
                case P1Hand:
                    return SwingUtilities.convertPoint(null, point, player1Hand);
                case P1Board:
                    return SwingUtilities.convertPoint(null, point, player1Board);
                case P1Hero:
                    return SwingUtilities.convertPoint(null, point, player1Hero);
                case P1HeroPower:
                    return SwingUtilities.convertPoint(null, point, player1Power);
                case P2Hand:
                    return SwingUtilities.convertPoint(null, point, player2Hand);
                case P2Board:
                    return SwingUtilities.convertPoint(null, point, player2Board);
                case P2Hero:
                    return SwingUtilities.convertPoint(null, point, player2Hero);
                case P2HeroPower:
                    return SwingUtilities.convertPoint(null, point, player2Power);

                default:
                    return point;

            }
        }
    }

    public void setGameState(MyGameState gameState) {
        this.gameState = gameState;
    }


    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}
