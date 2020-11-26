package UI.GUI.MainPanels;

import abstracts.MyButtonAction;
import UI.GUI.MainFrame;
import UI.GUI.MyPanel;
import UI.GUI.SubPanel.BackAndExitPanel;
import UI.GUI.SubPanel.ModelViewerScroll;
import UI.GUI.SubPanel.DecksEditor;
import UI.GUI.SubPanel.InfoPanel;
import UI.PlayerMapper;
import enums.Class;
import enums.LogType;
import models.Card;
import models.Heroes;
import network.client.Client;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CollectionsPanel extends MyPanel {

    private static FilterPanel filterPanel;
    private static DecksEditor decksEditor;
    private JTabbedPane tabbedPane;
    private InfoPanel info;
    private BackAndExitPanel control;

    public CollectionsPanel(Client client) {
        super(client);
        drawBackground(pathConfigs.getHomeBGImage());
        createTitle("Collections");
        init();
        placeComponents();
    }

    private void init() {
        if (filterPanel == null)
            filterPanel = new FilterPanel(client);
        if (decksEditor == null)
            decksEditor = new DecksEditor(client);

        info = new InfoPanel(client ,true);
        control = new BackAndExitPanel(client);
        tabbedPane = new JTabbedPane();
        makeTabs();
    }

    private void makeTabs() {
        tabbedPane.removeAll();
        tabbedPane.setOpaque(false);
        ModelViewerScroll naturalViewer = new ModelViewerScroll(new AddToDeckAction(), false, false,
                true, false, client);
        naturalViewer.setModels(client.getPlayerMapper().filteredCards(Card.getAllCardsOfClass(Class.Natural),
                filterPanel.getManaNumber(), filterPanel.getSearchText(), filterPanel.getOwnedCardsOnly()));
        tabbedPane.addTab("Natural", naturalViewer);

        for (Heroes hero : Heroes.getAllHeroes()) {
            ModelViewerScroll heroCardsViewer = new ModelViewerScroll(new AddToDeckAction(), false, false, true, false, client);
            heroCardsViewer.setModels(client.getPlayerMapper().filteredCards(hero.getAllCardsOfThisClass(),
                    filterPanel.getManaNumber(), filterPanel.getSearchText(), filterPanel.getOwnedCardsOnly()));

            tabbedPane.addTab(hero.getName(), heroCardsViewer);
        }
    }

    private void placeComponents() {
        setOpaque(false);
        setLayout(new BorderLayout());

        add(info, BorderLayout.NORTH);
        add(control, BorderLayout.SOUTH);
        add(filterPanel, BorderLayout.WEST);
        add(tabbedPane, BorderLayout.CENTER);
        add(decksEditor, BorderLayout.EAST);
    }

    private class AddToDeckAction extends MyButtonAction {

        @Override
        public MyButtonAction cloned() {
            return new AddToDeckAction();
        }

        @Override
        public String getButtonLabel() {
            return "Add to selected deck";
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            client.getPlayerMapper().getLogger().writeLog(LogType.BUTTON_CLICK, "Add to deck button -> " + getModel().getName());
            client.getPlayerMapper().addCardToDeck((Card) getModel());
            client.requestPlayerUpdate();
            decksEditor.updateTable();
        }
    }
}


class FilterPanel extends MyPanel implements ActionListener {

    private String searchText;
    private String ownedCardsOnly;
    private int manaNumber;

    private JLabel mana;
    private JLabel search;
    private JLabel show;

    private JTextField cardName;
    private JComboBox<String> manaList;

    private ButtonGroup buttonGroup;
    private JRadioButton allCards;
    private JRadioButton ownedCards;
    private JRadioButton unownedCards;

    private JButton apply;

    public FilterPanel(Client client) {
        super(client);
        init();
        createTitle();
        configureElements();
        setupPanel();
    }

