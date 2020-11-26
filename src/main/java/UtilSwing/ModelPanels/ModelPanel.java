package UtilSwing.ModelPanels;

import abstracts.Updatable;
import configs.GameGraphicConstants;
import configs.PanelsConfigs;

import javax.swing.*;

public abstract class ModelPanel extends JPanel {

    protected PanelsConfigs panelsConfigs;
    protected GameGraphicConstants constants;

    public ModelPanel() {
        this.panelsConfigs = new PanelsConfigs();
        this.constants = new GameGraphicConstants();
    }
}
