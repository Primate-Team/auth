package ru.samsonium.primate.auth.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class NotSignedInListener implements Listener {

    private boolean isNotSignedIn(Player p) {
        return !p.hasMetadata("auth");
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (isNotSignedIn(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (isNotSignedIn(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onChatMessage(AsyncChatEvent e) {
        if (isNotSignedIn(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (isNotSignedIn(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (isNotSignedIn((Player) e.getPlayer()))
            e.getInventory().close();
    }
}
