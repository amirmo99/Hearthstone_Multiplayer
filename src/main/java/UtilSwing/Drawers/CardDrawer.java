package UtilSwing.Drawers;

import UtilSwing.ImageLoader;
import enums.MinionAbility;
import models.cards.Minion;
import models.cards.Mission;
import models.cards.Weapon;
import models.Card;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class CardDrawer extends MainDrawer {

    private Card card;
    private boolean drawAbilities = false;

    public CardDrawer() {
        super();
    }

    public void set(Graphics g, Card card, int x, int y) {
        this.x = x;
        this.y = y;
        this.card = card;
        this.g = g;
    }

    public void drawCard(Graphics g, Card card, boolean front, boolean locked, int x, int y) {
        set(g, card, x, y);
        if (!front || card == null) {
            drawBackOfCard();
        }
        else {
            drawFrame();
            drawCardImage();
            // drawing mana:
            drawNumber(card.getMana(), x + cardX1, y + cardY1);
            // draw stats:
            if (locked)
                showLock();
            if (card instanceof Minion)
                drawMinion();
            else if (card instanceof Weapon)
                drawWeapon();
            else if (card instanceof Mission)
                drawMission();
        }
    }

    private void drawFrame() {
//        if (card == null) return;
        Color color = getFrameColor(card.getSituation());

        if (color != null) {
            g.setColor(color);
            double scale = 1;
            g.fillRect(x, y, (int) (graphicConstants.getCardWidth() * scale), (int) (graphicConstants.getCardHeight() * scale));
        }
    }

    private void drawMission() {
        Mission mission = (Mission) card;
        double progress = Math.min((double) mission.getProgress() / (double) mission.getGoal(), 1);

        int offset = 10;

        int x = this.x + offset;
        int y = (int) (this.y + graphicConstants.getCardHeight() * 0.95);
        int width = (int) graphicConstants.getCardWidth() - 2 * offset;
        int height = 10;

        if (drawAbilities)
            drawProgressBar(progress, x, y, width, height);
    }

    private void drawProgressBar(double progress, int x, int y, int width, int height) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x, y, width, height, 10, 5);

        g.setColor(Color.CYAN);
        g.fillRoundRect(x, y, (int) (width * progress), height, 10, 5);

        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, width, height, 10, 5);
    }

    private void drawMinion() {
        Minion minion = (Minion) card;
        Color color;

        color = (minion.isDamaged()) ? Color.RED : (minion.isHealed()) ? Color.GREEN : Color.WHITE;
        drawNumber(minion.getHP(), x + cardX2, y + cardY2, color);

        color = (minion.isEmpowered()) ? Color.GREEN : Color.WHITE;
        drawNumber(minion.getAttack(), x + cardX1, y + cardY2, color);

        if (drawAbilities) drawMinionAbilities();
    }

    private void drawMinionAbilities() {
        Minion minion = (Minion) card;

        if (minion.getAbilities().contains(MinionAbility.TAUNT))
            drawTaunt();
        if (minion.getAbilities().contains(MinionAbility.STEALTH))
            drawStealth();
        if (minion.getAbilities().contains(MinionAbility.DIVINE_SHIELD))
            drawDivineShield();
    }

    private void drawTaunt() {
        int offset = 0;

        g.setColor(Color.BLACK);
        for (int i = offset; i <= offset + 2; i++) {
            g.drawRect(x + i, y + i, (int) graphicConstants.getCardWidth() - 2*i, (int) graphicConstants.getCardHeight() - 2*i);
        }
    }

    private void drawStealth() {
        int offset = 3;

        g.setColor(Color.GRAY);
        for (int i = offset; i <= offset + 2; i++) {
            g.drawRect(x + i, y + i, (int) graphicConstants.getCardWidth() - 2*i, (int) graphicConstants.getCardHeight() - 2*i);
        }
    }

    private void drawDivineShield() {
        int offset = 6;

        g.setColor(Color.YELLOW);
        for (int i = offset; i <= offset + 2; i++) {
            g.drawRect(x + i, y + i, (int) graphicConstants.getCardWidth() - 2*i, (int) graphicConstants.getCardHeight() - 2*i);
        }
    }
    private void drawWeapon() {
        Weapon weapon = (Weapon) card;

        drawNumber(weapon.getAttack(), x + cardX1, y + cardY2);
        drawNumber(weapon.getDurability(), x + cardX2, y + cardY2);
    }

    private void showLock() {
        File file = new File(pathConfigs.getCardLock());
        BufferedImage bg = ImageLoader.loadImage(file);
        g.drawImage(bg, (int) (x + graphicConstants.getCardWidth() / 2), y, null);

    }

    private void drawBackOfCard() {
        BufferedImage bg = ImageLoader.loadImage(getBackCardLocation());
        g.drawImage(bg, x, y, (int) graphicConstants.getCardWidth(), (int) graphicConstants.getCardHeight(), null);
    }

    private void drawCardImage() {
        BufferedImage bg = getImage(card);
        g.drawImage(bg, x, y, (int) graphicConstants.getCardWidth(), (int) graphicConstants.getCardHeight(), null);
    }

    private BufferedImage getImage(Card card) {
        String name = card.getName();

        if (!map.containsKey(name)) {
            map.put(name, ImageLoader.loadImage(getCardLocation(card)));
        }

        return map.get(name);
    }

    // Get locations ---------- To be moved later

    private File getCardLocation(Card card) {
        String pic = card.getName().toLowerCase();

        return new File(pathConfigs.getCardImages() + "/" + pic + ".png");
    }


    private File getBackCardLocation() {
        String pic = "back1";
        return new File(pathConfigs.getCardImages() + "/" + pic + ".png");
    }

    public void setDrawAbilities(boolean drawAbilities) {
        this.drawAbilities = drawAbilities;
    }
}
