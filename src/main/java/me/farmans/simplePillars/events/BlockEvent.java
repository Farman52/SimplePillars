package me.farmans.simplePillars.events;

import me.farmans.simplePillars.SimplePillars;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class BlockEvent implements Listener {
    public static Set<Location> blocks = new HashSet<>();
    public SimplePillars plugin;

    public BlockEvent(SimplePillars plugin) {this.plugin = plugin;}

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (plugin.getConfig().getInt("Schedule") != -1) {
            blocks.add(event.getBlockPlaced().getLocation());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (plugin.getConfig().getInt("Schedule") != -1 && blocks.contains(event.getBlock())) {
            blocks.remove(event.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onFallingBlockLand(EntityChangeBlockEvent event) {
        if (plugin.getConfig().getInt("Schedule") != -1) {
            if (event.getEntityType() == EntityType.FALLING_BLOCK) {
                blocks.add(event.getBlock().getLocation());
            }
        }
    }

    // Stromy, houby atd.
    @EventHandler
    public void onStructureGrow(@NotNull StructureGrowEvent event) {
        if (plugin.getConfig().getInt("Schedule") != -1) {
            for (BlockState state : event.getBlocks()) {
                blocks.add(state.getLocation());
            }
        }
    }

    // Rostliny, plodiny, sugar cane, kaktus apod.
    @EventHandler
    public void onBlockGrow(BlockGrowEvent event) {
        if (plugin.getConfig().getInt("Schedule") != -1) {
            blocks.add(event.getBlock().getLocation());
            System.out.println("TREST");
        }
    }

    // Tráva, houby, oheň, mycelium apod.
    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event) {
        if (plugin.getConfig().getInt("Schedule") != -1) {
            blocks.add(event.getBlock().getLocation());
        }
    }

    //Voda, láva
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (plugin.getConfig().getInt("Schedule") != -1) {
            if (event.getToBlock().getType() == Material.WATER || event.getToBlock().getType() == Material.LAVA) {
                blocks.add(event.getToBlock().getLocation());
            }
            if (event.getBlock().getType() == Material.WATER || event.getBlock().getType() == Material.LAVA) {
                blocks.add(event.getBlock().getLocation());
            }
        }
    }
    @EventHandler
    public void onBlockPhysics(BlockPhysicsEvent event) {
        if (plugin.getConfig().getInt("Schedule") != -1) {
            if (event.getBlock().getType() == Material.WATER || event.getBlock().getType() == Material.LAVA) {
                blocks.add(event.getBlock().getLocation());
            }
        }
    }
}
