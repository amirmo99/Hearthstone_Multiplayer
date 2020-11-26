package UtilSwing.ModelPanels;

import UtilSwing.Drawers.PassiveDrawer;
import models.InfoPassive;

import java.awt.*;

public class InfoPassivePanel extends ModelPanel {
    private final InfoPassive infoPassive;
    PassiveDrawer drawer;

    public InfoPassivePanel(InfoPassive infoPassive) {
        this.infoPassive = infoPassive;
        drawer = new PassiveDrawer();
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
        drawer.drawPassive(g, infoPassive, 0, 0);
    }
}
