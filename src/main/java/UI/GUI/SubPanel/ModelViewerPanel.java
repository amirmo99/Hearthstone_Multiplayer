package UI.GUI.SubPanel;

import UI.PlayerMapper;
import UtilSwing.ModelPanels.CardPanel;
import UtilSwing.ModelPanels.InfoPassivePanel;
import UtilSwing.ModelPanels.ModelPanel;
import abstracts.MyButtonAction;
import models.Card;
import models.GameModel;
import models.InfoPassive;
import network.client.Client;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModelViewerPanel extends JPanel {

    private List<GameModel> models;
    private final MyButtonAction buttonAction;
    private final boolean onlyOneRow;
    private final boolean viewOnly;
    private final boolean showLock;
    private final boolean drawAbilities;

    private Client client;

    public ModelViewerPanel(MyButtonAction cardAction, boolean onlyOneRow, boolean viewOnly, boolean showLock, boolean drawAbilities, Client client) {
        super();
        this.client = client;
        this.buttonAction = cardAction;
        this.onlyOneRow = onlyOneRow;
        this.viewOnly = viewOnly;
        this.showLock = showLock;
        this.drawAbilities = drawAbilities;

        config();
    }

    public void build() {
        removeAll();
        placeSeparately();

        revalidate();
        repaint();
    }

    private void config() {
        setLayout(new GridBagLayout());
        setOpaque(false);
    }


    private void placeSeparately() {

        GridBagConstraints gc = new GridBagConstraints();

        gc.gridy = 0;
        gc.gridx = 0;
        gc.fill = GridBagConstraints.HORIZONTAL;

        for (GameModel model : models) {
            if (viewOnly)
                add(createModel(model), gc);
            else
                add(createModelAndButton(model), gc);

            if (onlyOneRow) {
                gc.gridx++;
            } else {
                if (gc.gridy == 1)
                    gc.gridx++;
                gc.gridy = (gc.gridy + 1) % 2;
            }
        }
    }

    private JSplitPane createModelAndButton(GameModel model) {
        ModelPanel modelMaker = createModel(model);
        JButton button = new JButton();
        if (buttonAction != null) {
            MyButtonAction buttonAction = (MyButtonAction) this.buttonAction.cloned();
            buttonAction.setModel(model);
            button.addActionListener(buttonAction);
            String buttonLabel = buttonAction.getButtonLabel();
            button.setText(buttonLabel);
        }
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, modelMaker, button);
        splitPane.setOpaque(false);
        return splitPane;
    }

    private ModelPanel createModel(GameModel model) {
        ModelPanel modelPanel = null;
        if (model instanceof Card) {
            Card card = (Card) model;

            boolean lock = (showLock) && client.getPlayerMapper().isCardLocked(card);

            modelPanel = new CardPanel(card, lock, drawAbilities);
            modelPanel.setToolTipText(card.getDescription());
        } else if (model instanceof InfoPassive) {
            InfoPassive infoPassive = (InfoPassive) model;

            modelPanel = new InfoPassivePanel(infoPassive);
            modelPanel.setToolTipText(infoPassive.getDescription());
        } else {
            System.out.println("Could not create model : " + model.getName());
        }

        return modelPanel;
    }


    public void setModels(List<? extends GameModel> models) {
        this.models = new ArrayList<>();
        this.models.addAll(models);
        build();
    }


}
