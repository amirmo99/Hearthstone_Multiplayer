package UtilSwing.Drawers;

import UtilSwing.ImageLoader;
import models.Heroes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class HeroDrawer extends MainDrawer {

    public HeroDrawer() {
        super();
    }

    public void drawHero(Graphics g, Heroes hero, int x, int y) {
        set(g, x, y);

        drawFrame(hero);
        g.drawImage(getImage(hero), x, y, (int) graphicConstants.getHeroWidth(), (int) graphicConstants.getHeroHeight(), null);

        Color color = (hero.isHealed()) ? Color.GREEN : (hero.isDamaged()) ? Color.RED : Color.WHITE;
        drawNumber(hero.getHP(), (int) (graphicConstants.getHeroHpX()), (int) (graphicConstants.getHeroHpY()), color);

        drawDefense(hero.getDefense());
    }

    private void drawDefense(int defense) {
        int x = (int) (this.x * 0.8 + graphicConstants.getHeroWidth() * 0.2);
        int y = (int) graphicConstants.getHeroHpY();

        g.drawImage(getShieldImage(), x - 30, y - 30, 60, 60, null);

        Color color = (defense > 0) ? Color.GREEN : Color.WHITE;
        drawNumber(defense, x, y + 7, color);
    }

    private BufferedImage getImage(Heroes hero) {
        String name = hero.getName();

        if (!map.containsKey(name)) {
            map.put(name, ImageLoader.loadImage(getHeroLocation(hero)));
        }

        return map.get(name);
    }

    private BufferedImage getShieldImage() {
        String path = pathConfigs.getHeroShield();
        return ImageLoader.loadImage(path);
    }

    private void drawFrame(Heroes hero) {
        Color color = getFrameColor(hero.getSituation());

        if (color != null) {
            g.setColor(color);
            g.fillRect(x, y, (int) graphicConstants.getHeroWidth(), (int) graphicConstants.getHeroHeight());
        }
    }

    private void set(Graphics g, int x, int y) {
        this.x = x;
        this.y = y;
        this.g = g;
    }

    private File getHeroLocation(Heroes hero) {

        String loc = pathConfigs.getHeroImages() + "/" + hero.getName().toLowerCase() + ".png";
        return new File(loc);
    }
}
