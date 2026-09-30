package com.itson.Mod.listener;

import com.itson.Mod.ModPlugin;
import com.itson.Mod.mute.Mute;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public final class ChatListener implements Listener {

  private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

  private final ModPlugin plugin;

  public ChatListener(ModPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
  public void onChat(AsyncChatEvent event) {
    Mute mute = plugin.getMutesManager().getMute(event.getPlayer().getUniqueId());
    if (mute == null) {
      return;
    }
    event.setCancelled(true);
    event.getPlayer().sendMessage(MINI_MESSAGE.deserialize(plugin.getModConfig().getMuteMessage()
      .replace("{reason}", mute.reason())));
  }
}
