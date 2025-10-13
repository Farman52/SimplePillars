package me.farmans.simplePillars.utils;

import me.farmans.simplePillars.SimplePillars;
import me.farmans.simplePillars.commands.StartCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Random;

public class RunnableUtil {
    public static void startGame(SimplePillars plugin) {
        Object[] materials = Arrays.stream(Material.values()).filter(p -> p.isItem()).toArray();

        final double INTERVAL = plugin.getConfig().getDouble("Interval");

        BossBar bossbar = plugin.getServer().getBossBar(new NamespacedKey(plugin, "simplepillars"));

        int schedule = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            private int ticks = 0;

            @Override
            public void run() {
                ticks++;
                bossbar.setProgress(INTERVAL / 100 * ticks);
                if (ticks == 100 / INTERVAL) {
                    for (int i = 0; i < StartCommand.playingPlayers.size(); i++) {
                        int num = new Random().nextInt(materials.length);

                        Player player = Bukkit.getPlayer(StartCommand.playingPlayers.get(i));

                        player.getInventory().addItem(new ItemStack((Material) materials[num]));
                    }
                    ticks = 0;
                }
            }
        }, 100/*WAIT*/, (long) INTERVAL);

        plugin.getConfig().set("Schedule", schedule);
        plugin.saveConfig();
    }
}