package cc.dreamcode.kits.service;

import cc.dreamcode.kits.config.KitConfig;
import cc.dreamcode.kits.config.MessageConfig;
import cc.dreamcode.kits.config.PluginConfig;
import cc.dreamcode.kits.kit.KitEntry;
import cc.dreamcode.kits.profile.Profile;
import cc.dreamcode.kits.profile.ProfileRepository;
import cc.dreamcode.menu.bukkit.base.BukkitMenu;
import cc.dreamcode.notice.bukkit.BukkitNotice;
import cc.dreamcode.utilities.TimeUtil;
import cc.dreamcode.utilities.bukkit.InventoryUtil;
import cc.dreamcode.utilities.bukkit.builder.ItemBuilder;
import com.cryptomorin.xseries.XMaterial;
import eu.okaeri.injector.annotation.Inject;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class KitService {

  public final Map<UUID, String[]> activeChatEdits = new ConcurrentHashMap<>();
  private final KitConfig kitConfig;
  private final PluginConfig pluginConfig;
  private final ProfileRepository profileRepository;
  private final MessageConfig messageConfig;

  public BukkitNotice createKit(Player player, String name, int limit, String permission,
      Duration cooldown, int slot, ItemStack icon, String rank) {
    List<ItemStack> inventoryItems = Arrays.stream(player.getInventory().getContents())
        .filter(item -> item != null && item.getType() != org.bukkit.Material.AIR)
        .map(ItemStack::clone).collect(Collectors.toList());

    if (inventoryItems.isEmpty()) {
      return this.messageConfig.kitCannotBeEmpty;
    }

    KitEntry kitEntry = new KitEntry();
    kitEntry.items = inventoryItems;
    kitEntry.icon = icon.clone();
    kitEntry.cooldown = cooldown;
    kitEntry.slot = slot;
    kitEntry.permission = permission;
    kitEntry.limit = limit;
    kitEntry.rank = rank;

    this.kitConfig.kits.put(name, kitEntry);
    this.kitConfig.save();

    return this.messageConfig.kitCreated.with("name", name);
  }


  public BukkitNotice giveKit(String kitName, Player player) {

    KitEntry kit = this.kitConfig.kits.get(kitName);
    if (kit == null) {
      return this.messageConfig.kitNotFound;
    }

    Profile profile = this.profileRepository.findOrCreate(player.getUniqueId(), player.getName());

    if (!profile.canClaim(kitName, kit.cooldown, kit.limit) && !player.hasPermission(
        "dream-kits.bypass")) {

      if (kit.limit > 0 && profile.getKitUsage(kitName) >= kit.limit) {
        return this.messageConfig.kitLimitReached.with("limit", kit.limit);
      }

      long remainingTime = profile.getRemainingCooldown(kitName, kit.cooldown);
      String formattedTime = TimeUtil.format(remainingTime);

      return this.messageConfig.kitOnCooldown.with("cooldown", formattedTime);
    }

    if (this.pluginConfig.preventReceiveIfFullInventory) {
      Inventory tempInv = Bukkit.createInventory(null, 36);

      for (int i = 0; i < 36; i++) {
        ItemStack item = player.getInventory().getItem(i);
        if (item != null) {
          tempInv.setItem(i, item.clone());
        }
      }

      if (!tempInv.addItem(kit.items.toArray(new ItemStack[0])).isEmpty()) {
        return this.messageConfig.inventoryFull;
      }

    }

    InventoryUtil.giveItems(player, kit.items);

    long now = System.currentTimeMillis();
    profile.setCooldown(kitName, now);
    profile.incrementKitUsage(kitName);
    profile.save();

    return this.messageConfig.kitReceieved.with("name", kitName);

  }

  public BukkitNotice giveKitForce(String kitName, Player player) {

    KitEntry kit = this.kitConfig.kits.get(kitName);
    if (kit == null) {
      return this.messageConfig.kitNotFound;
    }

    InventoryUtil.giveItems(player, kit.items);

    return this.messageConfig.kitReceieved.with("name", kitName);
  }

  public BukkitNotice giveRandomKit(CommandSender sender, Player player) {
    List<String> availableKits = this.pluginConfig.randomKits;

    if (availableKits == null || availableKits.isEmpty()) {
      return this.messageConfig.emptyRandomKit;
    }

    int randomIndex = (int) (Math.random() * availableKits.size());
    String randomKitName = availableKits.get(randomIndex);
    BukkitNotice receiverNotice = this.giveKit(randomKitName, player);
    if (receiverNotice != null) {
      receiverNotice.send(player);
    }
    return this.messageConfig.randomKitGivenToPlayer.with("player", player.getName())
        .with("kit", randomKitName);
  }

  public BukkitNotice resetCooldown(String player, String kitName) {
    Profile profile = this.profileRepository.findAll().stream()
        .filter(p -> p.getName() != null && p.getName().equalsIgnoreCase(player)).findFirst()
        .orElse(null);

    if (profile == null) {
      return this.messageConfig.playerNotFound;
    }
    profile.setCooldown(kitName, 0L);
    profile.save();

    Player targetPlayer = Bukkit.getPlayer(player);
    if (targetPlayer != null) {
      this.messageConfig.resetCooldownNotification.with("kit", kitName).send(targetPlayer);
    }
    return this.messageConfig.resetCooldown.with("player", profile.getName()).with("kit", kitName);
  }

  public void promptForChatInput(Player player, String kitName, String editType) {
    player.closeInventory();

    this.activeChatEdits.put(player.getUniqueId(), new String[]{kitName, editType});

    player.sendMessage("&eWpisz nową wartość na czacie.");
    switch (editType) {
      case "COOLDOWN":
        player.sendMessage("&7Podaj czas w sekundach (np. 3600 dla 1h).");
        break;
      case "SLOT":
        player.sendMessage("&7Podaj numer slotu (0-53).");
        break;
      case "RANK":
        player.sendMessage("&7Podaj nową nazwę rangi (możesz używać kolorów np. &cVIP).");
        break;
    }
    player.sendMessage("&cWpisz 'anuluj', aby przerwać edycję.");
  }


  public BukkitNotice edit(String kitName, Player player) {
    KitEntry kit = this.kitConfig.kits.get(kitName);
    if (kit == null) {
      return this.messageConfig.kitNotFound;
    }

    BukkitMenu mainMenu = new BukkitMenu("Edytuj: " + kitName, 3, 0);

    mainMenu.setCancelInventoryClick(true);
    mainMenu.setDisposeWhenClose(true);

    ItemStack iconBtn = ItemBuilder.of(Material.ITEM_FRAME).setName("&eZmiana ikonki")
        .setLore("&7Kliknij, aby zmienić ikonę zestawu w GUI.").fixColors().toItemStack();
    mainMenu.setItem(10, iconBtn, event -> openIconEditor(kitName, player));

    ItemStack slotBtn = ItemBuilder.of(Material.COMPASS).setName("&eZmiana slotu")
        .setLore("&7Obecny slot: &a" + kit.slot, "&7Kliknij, aby wpisać nowy na czacie.")
        .fixColors().toItemStack();
    mainMenu.setItem(11, slotBtn, event -> promptForChatInput(player, kitName, "SLOT"));
    ItemStack rankBtn = ItemBuilder.of(Material.NAME_TAG)
        .setName("&eZmiana rangi (Wygląd)")
        .setLore("&7Obecna ranga: &r" + (kit.rank != null ? kit.rank.replace("&", "§") : "&cBrak"),
            "&7Kliknij, aby wpisać nową nazwę na czacie.")
        .fixColors()
        .toItemStack();
    mainMenu.setItem(13, rankBtn, event -> promptForChatInput(player, kitName, "RANK"));
    String formattedCooldown = TimeUtil.format(kit.cooldown.toMillis());
    ItemStack cdBtn = ItemBuilder.of(XMaterial.CLOCK.parseItem().getType())
        .setName("&eZmiana cooldownu").setLore(
            "&7Obecny czas: &a" + formattedCooldown + " &8(" + kit.cooldown.getSeconds() + "s)", "",
            "&7Kliknij, aby wpisać nowy cooldown na czacie.").fixColors().toItemStack();
    mainMenu.setItem(15, cdBtn, event -> promptForChatInput(player, kitName, "COOLDOWN"));

    ItemStack itemsBtn = ItemBuilder.of(Material.CHEST).setName("&eZmiana przedmiotów")
        .setLore("&7Kliknij, aby edytować zawartość zestawu.").fixColors().toItemStack();
    mainMenu.setItem(16, itemsBtn, event -> openItemsEditor(kitName, player));

    mainMenu.open(player);
    return null;
  }

  public void openIconEditor(String kitName, Player player) {
    KitEntry kit = this.kitConfig.kits.get(kitName);
    if (kit == null) {
      return;
    }

    BukkitMenu menu = new BukkitMenu("Zmień Ikonę: " + kitName, 3, 0);

    menu.setCancelInventoryClick(false);
    menu.setDisposeWhenClose(true);

    ItemStack background = ItemBuilder.of(XMaterial.BLACK_STAINED_GLASS_PANE.parseItem().getType())
        .setName(" ").toItemStack();

    for (int i = 0; i < 27; i++) {
      if (i == 13 || i == 26) {
        continue;
      }

      menu.setItem(i, background, event -> {
        event.setCancelled(true);
      });
    }

    if (kit.icon != null && kit.icon.getType() != Material.AIR) {
      menu.getInventory().setItem(13, kit.icon.clone());
    }

    ItemStack saveBtn = ItemBuilder.of(XMaterial.LIME_STAINED_GLASS_PANE.parseItem().getType())
        .setName("&a&lZAPISZ IKONĘ")
        .setLore("&7Włóż przedmiot na środek", "&7i kliknij tutaj, aby zapisać.").fixColors()
        .toItemStack();

    menu.setItem(26, saveBtn, event -> {
      event.setCancelled(true);

      ItemStack newIcon = menu.getInventory().getItem(13);

      if (newIcon == null || newIcon.getType() == Material.AIR) {
        player.sendMessage("&cMusisz włożyć jakiś przedmiot na środek!");
        return;
      }

      kit.icon = newIcon.clone();
      this.kitConfig.save();

      player.sendMessage("&aIkonka zestawu zaktualizowana!");

      player.getInventory().addItem(newIcon);
      menu.getInventory().setItem(13, new ItemStack(Material.AIR));

      edit(kitName, player);
    });

    menu.setInventoryCloseEvent(event -> {
      ItemStack itemLeft = event.getInventory().getItem(13);
      if (itemLeft != null && itemLeft.getType() != Material.AIR) {
        player.getInventory().addItem(itemLeft);
      }
    });

    menu.open(player);
  }

  public void openItemsEditor(String kitName, Player player) {
    KitEntry kit = this.kitConfig.kits.get(kitName);
    BukkitMenu menu = new BukkitMenu("Itemy: " + kitName, 6, 0);

    menu.setCancelInventoryClick(false);
    menu.setDisposeWhenClose(true);

    for (int i = 0; i < kit.items.size(); i++) {
      if (i >= 53) {
        break;
      }
      menu.getInventory().setItem(i, kit.items.get(i).clone());
    }

    ItemStack saveBtn = ItemBuilder.of(XMaterial.GREEN_STAINED_GLASS_PANE.parseItem().getType())
        .setName("&a&lZAPISZ I WRÓĆ")
        .setLore("&7Kliknij, aby zapisać przedmioty", "&7i wrócić do menu głównego.").fixColors()
        .toItemStack();

    menu.setItem(53, saveBtn, event -> {
      event.setCancelled(true);

      java.util.List<ItemStack> newItems = new java.util.ArrayList<>();
      for (int i = 0; i < 53; i++) {
        ItemStack item = menu.getInventory().getItem(i);
        if (item != null && item.getType() != Material.AIR) {
          newItems.add(item.clone());
        }
      }

      kit.items = newItems;
      this.kitConfig.save();

      player.sendMessage("&aZapisano przedmioty zestawu!");

      edit(kitName, player);
    });

    menu.open(player);
  }

}
