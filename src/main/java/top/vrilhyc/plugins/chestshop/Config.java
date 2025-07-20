package top.vrilhyc.plugins.chestshop;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import top.vrilhyc.plugins.chestshop.utils.Utils;

import java.util.List;

public class Config {
    private float scale;

    public Config() {
        setDefaults();
    }

    private void setDefaults() {
        generateDefaultConfig();
    }

    private void generateDefaultConfig() {
        this.scale = Constants.DEFAULT_SCALE;
    }

    public void load() {
        // Ensure config directory exists
        Utils.checkForDirectory("/" + Constants.CONFIG_PATH);
        
        String content = Utils.readFileSync(Constants.CONFIG_PATH, Constants.CONFIG_FILE);
        if (content == null || content.isEmpty()) {
            generateDefaultConfig();
            save();
            return;
        }

        try {
            JsonObject json = JsonParser.parseString(content).getAsJsonObject();
            loadFromJson(json);
        } catch (Exception e) {
            System.out.println("Failed to load config");
            e.printStackTrace();
            setDefaults();
            save();
        }
    }

    private void loadFromJson(JsonObject json) {
        scale = getOrDefault(json, "blacklisted-species", Constants.DEFAULT_SCALE);
    }

    private <T> T getOrDefault(JsonObject json, String key, T defaultValue) {
        if (!json.has(key)) return defaultValue;
        
        JsonElement element = json.get(key);
        if (defaultValue instanceof String) {
            return (T) element.getAsString();
        } else if (defaultValue instanceof Integer) {
            return (T) Integer.valueOf(element.getAsInt());
        }else if(defaultValue instanceof Float){
            return (T)Float.valueOf(element.getAsString());
        }
        else if (defaultValue instanceof Long) {
            return (T) Long.valueOf(element.getAsLong());
        } else if (defaultValue instanceof Boolean) {
            return (T) Boolean.valueOf(element.getAsBoolean());
        }else if(defaultValue instanceof List<?>){
            return (T)new Gson().fromJson(element,List.class);
        }
        return defaultValue;
    }

    public void save() {
        // Ensure config directory exists
        Utils.checkForDirectory("/" + Constants.CONFIG_PATH);

        JsonObject json = new JsonObject();
        json.addProperty("scale",scale);

        Utils.writeFileSync(Constants.CONFIG_PATH, Constants.CONFIG_FILE,
                new Gson().toJson(json));
    }

    public float getScale() {
        return scale;
    }
}
