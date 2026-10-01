package com.itson.Mod;

import com.itson.Mod.command.ModCommand;
import com.itson.Mod.command.ReportCommand;
import com.itson.Mod.config.ModConfig;
import com.itson.Mod.freeze.FreezesManager;
import com.itson.Mod.history.HistoryManager;
import com.itson.Mod.listener.ChatListener;
import com.itson.Mod.listener.FreezeListener;
import com.itson.Mod.listener.VanishListener;
import com.itson.Mod.mute.MutesManager;
import com.itson.Mod.vanish.VanishManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ModPlugin extends JavaPlugin {

  private ModConfig modConfig;
  private HistoryManager historyManager;
  private MutesManager mutesManager;
  private FreezesManager freezesManager;
  private VanishManager vanishManager;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    modConfig = new ModConfig(this);
    historyManager = new HistoryManager(this);
    mutesManager = new MutesManager(this);
    freezesManager = new FreezesManager();
    vanishManager = new VanishManager(this);
    ModCommand modCommand = new ModCommand(this);
    getCommand("mod").setExecutor(modCommand);
    getCommand("mod").setTabCompleter(modCommand);
    getCommand("report").setExecutor(new ReportCommand(this));
    getServer().getPluginManager().registerEvents(new ChatListener(this), this);
    getServer().getPluginManager().registerEvents(new FreezeListener(this), this);
    getServer().getPluginManager().registerEvents(new VanishListener(this), this);
    getLogger().info("Mod v" + getPluginMeta().getVersion() + " enabled.");
  }

  @Override
  public void onDisable() {
    vanishManager.clear();
    freezesManager.clear();
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

  public FreezesManager getFreezesManager() {
    return freezesManager;
  }

  public VanishManager getVanishManager() {
    return vanishManager;
  }
}
