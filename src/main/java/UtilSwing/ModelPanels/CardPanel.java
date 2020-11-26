package UtilSwing.ModelPanels;

import UtilSwing.Drawers.CardDrawer;
import abstracts.Updatable;
import models.Card;

import java.awt.*;

public class CardPanel extends ModelPanel {
    private Card card;
    private final boolean locked;

    CardDrawer cardDrawer;

    public CardPanel(Card card, boolean locked, boolean drawAbilities) {
        super();
        this.card = card;
        this.locked = locked;
        this.cardDrawer = new CardDrawer();
        this.cardDrawer.setDrawAbilities(drawAbilities);

        configure();
    }

    private void configure() {
        setPreferredSize(new Dimension((int) constants.getCardWidth(), (int) constants.getCardHeight()));
        setOpaque(false);
        setBorder(null);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        cardDrawer.drawCard(g, card, true, locked, 0, 0);
    }

}

