package me.farmans.simplePillars;

import me.farmans.simplePillars.commands.HeightCommand;
import me.farmans.simplePillars.commands.RadiusCommand;
import me.farmans.simplePillars.commands.StartCommand;
import me.farmans.simplePillars.commands.StopCommand;
import me.farmans.simplePillars.events.BlockEvent;
import me.farmans.simplePillars.events.DamageEvent;
import me.farmans.simplePillars.events.PlayerJoinEvent;
import me.farmans.simplePillars.events.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class SimplePillars extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Pillary frčí");

        File file = new File(getDataFolder() + File.separator + "config.yml");
        if (!file.exists()) {
            getConfig().addDefault("Radius", 10);
            getConfig().addDefault("Schedule", -1);
            getConfig().addDefault("Interval", 2);
            getConfig().addDefault("Center", "0 0");
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            getConfig().set("Schedule", -1);
            saveConfig();
            reloadConfig();
        }

        getCommand("pstart").setExecutor(new StartCommand(this));
        getCommand("pstop").setExecutor(new StopCommand(this));
        getCommand("pradius").setExecutor(new RadiusCommand(this));
        getCommand("pheight").setExecutor(new HeightCommand(this));

        getServer().getPluginManager().registerEvents(new BlockEvent(this), this);
        getServer().getPluginManager().registerEvents(new DamageEvent(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinEvent(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitEvent(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Pillary už nefrčí");
    }
}
