package me.farmans.simplePillars.commands;

import me.farmans.simplePillars.SimplePillars;
import me.farmans.simplePillars.events.BlockEvent;
import me.farmans.simplePillars.events.DeathEvent;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StopCommand implements CommandExecutor {
    SimplePillars plugin;

    public StopCommand(SimplePillars plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] strings) {
        if (!label.equalsIgnoreCase("pstop")) return false;

        if (plugin.getConfig().getInt("Schedule") == -1) {
            commandSender.sendMessage("Žádný pillary nejedou");
            return true;
        }

        plugin.getServer().getScheduler().cancelTask(plugin.getConfig().getInt("Schedule"));

        plugin.getConfig().set("Schedule", -1);
        plugin.saveConfig();
        commandSender.sendMessage("Pillary přestaly jet");

        for (Location location : BlockEvent.blocks) {
            location.getBlock().setType(Material.AIR);
        }
        for (Entity entity : ((Player)commandSender).getWorld().getEntities()) {
            if (entity instanceof Player) continue;
            entity.remove();
        }
        commandSender.sendMessage("Aréna vyčištěna");

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.getInventory().clear();
            player.teleport(player.getWorld().getSpawnLocation());
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            player.getActivePotionEffects().clear();
            if (!player.isOp()) {
                player.setGameMode(GameMode.SURVIVAL);
            } else player.setGameMode(GameMode.CREATIVE);
        }
        System.out.println("Hráči portnuti na world spawn");

        ((Player) commandSender).getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, false);
        ((Player) commandSender).getWorld().setGameRule(GameRule.RANDOM_TICK_SPEED, 3);

        StartCommand.playingPlayers.clear();
        DeathEvent.kills.clear();
        StartCommand.scoreboards.clear();
        BlockEvent.blocks.clear();
        System.out.println("Data resetovány");

        plugin.getServer().getBossBar(new NamespacedKey(plugin, "simplepillars")).removeAll();

        return true;
    }
}
