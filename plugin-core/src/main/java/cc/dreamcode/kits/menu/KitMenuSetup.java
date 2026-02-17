package cc.dreamcode.kits.menu;

import cc.dreamcode.kits.config.KitConfig;
import cc.dreamcode.kits.config.MessageConfig;
import cc.dreamcode.kits.config.PluginConfig;
import cc.dreamcode.kits.kit.KitEntry;
import cc.dreamcode.kits.profile.Profile;
import cc.dreamcode.kits.profile.ProfileRepository;
import cc.dreamcode.kits.service.KitService;
import cc.dreamcode.menu.bukkit.base.BukkitMenu;
import cc.dreamcode.menu.bukkit.setup.BukkitMenuPlayerSetup;
import cc.dreamcode.notice.bukkit.BukkitNotice;
import cc.dreamcode.utilities.bukkit.builder.ItemBuilder;
import eu.okaeri.injector.annotation.Inject;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class KitMenuSetup implements BukkitMenuPlayerSetup {

  private final MenuConfig menuConfig;
  private final KitConfig kitConfig;
  private final MessageConfig messageConfig;
  private final PluginConfig pluginConfig;
  private final ProfileRepository profileRepository;
  private final KitService kitService;

  @Override
  public BukkitMenu build(@NotNull HumanEntity humanEntity) {
    Player player = (Player) humanEntity;

    BukkitMenu menu = this.menuConfig.kitsMenu.buildWithItems();

    Profile profile = this.profileRepository.findOrCreate(player.getUniqueId(), player.getName());

    if (this.menuConfig.closeMenuSlot != -1) {

      ItemStack fixedColorItem = ItemBuilder.of(this.menuConfig.closeMenuItem.clone()).fixColors()
          .toItemStack();

      menu.setItem(this.menuConfig.closeMenuSlot, fixedColorItem, event -> {
        player.closeInventory();
      });
    }

    this.kitConfig.kits.forEach((kitName, kitEntry) -> {

      ItemStack baseIcon = kitEntry.icon != null ? kitEntry.icon.clone()
          : (!kitEntry.items.isEmpty() ? kitEntry.items.get(0).clone()
              : new ItemStack(org.bukkit.Material.BARRIER));

      boolean canClaim =
          profile.canClaim(kitName, kitEntry.cooldown, kitEntry.limit) || player.hasPermission(
              "dream-kits.bypass");

      String displayName = this.messageConfig.kitIconName.replace("{name}", kitName);
      String displayRank =
          (kitEntry.rank != null && !kitEntry.rank.isEmpty()) ? kitEntry.rank : "&7Wszyscy";
      ItemBuilder iconBuilder = ItemBuilder.of(baseIcon).setName(displayName);

      if (canClaim) {


        List<String> availableLore = this.messageConfig.kitIconAvailableLore.stream()
            .map(line -> line.replace("{name}", kitName).replace("{rank}", displayRank))
            .collect(Collectors.toList());

        iconBuilder.setLore(availableLore);

      } else {

        String remainingTime = cc.dreamcode.utilities.TimeUtil.format(
            profile.getRemainingCooldown(kitName, kitEntry.cooldown));

        List<String> cooldownLore = this.messageConfig.kitIconCooldownLore.stream()
            .map(line -> line.replace("{name}", kitName).replace("{time}", remainingTime).replace("{rank}", displayRank))
            .collect(Collectors.toList());

        iconBuilder.setLore(cooldownLore);
      }

      iconBuilder.fixColors();

      ItemStack finalIcon = iconBuilder.toItemStack();

      menu.setItem(kitEntry.slot, finalIcon, event -> {
        if (event.getClick().isLeftClick()) {
          event.setCancelled(true);

          BukkitNotice notice = this.kitService.giveKit(kitName, player);
          if (notice != null) {
            notice.send(player);
          }
          player.closeInventory();
        } else if (event.getClick().isRightClick()) {
          event.setCancelled(true);

          openPreview(kitName, player);
        }

      });

    });
    return menu;
  }

  public void openPreview(String kitName, Player player) {
    KitEntry kit = this.kitConfig.kits.get(kitName);
    if (kit == null) {
      return;
    }

    BukkitMenu previewMenu = new BukkitMenu(kitName, 6, 0);

    previewMenu.setCancelInventoryClick(true);
    previewMenu.setDisposeWhenClose(true);

    for (int i = 0; i < kit.items.size(); i++) {
      if (i >= 54) {
        break;
      }
      previewMenu.getInventory().setItem(i, kit.items.get(i).clone());
    }
    Material backMaterial = Material.matchMaterial(
        this.pluginConfig.previewBackMaterial.toUpperCase());
    if (backMaterial == null) {
      backMaterial = Material.DARK_OAK_DOOR;
    }

    ItemStack backBtn = ItemBuilder.of(backMaterial).setName(this.pluginConfig.previewBackName)
        .setLore(this.pluginConfig.previewBackLore).fixColors().toItemStack();

    previewMenu.setItem(53, backBtn, event -> {
      this.build(player).open(player);
    });
    previewMenu.open(player);
  }
}