package me.farmans.simplePillars.commands;

import me.farmans.simplePillars.SimplePillars;
import me.farmans.simplePillars.events.BlockEvent;
import me.farmans.simplePillars.utils.ChatUtil;
import me.farmans.simplePillars.utils.RunnableUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.farmans.simplePillars.utils.ChatUtil.gradient;

public class StartCommand implements CommandExecutor, TabExecutor {
    SimplePillars plugin;

    public static List<UUID> playingPlayers = new ArrayList<>();
    public static Map<UUID, Scoreboard> scoreboards = new HashMap<>();

    public StartCommand(SimplePillars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!label.equalsIgnoreCase("pstart")) return false;

        if (plugin.getConfig().getInt("Schedule") != -1) {
            commandSender.sendMessage("Pillary jsou již zapnutý");
            return true;
        }

        if (args.length > 2) {
            commandSender.sendMessage("Zadávej pouze X a Z coords, Y je nastavený v /pheight");
            return true;
        }

        Player sender = (Player) commandSender;

        int x = args.length > 1 ? Integer.parseInt(args[0]) : (int) sender.getLocation().getX();
        int z = args.length > 1 ? Integer.parseInt(args[1]) : (int) sender.getLocation().getZ();

        plugin.getConfig().set("Center", String.format("%s %s", x, z));
        plugin.saveConfig();

        Object[] players = plugin.getServer().getOnlinePlayers().toArray();

        BossBar bossbar = plugin.getServer().createBossBar(new NamespacedKey(plugin, "simplepillars"), ChatColor.BOLD + "=== TIMER ===", BarColor.PURPLE, BarStyle.SOLID);

        int radius = plugin.getConfig().getInt("Radius");
        int height = plugin.getConfig().getInt("Height");
        for (int i = 0; i < players.length; i++) {
            double angle = 2 * Math.PI * i / players.length;
            int xAngle = (int) Math.round(Math.cos(angle) * radius);
            int yAngle = (int) Math.round(Math.sin(angle) * radius);

            Player player = (Player) players[i];
            Location location = new Location(player.getWorld(), xAngle + x, height, yAngle + z);
            for (int j = height; j >= -64; j--) {
                Block block = location.add(0, -1, 0).getBlock();
                block.setType(Material.BEDROCK);
                BlockEvent.blocks.add(block.getLocation());
            }
            player.teleport(location.add(0.5, height + 65, 0.5));
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            bossbar.addPlayer(player);

            Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
            String title = gradient(plugin.getConfig().getString("Title"), true,
                    plugin.getConfig().getString("TitleHex").split(" ")
            );
            if (title.length() > 128) title = plugin.getConfig().getString("ScoreboardTitle");
            Objective obj = scoreboard.registerNewObjective("simplepillars", Criteria.DUMMY, title);
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.getScore(ChatColor.BOLD + "Name: " + ChatColor.RESET + player.getName()).setScore(2);
            obj.getScore(ChatColor.BOLD + "Alive: " + ChatColor.RESET + plugin.getServer().getOnlinePlayers().size()).setScore(1);
            obj.getScore(ChatColor.BOLD + "Kills: " + ChatColor.RESET + "0").setScore(0);

            player.setScoreboard(scoreboard);
            scoreboards.put(player.getUniqueId(), scoreboard);

            playingPlayers.add(player.getUniqueId());
        }
        commandSender.sendMessage("Hráči teleportnuti na místa");

        sender.getWorld().setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        sender.getWorld().setGameRule(GameRule.RANDOM_TICK_SPEED, 0);

        RunnableUtil.startGame(plugin);

        ChatUtil.sendAllMessage(plugin, "Hra započala!", true);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) commandSender;

        return switch (args.length) {
            case 1 -> Collections.singletonList((int)player.getLocation().getX() + "");
            case 2 -> Collections.singletonList((int)player.getLocation().getZ() + "");
            default -> List.of();
        };
    }
}
