package com.itson.Mod.command;

import com.itson.Mod.ModPlugin;
import java.util.Arrays;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ReportCommand implements CommandExecutor {

  private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

  private final ModPlugin plugin;

  public ReportCommand(ModPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<red>Only players can use this command."));
      return true;
    }
    if (args.length < 2) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<red>Usage: /report <player> <reason...>"));
      return true;
    }
    Player target = Bukkit.getPlayerExact(args[0]);
    if (target == null) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<red>Player <white>" + args[0] + " <red>is not online."));
      return true;
    }
    if (target.getUniqueId().equals(player.getUniqueId())) {
      sender.sendMessage(MINI_MESSAGE.deserialize("<red>You cannot report yourself."));
      return true;
    }
    String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

    plugin.getServer().broadcast(MINI_MESSAGE.deserialize(plugin.getModConfig().getReportFormat()
      .replace("{player}", player.getName())
      .replace("{target}", target.getName())
      .replace("{reason}", reason)), "mod.report.notify");
    player.sendMessage(MINI_MESSAGE.deserialize("<green>Thanks, your report has been sent to the staff."));
    return true;
  }
}
