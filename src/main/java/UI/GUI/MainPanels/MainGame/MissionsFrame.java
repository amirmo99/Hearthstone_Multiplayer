package UI.GUI.MainPanels.MainGame;

import UI.GUI.MainFrame;
import UI.GUI.SubPanel.ModelViewerScroll;
import models.cards.Mission;
import network.client.Client;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MissionsFrame extends JFrame {

    private ModelViewerScroll modelViewerScroll;
    private JPanel noMissionsPanel;

    private Client client;

    public MissionsFrame(String title, Client client) {
        super(title);
        this.client = client;
        init();
    }

    private void init() {
        this.modelViewerScroll = new ModelViewerScroll(null, true, true, false, true, client);
        this.noMissionsPanel = new JPanel();
        makeEmptyPanel();

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void makeEmptyPanel() {
        noMissionsPanel.setLayout(new GridBagLayout());

        JLabel label = new JLabel("There is no active missions.");
        noMissionsPanel.add(label);
    }

    public void showMissions(List<Mission> missions) {
        if (missions.size() > 0) {
            modelViewerScroll.setModels(missions);
            setContentPane(modelViewerScroll);
        } else {
            setContentPane(noMissionsPanel);
        }
        setVisible(true);
    }
}
