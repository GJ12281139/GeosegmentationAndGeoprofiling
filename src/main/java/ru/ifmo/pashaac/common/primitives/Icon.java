package ru.ifmo.pashaac.common.primitives;

import ru.ifmo.pashaac.common.Properties;

/**
 * Paths to local icons
 * <p>
 * Created by Pavel Asadchiy
 * 11.05.16 23:27.
 */
public enum Icon {

    VISTA_BALL_AZURE_32(Properties.getIconPathPrefix() + "vista.ball.azure.32.png"),
    VISTA_BALL_AZURE_48(Properties.getIconPathPrefix() + "vista.ball.azure.48.png"),

    VISTA_BALL_BLUE_32(Properties.getIconPathPrefix() + "vista.ball.blue.32.png"),
    VISTA_BALL_BLUE_48(Properties.getIconPathPrefix() + "vista.ball.blue.48.png"),

    VISTA_BALL_BLUE_GREEN_32(Properties.getIconPathPrefix() + "vista.ball.blue.green.32.png"),
    VISTA_BALL_BLUE_GREEN_48(Properties.getIconPathPrefix() + "vista.ball.blue.green.48.png"),

    VISTA_BALL_BRONZE_32(Properties.getIconPathPrefix() + "vista.ball.bronze.32.png"),
    VISTA_BALL_BRONZE_48(Properties.getIconPathPrefix() + "vista.ball.bronze.48.png"),

    VISTA_BALL_DARK_BLUE_32(Properties.getIconPathPrefix() + "vista.ball.dark.blue.32.png"),
    VISTA_BALL_DARK_BLUE_48(Properties.getIconPathPrefix() + "vista.ball.dark.blue.48.png"),

    VISTA_BALL_GREEN_32(Properties.getIconPathPrefix() + "vista.ball.green.32.png"),
    VISTA_BALL_GREEN_48(Properties.getIconPathPrefix() + "vista.ball.green.48.png"),

    VISTA_BALL_HUE_32(Properties.getIconPathPrefix() + "vista.ball.hue.32.png"),
    VISTA_BALL_HUE_48(Properties.getIconPathPrefix() + "vista.ball.hue.48.png"),

    VISTA_BALL_IRON_32(Properties.getIconPathPrefix() + "vista.ball.iron.32.png"),
    VISTA_BALL_IRON_48(Properties.getIconPathPrefix() + "vista.ball.iron.48.png"),

    VISTA_BALL_LIGHT_RED_32(Properties.getIconPathPrefix() + "vista.ball.light.red.32.png"),
    VISTA_BALL_LIGHT_RED_48(Properties.getIconPathPrefix() + "vista.ball.light.red.48.png"),

    VISTA_BALL_ORANGE_32(Properties.getIconPathPrefix() + "vista.ball.orange.32.png"),
    VISTA_BALL_ORANGE_48(Properties.getIconPathPrefix() + "vista.ball.orange.48.png"),

    VISTA_BALL_PINK_32(Properties.getIconPathPrefix() + "vista.ball.pink.32.png"),
    VISTA_BALL_PINK_48(Properties.getIconPathPrefix() + "vista.ball.pink.48.png"),

    VISTA_BALL_POISON_GREEN_32(Properties.getIconPathPrefix() + "vista.ball.poison.green.32.png"),
    VISTA_BALL_POISON_GREEN_48(Properties.getIconPathPrefix() + "vista.ball.poison.green.48.png"),

    VISTA_BALL_PURPLE_32(Properties.getIconPathPrefix() + "vista.ball.purple.32.png"),
    VISTA_BALL_PURPLE_48(Properties.getIconPathPrefix() + "vista.ball.purple.48.png"),

    VISTA_BALL_SILVER_32(Properties.getIconPathPrefix() + "vista.ball.silver.32.png"),
    VISTA_BALL_SILVER_48(Properties.getIconPathPrefix() + "vista.ball.silver.48.png");


    String path;

    Icon(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
