package UI.GUI.MainPanels.MainGame;

import UtilSwing.Drawers.CardDrawer;
import abstracts.CardListHolder;
import configs.GameGraphicConstants;
import models.Card;
import models.cards.Minion;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerHandViewer extends JPanel {

    private List<Card> cards;
    private final double width;
    private boolean isVisible;
    private CardDrawer drawer;
    private GameGraphicConstants constants = new GameGraphicConstants();

    private CardListHolder cardListHolder;

    public PlayerHandViewer(double width, CardListHolder cardListHolder) {
        super();
        this.width = width;
        this.cardListHolder = cardListHolder;

        init();
        repaint();
    }

//    public PlayerHandViewer(double width, int numberOfHiddenCard) {
//        super();
//        this.width = width;
//        this.cards = new ArrayList<>();
//        for (int i = 0; i < numberOfHiddenCard; i++) {
//            cards.add(null);
//        }
//        this.isVisible = false;
//    }

    private void init() {
        isVisible = true;
        drawer = new CardDrawer();

        setOpaque(false);
        setPreferredSize(new Dimension((int) width, -1));
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawCards(g);
    }

    private void drawCards(Graphics g) {
        int size = cardListHolder.getCards().size();
        int distance = (int) Math.min((width / size), constants.getCardWidth());

        for (int i = 0; i < cardListHolder.getCards().size(); i++) {
            int x = distance * i;
            Card card = cardListHolder.getCards().get(i);

            drawer.drawCard(g, card, this.isVisible, false, x, 0);
        }

    }

    public void setCardsVisibility(boolean visible) {
        isVisible = visible;
    }
}
