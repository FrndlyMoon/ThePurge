package com.thepurge;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PurgeListener implements Listener {
    private final PurgeManager manager;

    public PurgeListener(PurgeManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            if (manager.getState() != PurgeState.ACTIVE && !manager.isPvpWhenStopped()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (manager.getState() == PurgeState.ACTIVE) {
            manager.resetSurvivalTime(victim.getUniqueId());
            manager.recordDeath(victim.getUniqueId());
            if (killer != null && killer != victim) {
                manager.recordKill(killer.getUniqueId());
            }

            // Hide the killer's name with scribbled text
            String msg = event.getDeathMessage();
            if (msg != null && killer != null) {
                String obscured = ChatColor.MAGIC + "????" + ChatColor.RESET;
                msg = msg.replace(killer.getName(), obscured);
                event.setDeathMessage(msg);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (manager.getState() == PurgeState.ACTIVE) {
            manager.getBossBar().addPlayer(event.getPlayer());
            manager.assignRandomColor(event.getPlayer());
        }
    }
}
