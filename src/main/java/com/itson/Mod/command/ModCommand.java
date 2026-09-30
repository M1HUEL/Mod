package com.itson.Mod.command;

import com.itson.Mod.ModPlugin;
import com.itson.Mod.config.ModConfig;
import com.itson.Mod.history.HistoryEntry;
import com.itson.Mod.history.HistoryManager;
import com.itson.Mod.history.HistoryType;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ModCommand implements CommandExecutor {

  private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

  private final ModPlugin plugin;

  public ModCommand(ModPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(
    @NotNull CommandSender sender,
    @NotNull Command command,
    @NotNull String label,
    @NotNull String[] args) {
    if (args.length == 0) {
      usage(sender);
      return true;
    }
    switch (args[0].toLowerCase()) {
      case "warn" ->
        warn(sender, args);
      case "history" ->
        history(sender, args);
      case "clear" ->
        clear(sender, args, 1);
      default ->
        usage(sender);
    }
    return true;
  }

  private void warn(CommandSender sender, String[] args) {
    if (!sender.hasPermission("mod.warn")) {
      deny(sender);
      return;
    }
    if (args.length < 2) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<red>Usage: /mod warn <player> <reason...>"));
      return;
    }
    if (args[1].equalsIgnoreCase("clear")) {
      clear(sender, args, 2);
      return;
    }
    Player target = Bukkit.getPlayerExact(args[1]);
    if (target == null) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<red>Player <white>" + args[1] + " <red>is not online."));
      return;
    }
    String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
    if (reason.isEmpty()) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<red>Usage: /mod warn <player> <reason...>"));
      return;
    }

    HistoryManager manager = plugin.getHistoryManager();
    manager.addEntry(target.getUniqueId(), HistoryType.WARN, reason, sender.getName());
    ModConfig config = plugin.getModConfig();
    int count = manager.getCount(target.getUniqueId(), HistoryType.WARN);
    int max = config.getMaxWarnings();

    plugin.getServer().broadcast(MINI_MESSAGE.deserialize(
      "<red>[WARN] <white>" + target.getName() + " <gray>was warned by <white>" + sender.getName()
      + " <gray>(" + count + "/" + max + ")"),
      "mod.warn");
    target.sendMessage(MINI_MESSAGE.deserialize(config.getWarnMessage()
      .replace("{staff}", sender.getName())
      .replace("{reason}", reason)));

    if (count >= max) {
      target.kick(MINI_MESSAGE.deserialize(config.getKickMessage()));
    }
  }

  private void clear(CommandSender sender, String[] args, int playerIndex) {
    if (!sender.hasPermission("mod.history.clear")) {
      deny(sender);
      return;
    }
    if (args.length < playerIndex + 1) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<red>Usage: /mod clear <player>"));
      return;
    }
    UUID uuid = Bukkit.getOfflinePlayer(args[playerIndex]).getUniqueId();
    plugin.getHistoryManager().clearHistory(uuid);
    sender.sendMessage(
      MINI_MESSAGE.deserialize("<green>Cleared the whole history of <white>" + args[playerIndex] + "<green>."));
  }

  private void history(CommandSender sender, String[] args) {
    if (!sender.hasPermission("mod.history")) {
      deny(sender);
      return;
    }
    if (args.length < 2) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<red>Usage: /mod history <player>"));
      return;
    }
    UUID uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
    HistoryManager manager = plugin.getHistoryManager();
    List<HistoryEntry> entries = manager.getHistory(uuid);
    if (entries.isEmpty()) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<green>" + args[1] + " <gray>has no history."));
      return;
    }
    sender.sendMessage(MINI_MESSAGE.deserialize("<yellow>History of <white>" + args[1] + "<yellow>:"));
    int index = 1;
    for (HistoryEntry entry : entries) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<gray>" + index + ". <red>" + entry.type().name()
        + " <white>" + entry.reason() + " <dark_gray>(<gray>" + entry.staff() + ", " + entry.date()
        + "<dark_gray>)"));
      index++;
    }
  }

  private void usage(CommandSender sender) {
    sender.sendMessage(MINI_MESSAGE.deserialize(
      "<red>Usage: /mod warn <player> <reason...> | /mod clear <player> | /mod history <player>"));
  }

  private void deny(CommandSender sender) {
    sender.sendMessage(MINI_MESSAGE.deserialize("<red>You do not have permission to do this."));
  }
}
