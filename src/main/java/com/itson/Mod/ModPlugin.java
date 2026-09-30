package com.itson.Mod;

import com.itson.Mod.command.ModCommand;
import com.itson.Mod.command.ReportCommand;
import com.itson.Mod.config.ModConfig;
import com.itson.Mod.history.HistoryManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ModPlugin extends JavaPlugin {

  private ModConfig modConfig;
  private HistoryManager historyManager;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    modConfig = new ModConfig(this);
    historyManager = new HistoryManager(this);
    ModCommand modCommand = new ModCommand(this);
    getCommand("mod").setExecutor(modCommand);
    getCommand("mod").setTabCompleter(modCommand);
    getCommand("report").setExecutor(new ReportCommand(this));
    getLogger().info("Mod v" + getPluginMeta().getVersion() + " enabled.");
  }

  @Override
  public void onDisable() {
    getLogger().info("Mod has been disabled.");
  }

  public ModConfig getModConfig() {
    return modConfig;
  }

  public HistoryManager getHistoryManager() {
    return historyManager;
  }
}
