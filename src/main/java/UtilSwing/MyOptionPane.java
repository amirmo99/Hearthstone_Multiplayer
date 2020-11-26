package UtilSwing;

import UI.GUI.SubPanel.ModelViewerScroll;
import abstracts.MyButtonAction;
import models.Card;
import models.InfoPassive;
import models.GameModel;
import network.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class MyOptionPane extends JOptionPane {

    private static GameModel model;
    private static JDialog dialog;

    private Client client;

    public MyOptionPane(Client client) {
        super();
        this.client = client;
    }

    public Card showCardChooser(List<Card> cards, String message) {

        model = null;
        ModelViewerScroll cardsViewer = new ModelViewerScroll(new MyAction(), true, false, false, false, client);
        cardsViewer.setPreferredSize(new Dimension(1000, 350));
        cardsViewer.setModels(cards);
        setMessage(message);
        setOptions(new ModelViewerScroll[]{cardsViewer});
        dialog = createDialog("Card Chooser");
        dialog.show();
        dialog.dispose();


        if (model != null)
            return (Card) model;
        else
            return null;
    }

    public InfoPassive showPassivesChooser(List<InfoPassive> infoPassives) {
        model = null;
        ModelViewerScroll passiveViewer = new ModelViewerScroll(new MyAction(), true, false, false, false, client);
        passiveViewer.setPreferredSize(new Dimension(1000, 350));
        passiveViewer.setModels(infoPassives);
        setMessage("Select a Passive");
        setOptions(new ModelViewerScroll[]{passiveViewer});
        dialog = createDialog("Passive Chooser");
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.show();
        dialog.dispose();

        if (model != null)
            return (InfoPassive) model;
        else
            return null;
    }

    public ArrayList<Card> showMultipleCardChooser(List<Card> cards, String title) {
        ArrayList<Card> selectedCards = new ArrayList<>();

        while (true) {
            Card card = showCardChooser(cards, title);
            if (card == null)
                break;
            selectedCards.add(card);
            cards.remove(card);
        }

        return selectedCards;
    }

    private

    static class MyAction extends MyButtonAction {
        @Override
        public MyButtonAction cloned() {
            return new MyAction();
        }

        @Override
        public String getButtonLabel() {
            return "Select";
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            model = getModel();
            dialog.dispose();
        }
    }

}

