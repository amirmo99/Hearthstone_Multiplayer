package UI.GUI.MainPanels.MainGame;

import UtilSwing.Drawers.CardDrawer;
import abstracts.MinionListHolder;
import configs.GameGraphicConstants;
import models.Card;
import models.cards.Minion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class PlayerBoardViewer extends JPanel {

    private final int size;
    private final double width;
    private final GameGraphicConstants constants = new GameGraphicConstants();

    private CardDrawer drawer;

    private List<Minion> cardsToPaint;
    private MinionListHolder minionListHolder;

    boolean highlightEmptySpots;

    public PlayerBoardViewer(int size, double width, MinionListHolder minionListHolder) {
        this.size = size;
        this.width = width;
        this.minionListHolder = minionListHolder;

        init();
        update(false);
    }

    private void init() {
        setOpaque(false);
        setPreferredSize(new Dimension((int) width, -1));

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (drawer == null) drawer = new CardDrawer();

        drawer.setDrawAbilities(true);
        drawCards(g);
    }

    private void drawCards(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        for (int i = 0; i < size; i++) {
            int x = getCardX(i);

            if (cardsToPaint.size() <= i) {
                continue;
            }
            if (cardsToPaint.get(i) == null) {
                if (highlightEmptySpots) {
                    g.setColor(Color.WHITE);
                    double scale = 0.9;
                    g.drawRect(x, 0, (int) (constants.getCardWidth() * scale), (int) (constants.getCardHeight() * scale));
                }
                continue;
            }
            drawer.drawCard(g2d, cardsToPaint.get(i), true, false, x, 0);
        }
    }

    public void update(boolean highlightEmptySpots) {
        this.cardsToPaint = minionListHolder.getMinion();
        this.highlightEmptySpots = highlightEmptySpots;
        repaint();
    }


    public int getIndex(Point point) {
        return (int) Math.floor(point.getX() / width * size);
    }

    public int getCardX(int index) {
        return (int) (index * (width / size));
    }

    public int getCardX(Point point) {
        return getCardX(getIndex(point));
    }

    public String getCardInfo(Point point) {
        int index = getCardX(getIndex(point));

        if (index >= 0 && index < cardsToPaint.size()) {
            if (minionListHolder.getMinion().get(index) != null)
                return minionListHolder.getMinion().get(index).getDescription() + "\nAbilities: " + cardsToPaint.get(index).getAbilities().toString();
        }
        return "";
    }

}
