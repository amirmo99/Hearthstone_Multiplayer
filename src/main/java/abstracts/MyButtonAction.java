package abstracts;

import models.GameModel;

import java.awt.event.ActionListener;

public abstract class MyButtonAction implements ActionListener, Prototype {

    private GameModel model;

    public abstract String getButtonLabel();

    public void setModel(GameModel model) {
        this.model = model;
    }

    public GameModel getModel() {
        return model;
    }

}
