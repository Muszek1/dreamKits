package cc.dreamcode.kits.command;

import cc.dreamcode.command.CommandBase;
import cc.dreamcode.command.annotation.Arg;
import cc.dreamcode.command.annotation.Command;
import cc.dreamcode.command.annotation.Completion;
import cc.dreamcode.command.annotation.Executor;
import cc.dreamcode.command.annotation.Permission;
import cc.dreamcode.kits.menu.KitMenuSetup;
import cc.dreamcode.kits.service.KitService;
import cc.dreamcode.notice.bukkit.BukkitNotice;
import eu.okaeri.injector.annotation.Inject;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@Command(name = "kit")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class KitCommand implements CommandBase {

  private final KitMenuSetup kitMenuSetup;
  private final KitService kitService;

  @Permission("dream-kits.claim")
  @Completion(arg = "kitName", value = "@allkits")
  @Executor(path = "claim", description = "Odbierz swoj zestaw.")
  BukkitNotice claim(Player player, @Arg String kitName) {

    return this.kitService.giveKit(kitName, player);
  }


  @Permission("dream-kits.preview")
  @Completion(arg = "player", value = "@allplayers")
  @Completion(arg = "kitName", value = "@allkits")
  @Executor(path = "preview", description = "Zobacz itemy w kicie.")
  public void preview(Player player, @Arg String kitName) {

    this.kitMenuSetup.openPreview(kitName, player);
  }

  @Permission("dream-kits.menu")
  @Executor(path = "", description = "Zobacz menu kitów.")
  public void openMenu(Player player) {

    this.kitMenuSetup.build(player).open(player);
  }


}
