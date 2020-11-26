package UtilSwing.Drawers;

import UtilSwing.ImageLoader;
import models.InfoPassive;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class PassiveDrawer extends MainDrawer {

    public PassiveDrawer() {
        super();
    }

    public void drawPassive(Graphics g, InfoPassive passive, int x, int y) {
        set(g, x, y);

        g.drawImage(getImage(passive), x, y, (int) graphicConstants.getCardWidth(), (int) graphicConstants.getCardHeight(), null);
    }

    private BufferedImage getImage(InfoPassive passive) {
        String name = passive.getName();

        if (!map.containsKey(name)) {
            map.put(name, ImageLoader.loadImage(getPassiveLocation(passive)));
        }

        return map.get(name);
    }

    private void set(Graphics g, int x, int y) {
        this.x = x;
        this.y = y;
        this.g = g;
    }

    private File getPassiveLocation(InfoPassive passive) {
        return new File(pathConfigs.getPassiveImages() + "/" + passive.getName().toLowerCase() + ".png");
    }

}
