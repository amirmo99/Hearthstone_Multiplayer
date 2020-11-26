package UtilSwing.Drawers;

import configs.GameGraphicConstants;
import configs.PathConfigs;
import enums.CardSituation;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public abstract class MainDrawer {

    protected final Font smallNumberFont = new Font(Font.SANS_SERIF, Font.BOLD, 24);
    protected final Font bigNumberFont = new Font(Font.SANS_SERIF, Font.BOLD, 30);
    protected final PathConfigs pathConfigs = new PathConfigs();
    protected GameGraphicConstants graphicConstants = new GameGraphicConstants();
    protected int cardX1, cardX2, cardY1, cardY2;

    protected Graphics g;
    protected int x, y;

    protected HashMap<String, BufferedImage> map;

    public MainDrawer() {
        map = new HashMap<>();
        cardX1 = (int) (graphicConstants.getCardX1());
        cardY1 = (int) (graphicConstants.getCardY1());
        cardX2 = (int) (graphicConstants.getCardX2());
        cardY2 = (int) (graphicConstants.getCardY2());
    }

    protected void drawNumber(int number, int x, int y, Color color, Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = bigNumberFont;

        String num = String.valueOf(number);

        g2d.setFont(font);
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int offsetX = fontMetrics.stringWidth(num) / 2;

        g2d.translate(x - offsetX, y);
        GlyphVector v = font.createGlyphVector(g2d.getFontMetrics(font).getFontRenderContext(), num);
        Shape s = v.getOutline();
        g2d.setColor(Color.BLACK);
        g2d.draw(s);
        g2d.setColor(color);
        g2d.fill(s);

        g2d.translate(-(x - offsetX), -y);
    }

    protected void drawNumber(int number, int x, int y) {
        drawNumber(number, x, y, Color.WHITE, g);
    }

    protected void drawNumber(int number, int x, int y, Color color) { drawNumber(number, x, y, color, g);}

    protected Color getFrameColor(CardSituation situation) {
        Color color = null;
        switch (situation) {
            case ATTACKING:
                color = Color.BLUE;
                break;
            case PLAYABLE:
                color = Color.GREEN;
                break;
            case SELECTABLE:
                color = Color.WHITE;
                break;
            case VULNERABLE:
                color = Color.RED;
                break;
        }
        return color;
    }
}
