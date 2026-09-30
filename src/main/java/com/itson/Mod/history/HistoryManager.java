package com.itson.Mod.history;

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

public final class HistoryManager {

  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

  private final ModPlugin plugin;
  private final File file;
  private final YamlConfiguration history;

  public HistoryManager(ModPlugin plugin) {
    this.plugin = plugin;
    this.file = new File(plugin.getDataFolder(), "history.yml");
    this.history = YamlConfiguration.loadConfiguration(file);
  }

  public List<HistoryEntry> getHistory(UUID uuid) {
    List<HistoryEntry> entries = new ArrayList<>();
    for (Map<?, ?> map : history.getMapList(section(uuid))) {
      entries.add(new HistoryEntry(
        HistoryType.valueOf(String.valueOf(map.get("type"))),
        String.valueOf(map.get("reason")),
        String.valueOf(map.get("staff")),
        String.valueOf(map.get("date"))));
    }
    return entries;
  }

  public int getCount(UUID uuid, HistoryType type) {
    int count = 0;
    for (HistoryEntry entry : getHistory(uuid)) {
      if (entry.type() == type) {
        count++;
      }
    }
    return count;
  }

  public void addEntry(UUID uuid, HistoryType type, String reason, String staff) {
    List<Map<String, String>> stored = new ArrayList<>();
    for (HistoryEntry entry : getHistory(uuid)) {
      stored.add(toMap(entry));
    }
    stored.add(toMap(new HistoryEntry(type, reason, staff, LocalDateTime.now().format(DATE_FORMAT))));
    history.set(section(uuid), stored);
    save();
  }

  public void clearHistory(UUID uuid) {
    history.set(section(uuid), null);
    save();
  }

  private Map<String, String> toMap(HistoryEntry entry) {
    Map<String, String> map = new HashMap<>();
    map.put("type", entry.type().name());
    map.put("reason", entry.reason());
    map.put("staff", entry.staff());
    map.put("date", entry.date());
    return map;
  }

  private String section(UUID uuid) {
    return "history." + uuid;
  }

  private void save() {
    try {
      history.save(file);
    } catch (IOException exception) {
      plugin.getLogger().severe("Could not save history.yml: " + exception.getMessage());
    }
  }
}
