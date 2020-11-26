package configs;

import java.io.Serializable;

public class MyConfigs implements Serializable {
    public Configs properties;
    private final String name;

    protected MyConfigs(String name) {
        this.name = name;
        setProperties();
    }

    public String getName() {
        return name;
    }

    private void setProperties() {
        this.properties = ConfigsLoader.getInstance().getConfigs(name);
    }
}
