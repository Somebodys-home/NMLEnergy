package io.github.NoOne.nMLEnergy;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLEnergy extends JavaPlugin {
    private static NMLEnergy instance;
    private ProfileManager profileManager;

    @Override
    public void onEnable() {
        instance = this;
        profileManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getProfileManager();

        EnergyManager.startEnergyRegenTask();
        getServer().getPluginManager().registerEvents(new EnergyListener(this), this);
    }

    public static NMLEnergy getInstance() {
        return instance;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }
}
