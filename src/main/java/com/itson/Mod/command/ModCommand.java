package com.itson.Mod.command;

import com.itson.Mod.ModPlugin;
import com.itson.Mod.config.ModConfig;
import com.itson.Mod.history.HistoryEntry;
import com.itson.Mod.history.HistoryManager;
import com.itson.Mod.history.HistoryType;
import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.ban.BanListType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.ban.ProfileBanList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ModCommand implements CommandExecutor, TabCompleter {

  private static final List<String> SUBCOMMANDS =
      Arrays.asList("warn", "ban", "kick", "unban", "history", "clear", "reload");
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
      case "ban" ->
        ban(sender, args);
      case "kick" ->
        kick(sender, args);
      case "unban" ->
        unban(sender, args);
      case "history" ->
        history(sender, args);
      case "clear" ->
        clear(sender, args, 1);
      case "reload" ->
        reload(sender);
      default ->
        usage(sender);
    }
    return true;
  }

  private void reload(CommandSender sender) {
    if (!sender.hasPermission("mod.reload")) {
      deny(sender);
      return;
    }
    plugin.getModConfig().reload();
    plugin.getHistoryManager().reload();
    sender.sendMessage(MINI_MESSAGE.deserialize("<green>Mod configuration reloaded."));
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
      target.kick(MINI_MESSAGE.deserialize(config.getWarningsKickMessage()));
    }
  }

  private void ban(CommandSender sender, String[] args) {
    if (!sender.hasPermission("mod.ban")) {
      deny(sender);
      return;
    }
    if (args.length < 2) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<red>Usage: /mod ban <player> <reason...>"));
      return;
    }
    String name = args[1];
    String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
    if (reason.isEmpty()) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<red>Usage: /mod ban <player> <reason...>"));
      return;
    }

    Player target = Bukkit.getPlayerExact(name);
    UUID uuid;
    if (target != null) {
      uuid = target.getUniqueId();
    } else {
      OfflinePlayer offline = Bukkit.getOfflinePlayerIfCached(name);
      if (offline == null) {
        sender.sendMessage(MINI_MESSAGE.deserialize("<red>Player <white>" + name + " <red>was never seen on this server."));
        return;
      }
      uuid = offline.getUniqueId();
    }

    ProfileBanList banList = Bukkit.getBanList(BanListType.PROFILE);
    PlayerProfile profile = Bukkit.createProfile(uuid, name);
    if (banList.isBanned(profile)) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<red>Player <white>" + name + " <red>is already banned."));
      return;
    }

    if (target != null) {
      target.kick(MINI_MESSAGE.deserialize(plugin.getModConfig().getBanMessage()
        .replace("{reason}", reason)));
    }
    banList.addBan(profile, reason, (Instant) null, sender.getName());
    plugin.getHistoryManager().addEntry(uuid, HistoryType.BAN, reason, sender.getName());
    plugin.getServer().broadcast(MINI_MESSAGE.deserialize(
      "<red>[BAN] <white>" + name + " <gray>was banned by <white>" + sender.getName()
      + " <dark_gray>(" + reason + ")"),
      "mod.ban");
  }

  private void kick(CommandSender sender, String[] args) {
    if (!sender.hasPermission("mod.kick")) {
      deny(sender);
      return;
    }
    if (args.length < 2) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<red>Usage: /mod kick <player> <reason...>"));
      return;
    }
    Player target = Bukkit.getPlayerExact(args[1]);
    if (target == null) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<red>Player <white>" + args[1] + " <red>is not online."));
      return;
    }
    String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
    if (reason.isEmpty()) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<red>Usage: /mod kick <player> <reason...>"));
      return;
    }

    target.kick(MINI_MESSAGE.deserialize(plugin.getModConfig().getKickMessage()
        .replace("{staff}", sender.getName())
        .replace("{reason}", reason)));
    plugin.getHistoryManager().addEntry(target.getUniqueId(), HistoryType.KICK, reason, sender.getName());
    plugin.getServer().broadcast(MINI_MESSAGE.deserialize(
            "<red>[KICK] <white>" + target.getName() + " <gray>was kicked by <white>" + sender.getName()
                + " <dark_gray>(" + reason + ")"),
        "mod.kick");
  }

  private void unban(CommandSender sender, String[] args) {
    if (!sender.hasPermission("mod.ban")) {
      deny(sender);
      return;
    }
    if (args.length < 2) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<red>Usage: /mod unban <player>"));
      return;
    }
    String name = args[1];
    OfflinePlayer offline = Bukkit.getOfflinePlayerIfCached(name);
    if (offline == null) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<red>Player <white>" + name + " <red>was never seen on this server."));
      return;
    }
    ProfileBanList banList = Bukkit.getBanList(BanListType.PROFILE);
    PlayerProfile profile = Bukkit.createProfile(offline.getUniqueId(), name);
    if (!banList.isBanned(profile)) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<red>Player <white>" + name + " <red>is not banned."));
      return;
    }
    banList.pardon(profile);
    sender.sendMessage(MINI_MESSAGE.deserialize("<green>Unbanned <white>" + name + "<green>."));
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
        "<red>Usage: /mod warn <player> <reason...> | /mod ban <player> <reason...> | /mod kick <player> <reason...>"
            + " | /mod unban <player> | /mod clear <player> | /mod history <player> | /mod reload"));
  }

  private void deny(CommandSender sender) {
    sender.sendMessage(MINI_MESSAGE.deserialize("<red>You do not have permission to do this."));
  }

  @Override
  public @Nullable
  List<String> onTabComplete(
    @NotNull CommandSender sender,
    @NotNull Command command,
    @NotNull String label,
    @NotNull String[] args) {
    if (args.length == 1) {
      String input = args[0].toLowerCase();
      List<String> completions = new ArrayList<>();
      for (String sub : SUBCOMMANDS) {
        if (sub.startsWith(input)) {
          completions.add(sub);
        }
      }
      return completions;
    }
    if (args.length == 2) {
      String sub = args[0].toLowerCase();
      if (sub.equals("warn") && !args[1].isEmpty()) {
        return List.of("clear");
      }
      if (SUBCOMMANDS.contains(sub)) {
        return playerCompletions(args[1]);
      }
    }
    if (args.length == 3 && args[0].equalsIgnoreCase("warn") && args[1].equalsIgnoreCase("clear")) {
      return playerCompletions(args[2]);
    }
    return Collections.emptyList();
  }

  private List<String> playerCompletions(String input) {
    List<String> completions = new ArrayList<>();
    String lower = input.toLowerCase();
    for (Player player : Bukkit.getOnlinePlayers()) {
      if (player.getName().toLowerCase().startsWith(lower)) {
        completions.add(player.getName());
      }
    }
    for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
      String name = offline.getName();
      if (name != null && name.toLowerCase().startsWith(lower)
        && completions.stream().noneMatch(n -> n.equalsIgnoreCase(name))) {
        completions.add(name);
      }
    }
    return completions;
  }
}
