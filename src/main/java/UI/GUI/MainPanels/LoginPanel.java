package UI.GUI.MainPanels;

import UI.GUI.MyPanel;
import UI.PlayerMapper;
import jdk.nashorn.internal.scripts.JO;
import network.client.Client;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends MyPanel {

    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton signIn;
    private JButton signUp;

    public LoginPanel(Client client) {
        super(client);
        drawBackground(pathConfigs.getHomeBGImage());
        init();
        createTitle("HearthStone");
        placeComponents();
    }

    private void init() {
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        usernameLabel = new JLabel("Username : ");
        passwordLabel = new JLabel("Password : ");
        signIn = new JButton("Sign In");
        signUp = new JButton("Sign Up");

        Dimension dim = new Dimension(panelsConfigs.getLoginButtonsWidth(), panelsConfigs.getLoginButtonsHeight());
        configureButton(signIn, dim, panelsConfigs.getLoginButtonFontSize(), new ButtonAction());
        configureButton(signUp, dim, panelsConfigs.getLoginButtonFontSize(), new ButtonAction());

        configureField(usernameField, panelsConfigs.getLoginFieldFontSize(), true, Color.BLACK);
        configureField(passwordField, panelsConfigs.getLoginFieldFontSize(), true, Color.BLACK);

        configureLabel(usernameLabel, panelsConfigs.getLoginLabelFontSize(), panelsConfigs.getLabelThemeColor());
        configureLabel(passwordLabel, panelsConfigs.getLoginLabelFontSize(), panelsConfigs.getLabelThemeColor());
    }

    private void signInPlayer(String user, String pass) {
        boolean success = client.getPlayerMapper().loginRequest(user, pass);
        if (success)
            sendPlayerInGame();
    }

    private void signUpPlayer(String user, String pass) {
        boolean success = client.getPlayerMapper().signUpRequest(user, pass);
        if (success)
            sendPlayerInGame();
    }

    public void sendPlayerInGame() {
        changePanel("Home");
    }

    private void placeComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.fill = GridBagConstraints.NONE;
        gc.insets = new Insets(10, 0, 0, 20);
        gc.weighty = 5;

        gc.ipadx = 250;
        // Row 1
        gc.gridy = 0;
        gc.gridx = 0;
        gc.weightx = 0.5;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.LAST_LINE_END;
        add(usernameLabel, gc);

        gc.gridx = 1;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.LAST_LINE_START;
        add(usernameField, gc);
        //Row 2
        gc.gridy++;
        gc.gridx = 0;
        gc.weightx = 0.5;
        gc.weighty = 0.5;
        gc.gridwidth = 1;
        gc.anchor = GridBagConstraints.FIRST_LINE_END;
        add(passwordLabel, gc);

        gc.gridx = 1;
        gc.gridwidth = 2;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(passwordField, gc);

        // Row 3
        gc.insets = new Insets(20, 40, 0, 0);

        gc.gridy++;
        gc.gridwidth = 1;
        gc.ipadx = 10;
        gc.weightx = 0.5;
        gc.weighty = 6;
        gc.anchor = GridBagConstraints.CENTER;

        gc.gridx = 0;
        gc.anchor = GridBagConstraints.FIRST_LINE_END;
        add(signIn, gc);

        gc.weightx = 1;
        gc.gridx = 2;
        gc.anchor = GridBagConstraints.FIRST_LINE_START;
        add(signUp, gc);
    }

    private class ButtonAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JButton button = (JButton) actionEvent.getSource();
            String user = usernameField.getText();
            String pass = String.valueOf(passwordField.getPassword());

            if (button == signIn) {
                signInPlayer(user, pass);
            } else if (button == signUp) {
                signUpPlayer(user, pass);
            }
        }

    }
}
