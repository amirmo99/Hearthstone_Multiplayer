package abstracts;

//import logic.GameState;
import logic.MyGameState;

public interface ModelAction {
    void execute(MyGameState gameState);
}
