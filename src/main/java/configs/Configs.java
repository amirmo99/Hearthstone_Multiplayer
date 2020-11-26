package configs;

import java.awt.*;
import java.util.Properties;

public class Configs extends Properties {

    public static final String MODELS_CONFIG = "MODELS";
    public static final String GAME_Graphic_CONFIG = "GAME_BOARD";
    public static final String PANELS_CONFIG = "PANELS";
    public static final String CONSTANTS_CONFIG = "CONSTANTS";
    public static final String PATHS_CONFIG = "PATHS";

    public int readInteger(String name) {
        return Integer.parseInt(this.getProperty(name));
    }

    public double readDouble(String name) {
        return Double.parseDouble(this.getProperty(name));
    }

    public Color readColor(String name) {
        String hex = getProperty(name).substring(2);
        return new Color(Integer.parseInt(hex, 16));
    }
}
