package configs;

import java.awt.*;

public class PanelsConfigs extends MyConfigs {

    private String font;
    private Color labelThemeColor;

    private int bigTitleSize,
            smallTitleSize,
            homeButtonsWidth,
            homeButtonsHeight,
            homeFontSize,
            loginButtonsWidth,
            loginButtonsHeight,
            loginFieldFontSize,
            loginLabelFontSize,
            loginButtonFontSize,
            tableRowHeight,
            tableFontSize;

    public PanelsConfigs() {
        super(Configs.PANELS_CONFIG);
        init();
    }

    private void init() {
        font = properties.getProperty("FONT");
        labelThemeColor = properties.readColor("LABEL_FOREGROUND_COLOR");

        bigTitleSize = properties.readInteger("BIG_TITLE_SIZE");
        smallTitleSize = properties.readInteger("SMALL_TITLE_SIZE");
        homeButtonsWidth = properties.readInteger("HOME_BUTTON_WIDTH");
        homeButtonsHeight = properties.readInteger("HOME_BUTTON_HEIGHT");
        homeFontSize = properties.readInteger("HOME_FONT_SIZE");
        loginButtonsWidth = properties.readInteger("LOGIN_BUTTON_WIDTH");
        loginButtonsHeight = properties.readInteger("LOGIN_BUTTON_HEIGHT");
        loginFieldFontSize = properties.readInteger("LOGIN_FIELD_FONT_SIZE");
        loginLabelFontSize = properties.readInteger("LOGIN_LABEL_FONT_SIZE");
        loginButtonFontSize = properties.readInteger("LOGIN_BUTTON_FONT_SIZE");
        tableRowHeight = properties.readInteger("TABLE_ROW_HEIGHT");
        tableFontSize = properties.readInteger("TABLE_FONT_SIZE");

    }

    public String getFont() {
        return font;
    }

    public Color getLabelThemeColor() {
        return labelThemeColor;
    }

    public int getBigTitleSize() {
        return bigTitleSize;
    }

    public int getSmallTitleSize() {
        return smallTitleSize;
    }

    public int getHomeButtonsWidth() {
        return homeButtonsWidth;
    }

    public int getHomeButtonsHeight() {
        return homeButtonsHeight;
    }

    public int getHomeFontSize() {
        return homeFontSize;
    }

    public int getLoginButtonsWidth() {
        return loginButtonsWidth;
    }

    public int getLoginButtonsHeight() {
        return loginButtonsHeight;
    }

    public int getLoginFieldFontSize() {
        return loginFieldFontSize;
    }

    public int getLoginLabelFontSize() {
        return loginLabelFontSize;
    }

    public int getLoginButtonFontSize() {
        return loginButtonFontSize;
    }

    public int getTableRowHeight() {
        return tableRowHeight;
    }

    public int getTableFontSize() {
        return tableFontSize;
    }
}
