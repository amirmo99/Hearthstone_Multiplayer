package Util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class DeckReader {

    private final ObjectMapper objectMapper;

    public DeckReader() {
        objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping();
    }

    public DeckFile readDeck(File file) throws IOException {
        DeckFile deckFile;

        deckFile = objectMapper.readValue(file, DeckFile.class);

        return deckFile;
    }

}
