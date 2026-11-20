package io.github.NoOne.nMLEnergy;

import io.github.NoOne.nMLPlayerStats.NMLPlayerStats;
import io.github.NoOne.nMLPlayerStats.StatConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NMLEnergy extends JavaPlugin {
    private static NMLEnergy instance;
    private StatConfigManager statConfigManager;

    @Override
    public void onEnable() {
        instance = this;
        statConfigManager = JavaPlugin.getPlugin(NMLPlayerStats.class).getStatConfigManager();

        EnergyManager.startEnergyRegenTask();
        getServer().getPluginManager().registerEvents(new EnergyListener(this), this);
    }

    public static NMLEnergy getInstance() {
        return instance;
    }

    public StatConfigManager getStatConfigManager() {
        return statConfigManager;
    }
}
