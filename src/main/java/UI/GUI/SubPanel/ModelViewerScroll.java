package UI.GUI.SubPanel;

import abstracts.MyButtonAction;
import models.GameModel;
import network.client.Client;

import javax.swing.*;
import java.util.List;

public class ModelViewerScroll extends JScrollPane {

    private final ModelViewerPanel panel;

    public ModelViewerScroll(MyButtonAction cardAction, boolean onlyOneRow, boolean viewOnly, boolean showLock, boolean drawAbilities, Client client) {
        panel = new ModelViewerPanel(cardAction, onlyOneRow, viewOnly, showLock, drawAbilities, client);
        init();
    }

    public void init() {
        setViewportView(panel);

        panel.setOpaque(false);
        setOpaque(false);
        getViewport().setOpaque(false);
    }

    public void setModels(List<? extends GameModel> models) {
        panel.setModels(models);
    }

}
