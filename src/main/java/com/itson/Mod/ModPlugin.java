package com.itson.Mod;

import com.itson.Mod.command.ModCommand;
import com.itson.Mod.command.ReportCommand;
import com.itson.Mod.config.ModConfig;
import com.itson.Mod.history.HistoryManager;
import com.itson.Mod.listener.ChatListener;
import com.itson.Mod.mute.MutesManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ModPlugin extends JavaPlugin {

  private ModConfig modConfig;
  private HistoryManager historyManager;
  private MutesManager mutesManager;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    modConfig = new ModConfig(this);
    historyManager = new HistoryManager(this);
    mutesManager = new MutesManager(this);
    ModCommand modCommand = new ModCommand(this);
    getCommand("mod").setExecutor(modCommand);
    getCommand("mod").setTabCompleter(modCommand);
    getCommand("report").setExecutor(new ReportCommand(this));
    getServer().getPluginManager().registerEvents(new ChatListener(this), this);
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

  public MutesManager getMutesManager() {
    return mutesManager;
  }
}
