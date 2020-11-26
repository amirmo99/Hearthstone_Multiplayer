package enums;

public enum Rarity {
    Free(0),
    Common(1),
    Rare(2),
    Epic(3),
    Legendary(4);

    private final int number;

    Rarity(int i) {
        number = i;
    }

    public int getNumber() {
        return number;
    }
}
