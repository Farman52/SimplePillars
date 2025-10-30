package me.farmans.simplePillars;

import me.farmans.simplePillars.commands.*;
import me.farmans.simplePillars.events.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class SimplePillars extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Pillary frčí");

        File file = new File(getDataFolder() + File.separator + "config.yml");
        if (!file.exists()) {
            getConfig().addDefault("Distance", 15);
            getConfig().addDefault("Height", 20);
            getConfig().addDefault("FallMax", 25);
            getConfig().addDefault("Schedule", -1);
            getConfig().addDefault("Interval", 2);
            getConfig().addDefault("Period", 100);
            getConfig().addDefault("Center", "0 0");
            getConfig().addDefault("Title", "SirYakari Pillars");
            getConfig().addDefault("TitleHex", "\\#00C3FF \\#2EE6E6 \\#FF66C4 \\#FF42B6");
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            getConfig().set("Schedule", -1);
            saveConfig();
            reloadConfig();
        }

        getCommand("pstart").setExecutor(new StartCommand(this));
        getCommand("pstop").setExecutor(new StopCommand(this));
        getCommand("pdistance").setExecutor(new DistanceCommand(this));
        getCommand("pheight").setExecutor(new HeightCommand(this));
        getCommand("pfallmax").setExecutor(new FallMaxCommand(this));

        getServer().getPluginManager().registerEvents(new BlockEvent(this), this);
        getServer().getPluginManager().registerEvents(new DeathEvent(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinEvent(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitEvent(this), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveEvent(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Pillary už nefrčí");
    }
}
