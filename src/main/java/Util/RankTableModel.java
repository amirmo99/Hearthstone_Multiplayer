package Util;

import logic.Player;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RankTableModel implements TableModel, Serializable {

    private ArrayList<String> userNames;
    private ArrayList<Integer> trophies;
    private ArrayList<Integer> ranks;

    public RankTableModel(int firstRank, int lastRank, List<Player> players) {
        init();
        fillLists(firstRank, lastRank, players);
    }

    private void init() {
        userNames = new ArrayList<>();
        trophies = new ArrayList<>();
        ranks = new ArrayList<>();
    }

    private void fillLists(int firstRank, int lastRank, List<Player> players) {
        for (int i = firstRank; i <= lastRank; i++) {
            Player player = players.get(i);

            ranks.add(1 + i);
            userNames.add(player.getUsername());
            trophies.add(player.getTrophies());
        }
    }

    @Override
    public int getRowCount() {
        return ranks.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "Rank";
            case 1:
                return "Player Username";
            case 2:
                return "Trophies";
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0: case 2:
                return Integer.class;
            case 1:
                return String.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return ranks.get(rowIndex);
            case 1:
                return userNames.get(rowIndex);
            case 2:
                return trophies.get(rowIndex);
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }
}
