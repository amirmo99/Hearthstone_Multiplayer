package UI.GUI.SubPanel;

import UI.GUI.MyPanel;
import UI.PlayerMapper;
import enums.LogType;
import network.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BackAndExitPanel extends MyPanel {

    private JButton back;
    private JButton exit;

    public BackAndExitPanel(Client client) {
        super(client);
        init();
        configureElements();
        placeComponents();
    }

    private void init() {
        exit = new JButton("Exit");
        back = new JButton("Back");
    }

    public void configureElements() {
        Dimension dim = new Dimension(panelsConfigs.getLoginButtonsWidth(), panelsConfigs.getHomeButtonsHeight());
        configureButton(exit, dim, panelsConfigs.getHomeFontSize(), new MyButtonAction());
        configureButton(back, dim, panelsConfigs.getHomeFontSize(), new MyButtonAction());
    }

    private void placeComponents() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.gridy = 0;
        gc.fill = GridBagConstraints.NONE;

        //Column 1
        gc.weightx = 1;
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_START;
        add(back, gc);
        //Column 2
        gc.weightx = 1;
        gc.gridx++;
        gc.anchor = GridBagConstraints.LINE_END;
        add(exit, gc);
    }

    private class MyButtonAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JButton button = (JButton) actionEvent.getSource();

            if (button == exit) {
                exitAction();
            } else if (button == back) {
                client.getPlayerMapper().getLogger().writeLog(LogType.BUTTON_CLICK, "Back Button");
                changePanel("home");
            }
        }
    }

    private void exitAction() {
        int report = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "QUIT GAME?",JOptionPane.OK_CANCEL_OPTION);
        if (report == JOptionPane.OK_OPTION) {
            client.getPlayerMapper().getLogger().writeLog(LogType.BUTTON_CLICK, "Exit Button");
            client.getPlayerMapper().signPlayerOut();
            System.exit(0);
        }
    }

    public JButton getBack() {
        return back;
    }
}
