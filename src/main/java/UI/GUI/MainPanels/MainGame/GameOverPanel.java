package UI.GUI.MainPanels.MainGame;

import UI.GUI.MyPanel;
import UI.GUI.SubPanel.BackAndExitPanel;
import network.client.Client;

import javax.swing.*;
import java.awt.*;

public class GameOverPanel extends MyPanel {

    private boolean wonGame;
    private BackAndExitPanel backAndExitPanel;
    private int fontSize;

    public GameOverPanel(Client client, boolean wonGame) {
        super(client);
        this.wonGame = wonGame;
        this.backAndExitPanel = new BackAndExitPanel(client);
        fontSize = 40;

        configure();
    }

    private void configure() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        String string = (wonGame) ? "Victory :)))" : "You Lost :(";
        Color color = (wonGame) ? Color.YELLOW : Color.RED;

        g.setColor(color);

        Font font = new Font(panelsConfigs.getFont(), Font.BOLD, panelsConfigs.getBigTitleSize());
        g.setFont(font);
        FontMetrics fontMetrics = g.getFontMetrics();
        int offsetX = fontMetrics.stringWidth(string) / 2;
        g.drawString(string, (int) (graphicConstants.getGameWidth() / 2 - offsetX), (int) (graphicConstants.getGameHeight() / 2 - fontSize / 2));
    }
}
