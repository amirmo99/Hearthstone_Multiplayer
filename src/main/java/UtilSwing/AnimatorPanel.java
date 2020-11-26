package UtilSwing;

import UtilSwing.Drawers.CardDrawer;
import models.Card;

import javax.swing.*;
import java.awt.*;

public class AnimatorPanel extends JPanel {

    private int x,y;
    private Card card;
    private final CardDrawer drawer;

    public AnimatorPanel() {
        super();
        this.drawer = new CardDrawer();

        setOpaque(false);
    }

    public void draw(int x, int y) {
        this.x = x;
        this.y = y;

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawer.drawCard(g, card, true, false, x, y);
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