    private void init() {
        show = new JLabel("Show : ");
        mana = new JLabel("Mana : ");
        search = new JLabel("Search : ");
        cardName = new JTextField();
        manaList = new JComboBox<>();
        allCards = new JRadioButton("All Cards");
        ownedCards = new JRadioButton("Only Owned Cards");
        unownedCards = new JRadioButton("Only Unowned Cards");
        buttonGroup = new ButtonGroup();

        apply = new JButton("Apply");

        manaNumber = -1;
        ownedCardsOnly = "All Cards";
        searchText = "";

    }

    private void createTitle() {
        setOpaque(false);
        Border border = BorderFactory.createEmptyBorder(10, 0, 0, 0);
        TitledBorder titledBorder = BorderFactory.createTitledBorder("Filter");
        titledBorder.setTitleColor(Color.WHITE);

        setBorder(BorderFactory.createCompoundBorder(border, titledBorder));
    }

    public void configureElements() {
        makeRadioButton(allCards);
        makeRadioButton(ownedCards);
        makeRadioButton(unownedCards);
        makeManaList();

        Color labelColor = Color.WHITE;
        int size = 15;
        configureLabel(show, size, labelColor);
        configureLabel(mana, size, labelColor);
        configureLabel(search, size, labelColor);
        configureField(cardName, 15, true, Color.BLACK);
        configureButton(apply, null, 20, this);

        buttonGroup.add(allCards);
        buttonGroup.add(unownedCards);
        buttonGroup.add(ownedCards);
        allCards.setSelected(true);

    }

    private void setupPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        // Row 1
        gc.gridy = 0;
        gc.gridx = 0;
        gc.weighty = 0.1;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.LINE_END;
        gc.fill = GridBagConstraints.NONE;
        add(search, gc);
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.fill = GridBagConstraints.HORIZONTAL;
        add(cardName, gc);
        // Reset
        gc.fill = GridBagConstraints.NONE;
        // Row 2
        gc.gridy++;
        gc.gridx = 0;
        gc.weighty = 0.1;
        gc.weightx = 0.5;
        gc.anchor = GridBagConstraints.LINE_END;
        add(mana, gc);
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.LINE_START;
        add(manaList, gc);
        // Row 2.5
        gc.gridy++;
        gc.gridx = 0;
        gc.anchor = GridBagConstraints.LINE_END;
        add(show, gc);
        gc.gridx = 1;
        gc.anchor = GridBagConstraints.LINE_START;
        add(allCards, gc);
        // Row 3
        gc.gridy++;
        add(ownedCards, gc);
        // Row 4
        gc.gridy++;
        add(unownedCards, gc);
        //Row 5
        gc.gridy++;
        gc.gridx = 0;
        gc.gridwidth = 2;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weighty = 1;
        gc.anchor = GridBagConstraints.LAST_LINE_START;
        add(apply, gc);

    }

    private void makeManaList() {
        manaList.addItem("Any");
        for (int i = 0; i < 11; i++) {
            manaList.addItem(String.valueOf(i));
        }
        manaList.setSelectedIndex(0);
    }

    private void makeRadioButton(JRadioButton radioButton) {
        radioButton.setOpaque(false);
        radioButton.setForeground(Color.WHITE);
        radioButton.setActionCommand(radioButton.getText());
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.ownedCardsOnly = buttonGroup.getSelection().getActionCommand();
        this.searchText = cardName.getText();
        this.manaNumber = (manaList.getSelectedIndex() == 0) ? -1 : Integer.parseInt((String) manaList.getSelectedItem());

        client.getPlayerMapper().getLogger().writeLog(LogType.BUTTON_CLICK, "Apply Filter -> " + "Mana : " +
                manaNumber + ", Search text : " + searchText + ", Only Owned Card : " + ownedCardsOnly);
        client.getMainFrame().updatePanels();
    }


    public String getSearchText() {
        return searchText;
    }

    public String getOwnedCardsOnly() {
        return ownedCardsOnly;
    }

    public int getManaNumber() {
        return manaNumber;
    }
}

