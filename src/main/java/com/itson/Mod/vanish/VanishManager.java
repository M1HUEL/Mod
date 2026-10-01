package com.itson.Mod.vanish;

import com.itson.Mod.ModPlugin;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class VanishManager {

  private final ModPlugin plugin;
  private final Set<UUID> vanished = new HashSet<>();

  public VanishManager(ModPlugin plugin) {
    this.plugin = plugin;
  }

  public boolean isVanished(UUID uuid) {
    return vanished.contains(uuid);
  }

  public void vanish(Player player) {
    vanished.add(player.getUniqueId());
    applyHiddenState(player);
  }

  public void unvanish(Player player) {
    vanished.remove(player.getUniqueId());
    applyVisibleState(player);
  }

  public void clear() {
    for (UUID uuid : Set.copyOf(vanished)) {
      Player player = Bukkit.getPlayer(uuid);
      if (player != null) {
        unvanish(player);
      } else {
        vanished.remove(uuid);
      }
    }
  }

  private void applyHiddenState(Player target) {
    for (Player viewer : Bukkit.getOnlinePlayers()) {
      if (viewer.getUniqueId().equals(target.getUniqueId())) {
        continue;
      }
      viewer.hidePlayer(plugin, target);
      viewer.unlistPlayer(target);
    }
  }

  private void applyVisibleState(Player target) {
    for (Player viewer : Bukkit.getOnlinePlayers()) {
      if (viewer.getUniqueId().equals(target.getUniqueId())) {
        continue;
      }
      viewer.showPlayer(plugin, target);
      viewer.listPlayer(target);
    }
  }
}
