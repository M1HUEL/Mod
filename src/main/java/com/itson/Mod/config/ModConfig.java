package com.itson.Mod.config;

import org.bukkit.plugin.java.JavaPlugin;

public final class ModConfig {

  private final JavaPlugin plugin;
  private int maxWarnings;
  private String warnMessage;
  private String warnBroadcast;
  private String banMessage;
  private String banBroadcast;
  private String kickMessage;
  private String kickBroadcast;
  private String warningsKickMessage;
  private String muteMessage;
  private String muteBroadcast;
  private String unmuteBroadcast;
  private String reportFormat;
  private String permanentText;

  public ModConfig(JavaPlugin plugin) {
    this.plugin = plugin;
    reload();
  }

  public void reload() {
    plugin.reloadConfig();
    maxWarnings = Math.max(1, plugin.getConfig().getInt("max-warnings", 3));
    warnMessage = plugin.getConfig()
      .getString("warn-message", "<red>You have been warned by <white>{staff}<dark_gray>: <yellow>{reason}");
    warnBroadcast = plugin.getConfig()
      .getString("warn-broadcast", "<red>[WARN] <white>{target}<gray> was warned by <white>{staff}<gray> ({count}/{max})");
    banMessage = plugin.getConfig()
      .getString("ban-message", "<red>You have been banned.<newline><gray>Reason: <white>{reason}");
    banBroadcast = plugin.getConfig()
      .getString("ban-broadcast", "<red>[BAN] <white>{target}<gray> was banned by <white>{staff}<dark_gray> ({reason})");
    kickMessage = plugin.getConfig()
      .getString("kick-message", "<red>Kicked by <white>{staff}<newline><gray>Reason: <white>{reason}");
    kickBroadcast = plugin.getConfig()
      .getString("kick-broadcast", "<red>[KICK] <white>{target}<gray> was kicked by <white>{staff}<dark_gray> ({reason})");
    warningsKickMessage = plugin.getConfig()
      .getString("warnings-kick-message", "<red>You have been kicked for receiving too many warnings.");
    muteMessage = plugin.getConfig()
      .getString("mute-message", "<red>You are muted.<newline><gray>Reason: <white>{reason}");
    muteBroadcast = plugin.getConfig()
      .getString("mute-broadcast", "<red>[MUTE] <white>{target}<gray> was muted by <white>{staff}<dark_gray> ({reason})");
    unmuteBroadcast = plugin.getConfig()
      .getString("unmute-broadcast", "<green>[UNMUTE] <white>{target}<gray> was unmuted by <white>{staff}");
    reportFormat = plugin.getConfig()
      .getString("report-format", "<gold>[REPORT]<reset> <white>{player}<gray> reported <white>{target}<gray>: <white>{reason}");
    permanentText = plugin.getConfig().getString("permanent-text", "never");
  }

  public String getPermanentText() {
    return permanentText;
  }

  public int getMaxWarnings() {
    return maxWarnings;
  }

  public String getWarnMessage() {
    return warnMessage;
  }

  public String getWarnBroadcast() {
    return warnBroadcast;
  }

  public String getBanMessage() {
    return banMessage;
  }

  public String getBanBroadcast() {
    return banBroadcast;
  }

  public String getKickMessage() {
    return kickMessage;
  }

  public String getKickBroadcast() {
    return kickBroadcast;
  }

  public String getWarningsKickMessage() {
    return warningsKickMessage;
  }

  public String getReportFormat() {
    return reportFormat;
  }

  public String getMuteMessage() {
    return muteMessage;
  }

  public String getMuteBroadcast() {
    return muteBroadcast;
  }

  public String getUnmuteBroadcast() {
    return unmuteBroadcast;
  }
}
