package com.hpfxd.pandaspigot.knockback;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class KnockbackConfig {

    private File configFile;
    private YamlConfiguration config;
    private KnockbackProfile currentKb;

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public void setConfig(YamlConfiguration config) {
        this.config = config;
    }

    public void setKbProfiles(Set<KnockbackProfile> kbProfiles) {
        this.kbProfiles = kbProfiles;
    }

    public File getConfigFile() {
        return configFile;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public KnockbackProfile getCurrentKb() {
        return currentKb;
    }

    public void setCurrentKb(KnockbackProfile currentKb) {
        this.currentKb = currentKb;
    }

    private Set<KnockbackProfile> kbProfiles = new HashSet<>();

    public Set<KnockbackProfile> getKbProfiles() {
        return kbProfiles;
    }

    public KnockbackConfig() {
        configFile = new File("knockback.yml");
        config = new YamlConfiguration();

        try {
            config.load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load knockback.yml, please correct your syntax errors", e);
        }

        config.options().copyDefaults(true);
        loadConfig();
    }

    private void loadConfig() {
        CraftKnockbackProfile craftKnockbackProfile = new CraftKnockbackProfile("default");
        kbProfiles = new HashSet<>();
        kbProfiles.add(craftKnockbackProfile);

        for (String key : getKeys("profiles")) {
            String path = "profiles." + key;
            CraftKnockbackProfile profile = (CraftKnockbackProfile) getKbProfileByName(key);

            if (profile == null) {
                profile = new CraftKnockbackProfile(key);
                kbProfiles.add(profile);
            }

            profile.setHorizontal(getDouble(path + ".horizontal", 0.5273D));
            profile.setVertical(getDouble(path + ".vertical", 0.8835D));
            profile.setExtraHorizontal(getDouble(path + ".extra-horizontal", 0.5D));
            profile.setExtraVertical(getDouble(path + ".extra-vertical", 0.0D));
            profile.setStartRange(getDouble(path + ".start-range", 1.4D));
            profile.setRangeFactor(getDouble(path + ".range-factor", 0.1D));
            profile.setMaxRangeReduction(getDouble(path + ".max-range-reduction", 0.4D));
            profile.setTradeIncrement(getDouble(path + ".tradeincrement", 0.1051D));
            profile.setVerticalLimit(getDouble(path + ".vertical-limit",  0.361375D));
        }

        currentKb = getKbProfileByName(getString("current", "default"));

        if (currentKb == null)
            currentKb = craftKnockbackProfile;

        try {
            config.save(configFile);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + configFile, e);
        }
    }

    public KnockbackProfile getKbProfileByName(String name) {
        for (KnockbackProfile profile : kbProfiles) {
            if (profile.getName().equalsIgnoreCase(name))
                return profile;
        }
        return null;
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void set(String path, Object val) {
        config.set(path, val);
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<String> getKeys(String path) {
        if (!config.isConfigurationSection(path)) {
            config.createSection(path);
            return new HashSet<>();
        }
        return config.getConfigurationSection(path).getKeys(false);
    }

    public boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, config.getBoolean(path));
    }

    public double getDouble(String path, double def) {
        config.addDefault(path, def);
        return config.getDouble(path, config.getDouble(path));
    }

    public float getFloat(String path, float def) {
        return (float) getDouble(path, def);
    }

    public int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInt(path, config.getInt(path));
    }

    public <T> List<?> getList(String path, T def) {
        config.addDefault(path, def);
        return config.getList(path, config.getList(path));
    }

    public String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }
}
