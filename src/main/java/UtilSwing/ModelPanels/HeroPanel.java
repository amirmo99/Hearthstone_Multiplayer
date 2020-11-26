package UtilSwing.ModelPanels;

import UtilSwing.Drawers.CardDrawer;
import UtilSwing.Drawers.HeroDrawer;
import abstracts.HeroHolder;
import abstracts.Updatable;
import models.HeroPower;
import models.Heroes;

import javax.swing.*;
import java.awt.*;

public class HeroPanel extends ModelPanel implements Updatable {

//    private final Heroes mainHero;
    HeroDrawer heroDrawer;
    CardDrawer cardDrawer;
    private boolean includeWeapon = false;

    private HeroHolder heroHolder;

    public HeroPanel(HeroHolder heroHolder) {
        super();
//        this.mainHero = hero;
        this.heroHolder = heroHolder;
        this.heroDrawer = new HeroDrawer();
        this.cardDrawer = new CardDrawer();
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
        heroDrawer.drawHero(g, heroHolder.getHero(), 0, 0);

        if (heroHolder.getHero().getWeapon() == null) {
            includeWeapon = false;
        } else if (includeWeapon) {
            cardDrawer.drawCard(g, heroHolder.getHero().getWeapon(), true, false, (int) constants.getHeroWidth(), 0);
        }
        
    }

    @Override
    public void update() {
//        hero = mainHero.cloned();
        includeWeapon = true;
    }

    public Heroes getHero() {
        return heroHolder.getHero();
    }

//    public void setHero(Heroes hero) {
//        this.heroHolder.getHero() = hero;
//    }
}
