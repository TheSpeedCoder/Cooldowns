package net.draycia.cooldowns;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.UUID;

public final class Cooldowns extends JavaPlugin {

    private CooldownManager cooldownManager = new CooldownManager();

    private File dataFile = new File(getDataFolder(), "data.json");

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Type type = new TypeToken<HashMap<UUID, HashMap<String, Long>>>() {}.getType();

    @Override
    public void onEnable() {
        loadCooldowns();
        getServer().getServicesManager().register(CooldownManager.class, cooldownManager, this, ServicePriority.High);
    }

    @Override
    public void onDisable() {
        saveCooldowns();
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    private void loadCooldowns() {
        if (!dataFile.getParentFile().exists()) {
            dataFile.getParentFile().mkdirs();
        }

        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            HashMap<UUID, HashMap<String, Long>> json = gson.fromJson(new FileReader(dataFile), type);

            if (json != null) {
                cooldownManager.setCooldowns(json);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // TODO: Remove expired cooldowns on load
    }

    private void saveCooldowns() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            gson.toJson(cooldownManager.getCooldowns(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}