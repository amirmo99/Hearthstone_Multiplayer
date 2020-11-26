package Util;

import java.io.Serializable;

public class BundleRankTableModels implements Serializable {

    private final RankTableModel topTen;
    private final RankTableModel aroundPlayer;

    public BundleRankTableModels(RankTableModel topTen, RankTableModel aroundPlayer) {
        this.topTen = topTen;
        this.aroundPlayer = aroundPlayer;
    }

    public RankTableModel getTopTen() {
        return topTen;
    }

    public RankTableModel getAroundPlayer() {
        return aroundPlayer;
    }
}
