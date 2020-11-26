package UI.GUI;

import UI.GUI.MainPanels.*;
import UI.GUI.MainPanels.MainGame.GameBoardPanel;
import UI.PlayerMapper;
import Util.BundleRankTableModels;
import Util.Monitor;
import configs.GameGraphicConstants;
import configs.PanelsConfigs;
import enums.LogType;
import logic.Player;
import network.api.Message;
import network.client.Client;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private String activePanel;
    private boolean connected;

    private GameBoardPanel gameBoardPanel;

    private Client client;

    public MainFrame(Client client) {
        super("HearthStone");
        this.client = client;
        // Start
        init();
        connectToServer();
        // SET LOGIN AS FIRST PANEL
        goToLoginPanel();
    }

    private void init() {
        GameGraphicConstants constants = new GameGraphicConstants();

        UIManager.put("TabbedPane.contentOpaque", false);
        UIManager.put("ScrollPane.contentOpaque", false);

        activePanel = "login";
        connected = false;

        setLayout(new BorderLayout());
        setSize((int)constants.getGameWidth(),(int) constants.getGameHeight());
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void showError(String error) {
        JOptionPane.showConfirmDialog(this, error, "Error", JOptionPane.DEFAULT_OPTION,
                JOptionPane.ERROR_MESSAGE);
    }

    public void goToHomePanel() {
        setActivePanel("home");
    }

    public void makeGamePanel(boolean isGameLocal, boolean isPlayerOne) {
        gameBoardPanel = new GameBoardPanel(client, isGameLocal, isPlayerOne);
        setActivePanel("play");
//        gameBoardPanel.start(true);
    }

    protected void setActivePanel(String panelName) {
        if (!connected)
            return;
        if (client.getPlayerMapper().isPlayerLoggedIn() && !panelName.equals("play") && !panelName.equals("login"))
            client.requestPlayerUpdate();

        switch (panelName.toLowerCase()) {
            case "login":
                setContentPane(new LoginPanel(client));
                break;
            case "home":
                setContentPane(new HomePanel(client));
                break;
            case "play":
                setContentPane(gameBoardPanel);
                break;
            case "store":
                setContentPane(new StorePanel(client));
                break;
            case "collections":
                setContentPane(new CollectionsPanel(client));
                break;
            case "status":
                setContentPane(new StatusPanel(client));
                break;
            case "ranks":
                setContentPane(new RanksPanel(client));
                break;
        }
        if (!activePanel.equals(panelName)) {
            client.getPlayerMapper().getLogger().writeLog(LogType.ROOM_CHANGE, panelName);
        }
        activePanel = panelName;
        setVisible(true);
    }


    public void updatePanels() {
        setActivePanel(activePanel);
    }

    public void showGoToShopOption() {
        int report = JOptionPane.showConfirmDialog(null, "You Do Not Have This Card" +
                "\nDo You Want To Check Shop For This Card?", "Warning", JOptionPane.YES_NO_OPTION);
        if (report == JOptionPane.YES_OPTION)
            setActivePanel("Store");
    }

    public void connectToServer() {
        connected = false;

        while (!connected) {
            String portString = JOptionPane.showInputDialog(null, "Enter server port", "8080");
            String ip = JOptionPane.showInputDialog(null, "Enter server ip", "localhost");
            try {
                int port = Integer.parseInt(portString);
                connected = client.connect(port, ip);
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Could not connect to server!\n"+e.getMessage()+"\nPlease try again",
                        "Connection Error", JOptionPane.ERROR_MESSAGE);
            }

            System.out.println(connected);

        }

        connected = true;
    }

    public void updateRankPanel(BundleRankTableModels tableModels) {
        if (getContentPane() instanceof RanksPanel)
            ((RanksPanel) getContentPane()).build(tableModels);
    }

    public GameBoardPanel getGameBoardPanel() {
        return gameBoardPanel;
    }

    public void goToLoginPanel() {
        setActivePanel("login");
    }
}
