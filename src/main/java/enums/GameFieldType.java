package enums;

public enum GameFieldType {
    P1Hand,
    P1Board,
    P1Hero,
    P1HeroPower,

    P2Hand,
    P2Board,
    P2Hero,
    P2HeroPower,

    other;

    public int playerIndex() {
        switch (this) {
            case P1Hand:
            case P1Hero:
            case P1Board:
            case P1HeroPower:
                return 1;
            case P2HeroPower:
            case P2Board:
            case P2Hero:
            case P2Hand:
                return 2;
            default:
                return 0;
        }
    }
}
