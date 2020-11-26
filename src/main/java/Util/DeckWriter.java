package Util;

import com.fasterxml.jackson.databind.ObjectMapper;
import logic.Player;

import java.io.File;
import java.io.IOException;

public class DeckWriter {
    private final ObjectMapper objectMapper;

    public DeckWriter() {
        this.objectMapper = new ObjectMapper();
    }

    public void writeDecks(File file, Player player1, Player player2) {
        DeckFile deckFile = new DeckFile(player1, player2);

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, deckFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
