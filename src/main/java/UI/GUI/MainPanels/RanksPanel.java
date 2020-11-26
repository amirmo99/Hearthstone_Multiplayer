package UI.GUI.MainPanels;

import UI.GUI.MyPanel;
import UI.GUI.SubPanel.BackAndExitPanel;
import Util.BundleRankTableModels;
import network.api.Message;
import network.api.MessageType;
import network.client.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class RanksPanel extends MyPanel {

    private JTabbedPane tabbedPane;
    private JTable topTen, aroundPlayer;
    private BackAndExitPanel backAndExitPanel;

    public RanksPanel(Client client) {
        super(client);

        drawBackground(pathConfigs.getHomeBGImage());
        createTitle("Ranks");
        init();

        sendUpdateRequest();
    }

    private void init() {
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        this.backAndExitPanel = new BackAndExitPanel(client);

        add(backAndExitPanel, BorderLayout.SOUTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void sendUpdateRequest() {
        client.sendMessage(new Message(MessageType.RANKS_UPDATE_REQUEST));
    }

    public void build(BundleRankTableModels tableModels) {
        topTen = new JTable(tableModels.getTopTen());
        aroundPlayer = new JTable(tableModels.getAroundPlayer());

        centerAlignedTables();
        placeComponents();
    }

    private void centerAlignedTables() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( JLabel.CENTER );
//        topTen.setDefaultRenderer(String.class, centerRenderer);
        topTen.setDefaultRenderer(int.class, centerRenderer);
//        aroundPlayer.setDefaultRenderer(String.class, centerRenderer);
        aroundPlayer.setDefaultRenderer(int.class, centerRenderer);
    }

    private void placeComponents() {
        tabbedPane.addTab("Top Ten", new JScrollPane(topTen));
        tabbedPane.addTab("Your Rank", new JScrollPane(aroundPlayer));

        tabbedPane.updateUI();
        repaint();
    }
}
