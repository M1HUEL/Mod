package com.itson.Mod.config;

import org.bukkit.plugin.java.JavaPlugin;

public final class ModConfig {

  private final JavaPlugin plugin;
  private int maxWarnings;
  private String warnMessage;
  private String banMessage;
  private String kickMessage;
  private String warningsKickMessage;
  private String reportFormat;

  public ModConfig(JavaPlugin plugin) {
    this.plugin = plugin;
    reload();
  }

  public void reload() {
    plugin.reloadConfig();
    maxWarnings = Math.max(1, plugin.getConfig().getInt("max-warnings", 3));
    warnMessage = plugin.getConfig()
      .getString("warn-message", "<red>You have been warned by <white>{staff}<dark_gray>: <yellow>{reason}");
    banMessage = plugin.getConfig()
      .getString("ban-message", "<red>You have been banned.<newline><gray>Reason: <white>{reason}");
    kickMessage = plugin.getConfig()
      .getString("kick-message", "<red>Kicked by <white>{staff}<newline><gray>Reason: <white>{reason}");
    warningsKickMessage = plugin.getConfig()
      .getString("warnings-kick-message", "<red>You have been kicked for receiving too many warnings.");
    reportFormat = plugin.getConfig()
      .getString("report-format", "<gold>[REPORT]<reset> <white>{player}<gray> reported <white>{target}<gray>: <white>{reason}");
  }

  public int getMaxWarnings() {
    return maxWarnings;
  }

  public String getWarnMessage() {
    return warnMessage;
  }

  public String getBanMessage() {
    return banMessage;
  }

  public String getKickMessage() {
    return kickMessage;
  }

  public String getWarningsKickMessage() {
    return warningsKickMessage;
  }

  public String getReportFormat() {
    return reportFormat;
  }
}
