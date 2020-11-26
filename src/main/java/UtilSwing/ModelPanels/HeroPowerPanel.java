package UtilSwing.ModelPanels;

import UtilSwing.Drawers.HeroPowerDrawer;
import abstracts.PowerHolder;
import models.HeroPower;

import java.awt.*;

public class HeroPowerPanel extends ModelPanel {

    HeroPowerDrawer drawer;

    private PowerHolder powerHolder;

    public HeroPowerPanel(PowerHolder powerHolder) {
        super();
        this.powerHolder = powerHolder;
        drawer = new HeroPowerDrawer();
        configure();
        repaint();
    }

    private void configure() {
        setPreferredSize(new Dimension(200, 250));
        setOpaque(false);
        setBorder(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawer.drawHeroPower(g, powerHolder.getPower(), 0, 0);
    }

    public HeroPower getHeroPower() {
        return powerHolder.getPower();
    }

//    public void setHeroPower(PowerHolder power) {
//        this.powerHolder = power;
//    }
}

