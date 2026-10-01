package com.itson.Mod.listener;

import com.itson.Mod.ModPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public final class VanishListener implements Listener {

  private final ModPlugin plugin;

  public VanishListener(ModPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onRespawn(PlayerRespawnEvent event) {
    Player player = event.getPlayer();
    if (!plugin.getVanishManager().isVanished(player.getUniqueId())) {
      return;
    }
    plugin.getServer().getScheduler().runTask(plugin, () -> plugin.getVanishManager().vanish(player));
  }
}
