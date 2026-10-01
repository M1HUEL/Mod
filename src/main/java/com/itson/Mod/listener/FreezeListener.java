package com.itson.Mod.listener;

import com.itson.Mod.ModPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public final class FreezeListener implements Listener {

  private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
  private static final long NOTIFY_COOLDOWN_MILLIS = 3000L;

  private final ModPlugin plugin;
  private final Map<UUID, Long> lastNotified = new HashMap<>();

  public FreezeListener(ModPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    if (!plugin.getFreezesManager().isFrozen(player.getUniqueId())) {
      return;
    }
    Location from = event.getFrom();
    Location to = event.getTo();
    if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY()
      && from.getBlockZ() == to.getBlockZ()) {
      return;
    }
    event.setCancelled(true);
    notify(player);
  }

  @EventHandler(ignoreCancelled = true)
  public void onToggleFlight(PlayerToggleFlightEvent event) {
    if (plugin.getFreezesManager().isFrozen(event.getPlayer().getUniqueId())) {
      event.setCancelled(true);
    }
  }

  private void notify(Player player) {
    long now = System.currentTimeMillis();
    Long last = lastNotified.get(player.getUniqueId());
    if (last != null && now - last < NOTIFY_COOLDOWN_MILLIS) {
      return;
    }
    lastNotified.put(player.getUniqueId(), now);
    player.sendMessage(MINI_MESSAGE.deserialize(plugin.getModConfig().getFreezeMessage()));
  }
}
