package cc.dreamcode.kits.command;

import cc.dreamcode.command.CommandBase;
import cc.dreamcode.command.annotation.Arg;
import cc.dreamcode.command.annotation.Async;
import cc.dreamcode.command.annotation.Command;
import cc.dreamcode.command.annotation.Completion;
import cc.dreamcode.command.annotation.Executor;
import cc.dreamcode.command.annotation.Permission;
import cc.dreamcode.kits.config.KitConfig;
import cc.dreamcode.kits.config.MessageConfig;
import cc.dreamcode.kits.config.PluginConfig;
import cc.dreamcode.kits.menu.KitMenuSetup;
import cc.dreamcode.kits.menu.MenuConfig;
import cc.dreamcode.kits.service.KitService;
import cc.dreamcode.notice.bukkit.BukkitNotice;
import cc.dreamcode.utilities.TimeUtil;
import eu.okaeri.configs.exception.OkaeriException;
import eu.okaeri.injector.annotation.Inject;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Command(name = "akit")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class AkitCommand implements CommandBase {

  private final PluginConfig pluginConfig;
  private final MessageConfig messageConfig;
  private final KitConfig kitConfig;
  private final MenuConfig menuConfig;
  private final KitMenuSetup kitMenuSetup;
  private final KitService kitService;

  @Permission("dream-kits.create")
  @Executor(path = "create", description = "Tworzy nowy kit")
  public BukkitNotice create(Player player, @Arg String name, @Arg int limit,
      @Arg String permission, @Arg Duration cooldown, @Arg int slot, @Arg String iconSource,
      @Arg String rank) {
    ItemStack iconStack;

    if (iconSource.equalsIgnoreCase("hand")) {
      ItemStack itemInHand = player.getInventory().getItemInHand();
      if (itemInHand.getType() == Material.AIR) {
        player.sendMessage("§cMusisz trzymać przedmiot w ręku, aby użyć opcji 'hand'!");
        return null;
      }
      iconStack = itemInHand.clone();
    } else {
      Material mat = Material.matchMaterial(iconSource.toUpperCase());
      if (mat == null) {
        player.sendMessage("§cNie znaleziono materiału: " + iconSource);
        return null;
      }
      iconStack = new ItemStack(mat);
    }

    return this.kitService.createKit(player, name, limit, permission, cooldown, slot, iconStack,
        rank);
  }

  @Permission("dream-kits.give")
  @Completion(arg = "kitName", value = "@allkits")
  @Completion(arg = "player", value = "@allplayers")
  @Executor(path = "give", description = "Daj zestaw graczowi.")
  BukkitNotice give(@Arg String kitName, @Arg String player) {

    if (player.equalsIgnoreCase("all") || player.equalsIgnoreCase("*")) {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        BukkitNotice notice = this.kitService.giveKit(kitName, onlinePlayer);
        if (notice != null) {
          notice.send(onlinePlayer);
        }
      }
      return this.messageConfig.kitGivenToAll.with("kit", kitName);
    }

    Player target = Bukkit.getPlayer(player);
    if (target == null) {
      return this.messageConfig.playerNotFound.with("player", player);
    }

    return this.kitService.giveKitForce(kitName, target);
  }

  @Permission("dream-kits.random")
  @Completion(arg = "player", value = "@allplayers")
  @Executor(path = "random", description = "Odbierz losowy zestaw.")
  BukkitNotice random(CommandSender sender, @Arg Player player) {

    return this.kitService.giveRandomKit(sender, player);
  }


  @Permission("dream-kits.edit")
  @Completion(arg = "player", value = "@allplayers")
  @Completion(arg = "kitName", value = "@allkits")
  @Executor(path = "edit", description = "Edytuj wybrany kit.")
  BukkitNotice edit(Player player, @Arg String kitName) {

    return this.kitService.edit(kitName, player);
  }


  @Permission("dream-kits.resetcooldown")
  @Completion(arg = "player", value = "@allplayers")
  @Completion(arg = "kitName", value = "@allkits")
  @Executor(path = "resetcooldown", description = "Zresetuj cooldown kitu.")
  BukkitNotice resetcooldown(@Arg String player, @Arg String kitName) {

    return this.kitService.resetCooldown(player, kitName);
  }

  @Permission("dream-kits.remove")
  @Completion(arg = "kitName", value = "@allkits")
  @Executor(path = "remove", description = "Usuń kit.")
  BukkitNotice remove(Player player, @Arg String kitName) {
    if (!this.kitConfig.kits.containsKey(kitName)) {
      return this.messageConfig.kitNotFound.with("kit", kitName);
    }
    this.kitConfig.kits.remove(kitName);
    return this.messageConfig.kitRemoved.with("kit", kitName);
  }


  @Async
  @Permission("dream-template.reload")
  @Executor(path = "reload", description = "Przeladowuje konfiguracje.")
  BukkitNotice reload() {
    final long time = System.currentTimeMillis();

    try {
      this.messageConfig.load();
      this.pluginConfig.load();
      this.kitConfig.load();
      this.menuConfig.load();

      return this.messageConfig.reloaded.with("time",
          TimeUtil.format(System.currentTimeMillis() - time));
    } catch (NullPointerException | OkaeriException e) {
      e.printStackTrace();

      return this.messageConfig.reloadError.with("error", e.getMessage());
    }
  }
}
