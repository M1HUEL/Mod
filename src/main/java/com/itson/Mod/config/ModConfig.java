package com.itson.Mod.config;

import org.bukkit.plugin.java.JavaPlugin;

public final class ModConfig {

  private final JavaPlugin plugin;
  private int maxWarnings;
  private String warnMessage;
  private String kickMessage;

  public ModConfig(JavaPlugin plugin) {
    this.plugin = plugin;
    reload();
  }

  public void reload() {
    plugin.reloadConfig();
    maxWarnings = Math.max(1, plugin.getConfig().getInt("max-warnings", 3));
    warnMessage = plugin.getConfig()
      .getString("warn-message", "<red>You have been warned by <white>{staff}<dark_gray>: <yellow>{reason}");
    kickMessage = plugin.getConfig()
      .getString("kick-message", "<red>You have been kicked for receiving too many warnings.");
  }

  public int getMaxWarnings() {
    return maxWarnings;
  }

  public String getWarnMessage() {
    return warnMessage;
  }

  public String getKickMessage() {
    return kickMessage;
  }
}
