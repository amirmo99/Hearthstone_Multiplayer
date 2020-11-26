package UI.GUI.MainPanels;

import UI.GUI.MainFrame;
import UI.GUI.MyPanel;
import UI.GUI.SubPanel.BackAndExitPanel;
import UI.PlayerMapper;
import models.Deck;
import network.client.Client;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.List;

public class StatusPanel extends MyPanel {

    private BackAndExitPanel control;
    private JScrollPane scrollPane;
    private JTable decks;

    public StatusPanel(Client client) {
        super(client);
        drawBackground(pathConfigs.getHomeBGImage());
        createTitle("Status");
        init();
        configureElements();
        placeComponents();
    }

    private void init() {
        setOpaque(false);
        control = new BackAndExitPanel(client);
        decks = new JTable();
        scrollPane = new JScrollPane();
    }

    private void configureElements() {
        configureTable();
        makeScrollPane();
    }

    private void configureTable() {
        Font font = new Font(Font.SANS_SERIF, Font.PLAIN, panelsConfigs.getTableFontSize());
        decks.setFont(font);
        decks.setRowHeight(panelsConfigs.getTableRowHeight());
        decks.setOpaque(false);
        decks.setModel(new MyModel());
    }

    private void makeScrollPane() {

        scrollPane.setViewportView(decks);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
    }

    private void placeComponents() {
        setLayout(new BorderLayout());

        add(control, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private class MyModel implements TableModel {

        List<Deck> deckList = client.getPlayerMapper().getSortedDecks();

        @Override
        public int getRowCount() {
            return deckList.size();
        }

        @Override
        public int getColumnCount() {
            return 9;
        }

        @Override
        public String getColumnName(int i) {
            switch (i) {
                case 0:
                    return "Deck Name";
                case 1:
                    return "Trophy";
                case 2:
                    return "Victory Rate";
                case 3:
                    return "Wins";
                case 4:
                    return "Played";
                case 5:
                    return "Average Mana";
                case 6:
                    return "Hero";
                case 7:
                    return "Most Frequent Card";
                case 8:
                    return "Status";
            }
            return null;
        }

        @Override
        public Class<?> getColumnClass(int i) {
            switch (i) {
                case 1:
                case 2:
                case 3:
                case 4:
                    return int.class;
                case 5:
                    return double.class;
                default:
                    return String.class;
            }
        }

        @Override
        public boolean isCellEditable(int i, int i1) {
            return false;
        }

        @Override
        public Object getValueAt(int i, int i1) {
            Deck deck = deckList.get(i);
            switch (i1) {
                case 0:
                    return deck.getName();
                case 1:
                    return deck.getGainedTrophy();
                case 2:
                    return deck.victoryRate();
                case 3:
                    return deck.getWonGames();
                case 4:
                    return deck.getPlayedGames();
                case 5:
                    return deck.averageMana();
                case 6:
                    return deck.getHeroName();
                case 7:
                    return (deck.bestCard() == null) ? "" : deck.bestCard().getName();
                case 8:
                    return client.getPlayerMapper().isDeckActive(deck);
                default:
                    return null;
            }
        }

        @Override
        public void setValueAt(Object o, int i, int i1) {

        }

        @Override
        public void addTableModelListener(TableModelListener tableModelListener) {

        }

        @Override
        public void removeTableModelListener(TableModelListener tableModelListener) {

        }
    }


}
