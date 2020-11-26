package UI.GUI.MainPanels;

import UI.GUI.MyPanel;
import enums.LogType;
import network.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class HomePanel extends MyPanel {

    private JButton playLocal;
    private JButton playFromFile;
    private JButton playWithCPU;
    private JButton playOnline;
    private JButton collections;
    private JButton store;
    private JButton status;
    private JButton ranks;
    private JButton signOut;
    private JButton delete;
    private JButton exit;

    public HomePanel(Client client) {
        super(client);
        drawBackground(pathConfigs.getHomeBGImage());
        init();
        configureElements();
        placeComponents();
    }

    private void init() {
        playLocal = new JButton("Local Play");
        playFromFile = new JButton("Play From File");
        playWithCPU = new JButton("Play With CPU");
        playOnline = new JButton("Play Online");
        store = new JButton("Store");
        collections = new JButton("Collections");
        status = new JButton("Status");
        ranks = new JButton("Ranks");
        signOut = new JButton("Sign Out");
        delete = new JButton("Delete Account");
        exit = new JButton("Exit");
    }

    private void configureElements() {
        Dimension dimension = new Dimension(panelsConfigs.getHomeButtonsWidth(), panelsConfigs.getHomeButtonsHeight());
        ButtonAction buttonAction = new ButtonAction();
        int fontSize = panelsConfigs.getHomeFontSize();

        configureButton(playLocal, dimension, fontSize, buttonAction);
        configureButton(playFromFile, dimension, fontSize, buttonAction);
        configureButton(playWithCPU, dimension, fontSize, buttonAction);
        configureButton(playOnline, dimension, fontSize, buttonAction);
        configureButton(store, dimension, fontSize, buttonAction);
        configureButton(collections, dimension, fontSize, buttonAction);
        configureButton(status, dimension, fontSize, buttonAction);
        configureButton(ranks, dimension, fontSize, buttonAction);
        configureButton(signOut, dimension, fontSize, buttonAction);
        configureButton(delete, dimension, fontSize, buttonAction);
        configureButton(exit, dimension, fontSize, buttonAction);

    }

    private void placeComponents() {
        setLayout(new BorderLayout());

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        placeButtons(buttonsPanel);
        add(buttonsPanel, BorderLayout.CENTER);

        JPanel playerInfo = new JPanel();
        playerInfo.setOpaque(false);
        makeWelcomePanel(playerInfo);
        add(playerInfo, BorderLayout.NORTH);

    }

    private void makeWelcomePanel(JPanel panel) {
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel name = new JLabel("Welcome " + client.getPlayerMapper().getPlayerName());
        configureLabel(name, panelsConfigs.getSmallTitleSize(), Color.WHITE);
        panel.add(name);
    }

    private void placeButtons(JPanel panel) {
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.weightx = 1;
        gc.gridx = 0;

        // Row 1
        gc.gridy = 0;
        panel.add(playLocal, gc);
        // Row 2
        gc.gridy++;
        panel.add(playFromFile, gc);
        // Row 3
        gc.gridy++;
        panel.add(playWithCPU, gc);
        // Row 4
        gc.gridy++;
        panel.add(playOnline, gc);

        gc.gridx = 1;
        gc.gridy = 0;
        gc.insets = new Insets(0, 20, 0, 0);
        // Row 1
        gc.gridy++;
        panel.add(collections, gc);
        // Row 2
        gc.gridy++;
        panel.add(store, gc);
        // Row 3
        gc.gridy++;
        panel.add(status, gc);
        // Row 4
        gc.gridy++;
        panel.add(ranks, gc);
        // Row 5
        gc.gridy++;
        panel.add(signOut, gc);
        // Row 6
        gc.gridy++;
        panel.add(delete, gc);
        // Row 7
        gc.gridy++;
        panel.add(exit, gc);
    }

    private void playLocalButton() {
            client.getGameMapper().createGame();
    }

    private void playFromFileButton() {
            client.getGameMapper().createGameFromFile();
    }

    private void playWithCPUButton() {
            client.getGameMapper().createGameWithCPU();
    }

    private void playOnline() {
        client.getGameMapper().createOnlineGame();
    }

    private void deleteAccountButton() {
        int report = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete your account?\nThis action will be permanent!",
                "Delete Account Confirmation", JOptionPane.YES_NO_OPTION);

        if (report == JOptionPane.YES_OPTION) {
            changePanel("Login");
            client.getPlayerMapper().deleteRequest();
        }
    }

    private void signOutButton() {
        System.out.println("hi");
        changePanel("Login");
        client.getPlayerMapper().signPlayerOut();
    }

    private class ButtonAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JButton button = (JButton) actionEvent.getSource();
            client.getPlayerMapper().getLogger().writeLog(LogType.BUTTON_CLICK, "button -> " + button.getText());

            if (button == playLocal) {
                playLocalButton();
            } else if (button == collections) {
                changePanel("Collections");
            } else if (button == store) {
                changePanel("Store");
            } else if (button == status) {
                changePanel("Status");
            } else if (button == ranks) {
                changePanel("Ranks"); /////////////////// **********************
            } else if (button == signOut) {
                signOutButton();
            } else if (button == exit) {
                System.exit(0);
            } else if (button == playFromFile) {
                playFromFileButton();
            } else if (button == playWithCPU) {
                playWithCPUButton();
            } else if (button == delete) {
                deleteAccountButton();
            } else if (button == playOnline) {
                playOnline();
            }
        }
    }
}
