package io.github.NoOne.nMLEnergy;

import io.github.NoOne.nMLPlayerStats.StatConfigManager;
import io.github.NoOne.nMLPlayerStats.statSystem.PlayerStat;
import io.github.NoOne.nMLPlayerStats.statSystem.StatChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class EnergyManager {
    private static NMLEnergy nmlEnergy = NMLEnergy.getInstance();
    private static StatConfigManager statConfigManager = nmlEnergy.getStatConfigManager();
    private static ArrayList<UUID> ongoingEnergyPauses = new ArrayList<>(); // players that have their energy regen paused

    public static void startEnergyRegenTask() {
        new BukkitRunnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {

                    // energy cannot be added if either their energy regen is paused, or they're sprinting in survival or adv. mode
                    if ((player.isSprinting() && player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) || ongoingEnergyPauses.contains(player.getUniqueId())) {
                        continue;
                    }

                    UUID uuid = player.getUniqueId();
                    double currentEnergy = statConfigManager.getStats(uuid).getStatValue(PlayerStat.CURRENT_ENERGY);
                    double maxEnergy = statConfigManager.getStats(uuid).getStatValue(PlayerStat.MAX_ENERGY);

                    if (currentEnergy < maxEnergy) {
                        addEnergy(player, (maxEnergy / 15)); // how long in seconds it takes to regen to full energy
                    }
                }
            }
        }.runTaskTimer(nmlEnergy, 0, 20);
    }

    public static void addEnergy(Player player, double amount) {
        Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, PlayerStat.CURRENT_ENERGY, amount));
    }

    public static void useEnergy(Player player, double amount) {
        if (canHaveEnergyRemoved(player)) {
            Bukkit.getPluginManager().callEvent(new StatChangeEvent(player, PlayerStat.CURRENT_ENERGY, -amount));
        }
    }

    public static void pauseEnergyRegen(Player player) {
        ongoingEnergyPauses.add(player.getUniqueId());
    }

    public static void resumeEnergyRegen(Player player) {
        ongoingEnergyPauses.remove(player.getUniqueId());
    }

    public static void updateEnergyBar(Player player) {
        double currentEnergy = statConfigManager.getStats(player.getUniqueId()).getStatValue(PlayerStat.CURRENT_ENERGY);
        double maxEnergy = statConfigManager.getStats(player.getUniqueId()).getStatValue(PlayerStat.MAX_ENERGY);
        double percent = currentEnergy / maxEnergy;
        int foodLevel = (int) Math.ceil(percent * 20);

        foodLevel = Math.clamp(foodLevel, 0, 20);
        player.setFoodLevel(foodLevel);
    }

    public static boolean canHaveEnergyRemoved(Player player) {
        return player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE;
    }
}