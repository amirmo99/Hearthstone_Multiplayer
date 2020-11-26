package UtilSwing.Drawers;

import UtilSwing.ImageLoader;
import models.HeroPower;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class HeroPowerDrawer extends MainDrawer {

    public HeroPowerDrawer() {
        super();
    }

    public void drawHeroPower(Graphics g, HeroPower power, int x, int y) {
        set(g, x, y);

        drawFrame(power);
        g.drawImage(getImage(power), x, y, (int) graphicConstants.getPowerWidth(), (int) graphicConstants.getPowerHeight(), null);

        if (!power.isPassive())
            drawNumber(power.getMana(), (int) (graphicConstants.getPowerManaX()), (int) (graphicConstants.getPowerManaY()));
    }

    private BufferedImage getImage(HeroPower power) {
        String name = power.getName();

        if (!map.containsKey(name)) {
            map.put(name, ImageLoader.loadImage(getPowerLocation(power)));
        }

        return map.get(name);
    }

    private void drawFrame(HeroPower power) {
        Color color = getFrameColor(power.getSituation());

        if (color != null) {
            g.setColor(color);
            g.fillRect(x, y, (int) graphicConstants.getPowerWidth(), (int) graphicConstants.getPowerHeight());
        }
    }

    private void set(Graphics g, int x, int y) {
        this.x = x;
        this.y = y;
        this.g = g;
    }

    public File getPowerLocation(HeroPower power) {
        return new File(pathConfigs.getHeroPowerImages() + "/" + power.getName().toLowerCase() + ".png");
    }
}
