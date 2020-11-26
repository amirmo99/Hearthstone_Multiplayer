package configs;

public class PathConfigs extends MyConfigs {

    private String resources, homeBGImage, cardImages, heroImages,
            heroPowerImages, gameBGImage, passiveImages,
            cardLock, heroShield, logs,
            cardsData, passivesData, playersData, heroesData,
            deckReaderFile;

    public PathConfigs() {
        super(Configs.PATHS_CONFIG);
        init();
    }

    private void init() {
        resources = properties.getProperty("Resources");
        homeBGImage = properties.getProperty("HomeBGImage");
        cardImages = properties.getProperty("CardImage");
        heroImages = properties.getProperty("HeroImage");
        heroPowerImages = properties.getProperty("HeroPowerImage");
        gameBGImage = properties.getProperty("GameBGPath");
        passiveImages = properties.getProperty("PassiveImages");
        cardLock = properties.getProperty("CardLock");
        heroShield = properties.getProperty("HeroShield");
        logs = properties.getProperty("Logs");
        cardsData = properties.getProperty("CardsData");
        passivesData = properties.getProperty("PassivesData");
        playersData = properties.getProperty("PlayersData");
        heroesData = properties.getProperty("HeroesData");
        deckReaderFile = properties.getProperty("DeckReaderFile");
    }

    public String getResources() {
        return resources;
    }

    public String getHomeBGImage() {
        return homeBGImage;
    }

    public String getCardImages() {
        return cardImages;
    }

    public String getHeroImages() {
        return heroImages;
    }

    public String getHeroPowerImages() {
        return heroPowerImages;
    }

    public String getGameBGImage() {
        return gameBGImage;
    }

    public String getPassiveImages() {
        return passiveImages;
    }

    public String getCardLock() {
        return cardLock;
    }

    public String getHeroShield() {
        return heroShield;
    }

    public String getLogs() {
        return logs;
    }

    public String getCardsData() {
        return cardsData;
    }

    public String getPassivesData() {
        return passivesData;
    }

    public String getPlayersData() {
        return playersData;
    }

    public String getHeroesData() {
        return heroesData;
    }

    public String getDeckReaderFile() {
        return deckReaderFile;
    }
}
