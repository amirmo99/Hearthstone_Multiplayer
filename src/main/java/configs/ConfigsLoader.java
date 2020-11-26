package configs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigsLoader {

    private final static String defaultAddress = "src/main/resources/Configs/logicConfigFiles/ConfigsLocation.properties";

    private HashMap<String, Configs> constants;
    private HashMap<String, Configs> panelsConfigs;
    private HashMap<String, Configs> modelsConfigs;
    private HashMap<String, Configs> gameConfigs;
    private HashMap<String, Configs> pathConfigs;
    private Configs properties;


    private static ConfigsLoader loader;

    private ConfigsLoader() {
        init();
        loadConfigs();
    }

    public static ConfigsLoader getInstance() {
        if (loader == null)
            loader = new ConfigsLoader();
        return loader;
    }

    private void init() {
        constants = new HashMap<>();
        panelsConfigs = new HashMap<>();
        modelsConfigs = new HashMap<>();
        gameConfigs = new HashMap<>();
        pathConfigs = new HashMap<>();
        properties = new Configs();
    }

    private void loadConfigs() {
        Set<Map.Entry<Object, Object>> entries = getLocations().get("Location").entrySet();

        for (Map.Entry<Object, Object> entry : entries) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            Configs property = new Configs();
            try {
                File test = new File(value);
                FileReader reader = new FileReader(test);
                property.load(reader);
            } catch (IOException e) {
                System.out.println("Could not read file : " + value);
                e.printStackTrace();
            }

            if (key.equalsIgnoreCase(Configs.CONSTANTS_CONFIG)) {
                constants.put(key, property);
            } else if (key.equalsIgnoreCase(Configs.PANELS_CONFIG)) {
                panelsConfigs.put(key, property);
            } else if (key.equalsIgnoreCase(Configs.MODELS_CONFIG)) {
                modelsConfigs.put(key, property);
            } else if (key.equalsIgnoreCase(Configs.GAME_Graphic_CONFIG)) {
                gameConfigs.put(key, property);
            } else if (key.equalsIgnoreCase(Configs.PATHS_CONFIG)) {
                pathConfigs.put(key, property);
            }
            else
                properties.put(key, property);

        }

    }

    private HashMap<String, Configs> getLocations() {
        HashMap<String, Configs> locations = new HashMap<>();
        Configs configs = new Configs();
        loadAddressee(configs);
        locations.put("Location", configs);
        return locations;
    }

    private void loadAddressee(Configs configs) {
        try {
            FileReader fileReader = new FileReader(defaultAddress);
            configs.load(fileReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Configs getConstants(String name) {
        return constants.get(name);
    }

    private Configs getPanelsConfigs(String name) {
        return panelsConfigs.get(name);
    }

    private Configs getModelsConfigs(String name) {
        return modelsConfigs.get(name);
    }

    private Configs getGameConfigs(String name) {
        return gameConfigs.get(name);
    }

    private Configs getPathConfigs(String name){ return pathConfigs.get(name); }

    public Configs getConfigs(String name) {
        switch (name) {
            case Configs.CONSTANTS_CONFIG:
                return getConstants(name);
            case Configs.GAME_Graphic_CONFIG:
                return getGameConfigs(name);
            case Configs.MODELS_CONFIG:
                return getModelsConfigs(name);
            case Configs.PANELS_CONFIG:
                return getPanelsConfigs(name);
            case Configs.PATHS_CONFIG:
                return getPathConfigs(name);
            default:
                return null;
        }
    }
}
