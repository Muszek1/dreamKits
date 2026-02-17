package cc.dreamcode.kits.listener;

import cc.dreamcode.kits.config.KitConfig;
import cc.dreamcode.kits.kit.KitEntry;
import cc.dreamcode.kits.service.KitService;
import eu.okaeri.injector.annotation.Inject;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class KitEditChatListener implements Listener {

  private final KitService kitService;
  private final KitConfig kitConfig;
  private final Plugin plugin;

  @EventHandler
  public void onChat(AsyncPlayerChatEvent event) {
    Player player = event.getPlayer();

    String[] editData = this.kitService.activeChatEdits.get(player.getUniqueId());
    if (editData == null) {
      return;
    }

    event.setCancelled(true);

    String kitName = editData[0];
    String editType = editData[1];
    String input = event.getMessage();

    if (input.equalsIgnoreCase("anuluj")) {
      this.kitService.activeChatEdits.remove(player.getUniqueId());
      player.sendMessage("&cAnulowano edycję.");
      openMenuSync(player, kitName);
      return;
    }

    KitEntry kit = this.kitConfig.kits.get(kitName);
    if (kit == null) {
      return;
    }

    try {
      switch (editType) {
        case "SLOT":
          int newSlot = Integer.parseInt(input);
          if (newSlot < 0 || newSlot > 53) {
            throw new NumberFormatException();
          }

          kit.slot = newSlot;
          player.sendMessage("&aPomyślnie zmieniono slot na: &f" + newSlot);

          break;
        case "COOLDOWN":
          long seconds = Long.parseLong(input);
          kit.cooldown = Duration.ofSeconds(seconds);
          player.sendMessage("&aPomyślnie zmieniono cooldown na: &f" + seconds + "s");
          break;
        case "RANK":
          kit.rank = input;
          player.sendMessage("&aPomyślnie zmieniono nazwę rangi na: &r" + input.replace("&", "§"));
          break;
      }

      this.kitConfig.save();

    } catch (NumberFormatException e) {
      player.sendMessage(
          "&cPodano nieprawidłową wartość (to musi być liczba)! Spróbuj ponownie lub wpisz 'anuluj'.");
      return;
    }

    this.kitService.activeChatEdits.remove(player.getUniqueId());
    openMenuSync(player, kitName);
  }

  private void openMenuSync(Player player, String kitName) {
    Bukkit.getScheduler().runTask(this.plugin, () -> {
      this.kitService.edit(kitName, player);
    });
  }
}