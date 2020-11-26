package configs;

import java.awt.*;
import java.lang.reflect.Field;

public class GameGraphicConstants extends MyConfigs {

    private double gameWidth, gameHeight,
            cardWidth, cardHeight,
            cardX1, cardX2, cardY1, cardY2, heroHpX, heroHpY, powerManaX, powerManaY,
            hero1X, hero1Y, hero2X, hero2Y, heroWidth, heroHeight,
            power1X, power1Y, power2X, power2Y, powerWidth, powerHeight,
            board1X, board1Y, board2X, board2Y, boardWidth, boardHeight,
            deck1X, deck1Y, deck2X, deck2Y, deckWidth, deckHeight,
            hand1X, hand1Y, hand2X, hand2Y, handWidth, handHeight,
            mana1X, mana1Y, mana2X, mana2Y, manaWidth, manaHeight,
            mission1X, mission1Y, mission2X, mission2Y, missionDiameter,
            endX, endY, endHeight, endWidth,
            chatX, chatY, chatHeight, chatWidth,
            eventX, eventY, eventWidth, eventHeight,
            frameWidth, frameHeight,
            timerX, timerY;

    private int screenWidth, screenHeight;


    public GameGraphicConstants() {
        super(Configs.GAME_Graphic_CONFIG);
        init();
    }

    private void init() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.getType() == double.class) {
                try {
                    field.setDouble(this, properties.readDouble(field.getName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    System.out.println("Could not find field: " + field.getName() + " in GameGraphicConstants ");
                }
            }
        }
        screenWidth = (int) (Toolkit.getDefaultToolkit().getScreenSize().width * gameWidth);
        screenHeight = (int) (Toolkit.getDefaultToolkit().getScreenSize().height * gameHeight);

        gameHeight = screenHeight;
        gameWidth = screenWidth;
    }

    public double getGameWidth() {
        return gameWidth;
    }

    public double getGameHeight() {
        return gameHeight;
    }

    public double getCardWidth() {
        return cardWidth * screenWidth;
    }

    public double getCardHeight() {
        return cardHeight * screenHeight;
    }

    public double getHero1X() {
        return hero1X * screenWidth;
    }

    public double getHero1Y() {
        return hero1Y * screenHeight;
    }

    public double getHero2X() {
        return hero2X * screenWidth;
    }

    public double getHero2Y() {
        return hero2Y * screenHeight;
    }

    public double getHeroWidth() {
        return heroWidth * screenWidth;
    }

    public double getHeroHeight() {
        return heroHeight * screenHeight;
    }

    public double getPower1X() {
        return power1X * screenWidth;
    }

    public double getPower1Y() {
        return power1Y * screenHeight;
    }

    public double getPower2X() {
        return power2X * screenWidth;
    }

    public double getPower2Y() {
        return power2Y * screenHeight;
    }

    public double getPowerWidth() {
        return powerWidth * screenWidth;
    }

    public double getPowerHeight() {
        return powerHeight * screenHeight;
    }

    public double getBoard1X() {
        return board1X * screenWidth;
    }

    public double getBoard1Y() {
        return board1Y * screenHeight;
    }

    public double getBoard2X() {
        return board2X * screenWidth;
    }

    public double getBoard2Y() {
        return board2Y * screenHeight;
    }

    public double getBoardWidth() {
        return boardWidth * screenWidth;
    }

    public double getBoardHeight() {
        return boardHeight * screenHeight;
    }

    public double getDeck1X() {
        return deck1X * screenWidth;
    }

    public double getDeck1Y() {
        return deck1Y * screenHeight;
    }

    public double getDeck2X() {
        return deck2X * screenWidth;
    }

    public double getDeck2Y() {
        return deck2Y * screenHeight;
    }

    public double getDeckWidth() {
        return deckWidth * screenWidth;
    }

    public double getDeckHeight() {
        return deckHeight * screenHeight;
    }

    public double getHand1X() {
        return hand1X * screenWidth;
    }

    public double getHand1Y() {
        return hand1Y * screenHeight;
    }

    public double getHand2X() {
        return hand2X * screenWidth;
    }

    public double getHand2Y() {
        return hand2Y * screenHeight;
    }

    public double getHandWidth() {
        return handWidth * screenWidth;
    }

    public double getHandHeight() {
        return handHeight * screenHeight;
    }

    public double getMana1X() {
        return mana1X * screenWidth;
    }

    public double getMana1Y() {
        return mana1Y * screenHeight;
    }

    public double getManaWidth() {
        return manaWidth * screenWidth;
    }

    public double getManaHeight() {
        return manaHeight * screenHeight;
    }

    public double getEndX() {
        return endX * screenWidth;
    }

    public double getEndY() {
        return endY * screenHeight;
    }

    public double getEndHeight() {
        return endHeight * screenHeight;
    }

    public double getEndWidth() {
        return endWidth * screenWidth;
    }

    public double getEventX() {
        return eventX * screenWidth;
    }

    public double getEventY() {
        return eventY * screenHeight;
    }

    public double getEventWidth() {
        return eventWidth * screenWidth;
    }

    public double getEventHeight() {
        return eventHeight * screenHeight;
    }

    public double getPowerManaX() {
        return powerManaX * getCardWidth();
    }

    public double getPowerManaY() {
        return powerManaY * getCardHeight();
    }

    public double getHeroHpX() {
        return heroHpX * getHeroWidth();
    }

    public double getHeroHpY() {
        return heroHpY * getHeroHeight();
    }

    public double getCardX1() {
        return cardX1 * getCardWidth();
    }

    public double getCardX2() {
        return cardX2 * getCardWidth();
    }

    public double getCardY1() {
        return cardY1 * getCardHeight();
    }

    public double getCardY2() {
        return cardY2 * getCardHeight();
    }

    public double getMana2X() {
        return mana2X * screenWidth;
    }

    public double getMana2Y() {
        return mana2Y * screenHeight;
    }

    public double getFrameWidth() {
        return frameWidth * screenWidth;
    }

    public double getFrameHeight() {
        return frameHeight * screenHeight;
    }

    public double getMission1X() {
        return mission1X * screenWidth;
    }

    public double getMission1Y() {
        return mission1Y * screenHeight;
    }

    public double getMission2X() {
        return mission2X * screenWidth;
    }

    public double getMission2Y() {
        System.out.println(mission2Y * screenHeight);
        return mission2Y * screenHeight;
    }

    public double getMissionDiameter() {
        return missionDiameter * screenWidth;
    }

    public double getTimerX() {
        return timerX * screenWidth;
    }

    public double getTimerY() {
        return timerY * screenHeight;
    }

    public double getChatX() {
        return chatX * screenWidth;
    }

    public double getChatY() {
        return chatY * screenHeight;
    }

    public double getChatHeight() {
        return chatHeight * screenHeight;
    }

    public double getChatWidth() {
        return chatWidth * screenWidth;
    }
}