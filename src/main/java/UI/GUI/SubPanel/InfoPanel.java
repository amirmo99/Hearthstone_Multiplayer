package UI.GUI.SubPanel;

import UI.GUI.MyPanel;
import UI.PlayerMapper;
import network.client.Client;

import javax.swing.*;
import java.awt.*;

public class InfoPanel extends MyPanel {

    private JTextField playerName;
    private JTextField playerGems;
    private final boolean detailed;

    public InfoPanel(Client client, boolean detailed) {
        super(client);
        this.detailed = detailed;
        init();
        configureElements();
        placeComponents();
    }

    private void init() {
        if (detailed)
            playerName = new JTextField(client.getPlayerMapper().getPlayerName());
        else
            playerName = new JTextField("Welcome, " + client.getPlayerMapper().getPlayerName());

        playerGems = new JTextField("Gems : " + client.getPlayerMapper().getPlayerGems());
    }

    private void configureElements() {
        setOpaque(false);
        configureField(playerGems, 30, false, panelsConfigs.getLabelThemeColor());
        configureField(playerName, 30, false, panelsConfigs.getLabelThemeColor());

    }

    private void placeComponents() {
        if (detailed) {
            setLayout(new GridBagLayout());
            GridBagConstraints gc = new GridBagConstraints();
            gc.gridy = 0;
            gc.fill = GridBagConstraints.NONE;
            //Column 1
            gc.weightx = 1;
            gc.gridx = 0;
            gc.anchor = GridBagConstraints.LINE_START;
            add(playerName, gc);
            //Column 2
            gc.weightx = 1;
            gc.gridx++;
            gc.anchor = GridBagConstraints.LINE_END;
            add(playerGems, gc);
        } else {
            add(playerName);
        }
    }
}
