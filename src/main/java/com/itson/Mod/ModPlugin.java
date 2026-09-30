package com.itson.Mod;

import org.bukkit.plugin.java.JavaPlugin;

public final class ModPlugin extends JavaPlugin {

  @Override
  public void onEnable() {
    getLogger().info("Mod v" + getPluginMeta().getVersion() + " enabled.");
  }

  @Override
  public void onDisable() {
    getLogger().info("Mod has been disabled.");
  }
}
