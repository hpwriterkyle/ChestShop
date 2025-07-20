package top.vrilhyc.plugins.chestshop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {
    // Config Paths
    public static final String CONFIG_DIR = "config/chestshop";
    public static final String CONFIG_PATH = CONFIG_DIR;
    public static final String CONFIG_FILE = "config.json";
    public static final String LANG_FILE = "lang.json";
    public static final String TIERS_FILE = "tiers.json";
    public static final String PLAYERS_PATH = CONFIG_DIR + "/players";
    public static final String PLAYER_DATA_DIR = PLAYERS_PATH;

    // Default Values
    public static final float DEFAULT_SCALE = 1F;
    public static final String DEFAULT_STORAGE_CONTENT = "FILE"; //SQL@[url]

    // Battle Pass Messages
    public static final String MESSAGE_STARTED = "The cobblememory has started";
    public static final String MESSAGE_FAILED = "The cobblememory has failed";
    public static final String MESSAGE_SUCCESS = "The cobblememory has succeeded";
    public static final String MESSAGE_COOLDOWN = "You are still in the %s cooldown";
}
