package com.itson.Mod.mute;

import com.itson.Mod.ModPlugin;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public final class MutesManager {

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private final ModPlugin plugin;
  private final File file;
  private YamlConfiguration mutes;

  public MutesManager(ModPlugin plugin) {
    this.plugin = plugin;
    this.file = new File(plugin.getDataFolder(), "mutes.yml");
    this.mutes = YamlConfiguration.loadConfiguration(file);
  }

  public void reload() {
    mutes = YamlConfiguration.loadConfiguration(file);
  }

  public boolean isMuted(UUID uuid) {
    return getMute(uuid) != null;
  }

  public Mute getMute(UUID uuid) {
    ConfigurationSection section = mutes.getConfigurationSection(section(uuid));
    if (section == null) {
      return null;
    }
    Long expires = section.getLong("expires");
    if (expires != null && expires > 0L && System.currentTimeMillis() >= expires) {
      unmute(uuid);
      return null;
    }
    return new Mute(
      section.getString("name", ""),
      section.getString("reason", ""),
      section.getString("staff", ""),
      section.getString("date", ""));
  }

  public void mute(UUID uuid, String name, String reason, String staff, Duration duration) {
    String section = section(uuid);
    mutes.set(section + ".name", name);
    mutes.set(section + ".reason", reason);
    mutes.set(section + ".staff", staff);
    mutes.set(section + ".date", LocalDateTime.now().format(DATE_FORMAT));
    if (duration == null) {
      mutes.set(section + ".expires", null);
    } else {
      mutes.set(section + ".expires", System.currentTimeMillis() + duration.toMillis());
    }
    save();
  }

  public void unmute(UUID uuid) {
    mutes.set(section(uuid), null);
    save();
  }

  private String section(UUID uuid) {
    return "mutes." + uuid;
  }

  private void save() {
    try {
      mutes.save(file);
    } catch (IOException exception) {
      plugin.getLogger().severe("Could not save mutes.yml: " + exception.getMessage());
    }
  }
}
