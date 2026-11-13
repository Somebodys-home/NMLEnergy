package io.github.NoOne.nMLEnergy;

import io.github.NoOne.nMLPlayerStats.profileSystem.ProfileManager;
import io.github.NoOne.nMLPlayerStats.statSystem.PlayerStat;
import io.github.NoOne.nMLPlayerStats.statSystem.ResetStatsEvent;
import io.github.NoOne.nMLPlayerStats.statSystem.StatChangeEvent;
import io.github.NoOne.nMLPlayerStats.statSystem.Stats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class EnergyListener implements Listener {
    private ProfileManager profileManager;

    public EnergyListener(NMLEnergy nmlEnergy) {
        this.profileManager = nmlEnergy.getProfileManager();
    }

    @EventHandler (priority = EventPriority.MONITOR) // since this is the thing that updates the stat internally, it should be on monitor
    public void onEnergyStatChange(StatChangeEvent event) {
        Player player = event.getPlayer();
        PlayerStat playerStat = event.getPlayerStat();

        if (playerStat == PlayerStat.CURRENT_ENERGY) { // update the hunger bar when updating current energy
            EnergyManager.updateEnergyBar(player);
        } else if (playerStat == PlayerStat.MAX_ENERGY) { // check for overflow energy when updating max energy
            Stats stats = profileManager.getStats(player.getUniqueId());
            double currentEnergy = stats.getStatValue(PlayerStat.CURRENT_ENERGY);
            double maxEnergy = stats.getStatValue(PlayerStat.MAX_ENERGY);

            if (currentEnergy > maxEnergy) {
                Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, PlayerStat.CURRENT_ENERGY, maxEnergy - currentEnergy));
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.isSprinting()) {
            event.setCancelled(true);
            return;
        }

        Stats stats = profileManager.getStats(player.getUniqueId());
        int foodLevelChange = event.getFoodLevel() - player.getFoodLevel();
        double energyChange = foodLevelChange * (stats.getStatValue(PlayerStat.MAX_ENERGY) / 20);

        Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, PlayerStat.CURRENT_ENERGY, energyChange));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Stats stats = profileManager.getStats(player.getUniqueId());

        if (player.isSprinting()) {
            EnergyManager.useEnergy(player, stats.getStatValue(PlayerStat.MAX_ENERGY) / 600);
        }
    }

    @EventHandler
    public void resetEnergy(ResetStatsEvent event) {
        event.getPlayer().setFoodLevel(20);
    }
}
