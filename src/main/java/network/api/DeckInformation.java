package network.api;

import models.Deck;

import java.io.Serializable;

public class DeckInformation implements Serializable {

    private String name, heroName;
    private int index;

    public DeckInformation(String name, String heroName, int index) {
        this.name = name;
        this.heroName = heroName;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public String getHeroName() {
        return heroName;
    }

    public int getIndex() {
        return index;
    }
}
