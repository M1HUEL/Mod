package com.itson.Mod.warn;

import com.itson.Mod.ModPlugin;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.configuration.file.YamlConfiguration;

public final class WarningsManager {

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private final ModPlugin plugin;
  private final File file;
  private final YamlConfiguration warnings;

  public WarningsManager(ModPlugin plugin) {
    this.plugin = plugin;
    this.file = new File(plugin.getDataFolder(), "warnings.yml");
    this.warnings = YamlConfiguration.loadConfiguration(file);
  }

  public List<Warning> getWarnings(UUID uuid) {
    List<Warning> result = new ArrayList<>();
    for (Map<?, ?> map : warnings.getMapList(section(uuid))) {
      result.add(new Warning(
        String.valueOf(map.get("reason")),
        String.valueOf(map.get("staff")),
        String.valueOf(map.get("date"))));
    }
    return result;
  }

  public int getWarningCount(UUID uuid) {
    return getWarnings(uuid).size();
  }

  public void addWarning(UUID uuid, String reason, String staff) {
    List<Map<String, String>> stored = new ArrayList<>();
    for (Warning warning : getWarnings(uuid)) {
      Map<String, String> entry = new HashMap<>();
      entry.put("reason", warning.reason());
      entry.put("staff", warning.staff());
      entry.put("date", warning.date());
      stored.add(entry);
    }
    Map<String, String> entry = new HashMap<>();
    entry.put("reason", reason);
    entry.put("staff", staff);
    entry.put("date", LocalDateTime.now().format(DATE_FORMAT));
    stored.add(entry);
    warnings.set(section(uuid), stored);
    save();
  }

  public void clearWarnings(UUID uuid) {
    warnings.set(section(uuid), null);
    save();
  }

  private String section(UUID uuid) {
    return "warnings." + uuid;
  }

  private void save() {
    try {
      warnings.save(file);
    } catch (IOException exception) {
      plugin.getLogger().severe("Could not save warnings.yml: " + exception.getMessage());
    }
  }
}
