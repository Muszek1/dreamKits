package cc.dreamcode.kits.menu;

import cc.dreamcode.menu.bukkit.BukkitMenuBuilder;
import cc.dreamcode.platform.bukkit.component.configuration.Configuration;
import cc.dreamcode.utilities.builder.MapBuilder;
import cc.dreamcode.utilities.bukkit.builder.ItemBuilder;
import com.cryptomorin.xseries.XMaterial;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.NameModifier;
import eu.okaeri.configs.annotation.NameStrategy;
import eu.okaeri.configs.annotation.Names;
import org.bukkit.inventory.ItemStack;

@Configuration(child = "menu.yml")
@Header("## Dream-Kits (Menu-Config) ##")
@Names(strategy = NameStrategy.HYPHEN_CASE, modifier = NameModifier.TO_LOWER_CASE)
public class MenuConfig extends OkaeriConfig {

  @Comment("Wygląd przycisku zamykania menu:")
  public ItemStack closeMenuItem = new ItemBuilder(XMaterial.BARRIER.parseItem().getType()).setName(
      "&cZamknij menu").toItemStack();

  @Comment("Pod ktorym slotem ma sie znajdowac przycisk do zamnkniecia menu?")
  public int closeMenuSlot = 49;

  @Comment("Jak ma wyglądać nazwa menu (tytuł)?")
  public String title = "&6&lKity";

  @Comment("Ustaw konfiguracje menu kitów:")
  public BukkitMenuBuilder kitsMenu = new BukkitMenuBuilder(title, 6,
      new MapBuilder<Integer, ItemStack>().put(0,
          new ItemBuilder(XMaterial.MAGENTA_STAINED_GLASS_PANE.parseItem().getType()).setName(" ")
              .toItemStack()).put(1,
          new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem().getType()).setName(" ")
              .toItemStack()).put(2,
          new ItemBuilder(XMaterial.MAGENTA_STAINED_GLASS_PANE.parseItem().getType()).setName(" ")
              .toItemStack()).put(3,
          new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem().getType()).setName(" ")
              .toItemStack()).put(4,
          new ItemBuilder(XMaterial.MAGENTA_STAINED_GLASS_PANE.parseItem().getType()).setName(" ")
              .toItemStack()).put(5,
          new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem().getType()).setName(" ")
              .toItemStack()).put(6,
          new ItemBuilder(XMaterial.MAGENTA_STAINED_GLASS_PANE.parseItem().getType()).setName(" ")
              .toItemStack()).put(7,
          new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem().getType()).setName(" ")
              .toItemStack()).put(8,
          new ItemBuilder(XMaterial.MAGENTA_STAINED_GLASS_PANE.parseItem().getType()).setName(" ")
              .toItemStack()).put(45,
          new ItemBuilder(XMaterial.MAGENTA_STAINED_GLASS_PANE.parseItem().getType()).setName(" ")
              .toItemStack()).put(46,
          new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem().getType()).setName(" ")
              .toItemStack()).put(47,
          new ItemBuilder(XMaterial.MAGENTA_STAINED_GLASS_PANE.parseItem().getType()).setName(" ")
              .toItemStack()).put(48,
          new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem().getType()).setName(" ")
              .toItemStack()).put(50,
          new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem().getType()).setName(" ")
              .toItemStack()).put(51,
          new ItemBuilder(XMaterial.MAGENTA_STAINED_GLASS_PANE.parseItem().getType()).setName(" ")
              .toItemStack()).put(52,
          new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseItem().getType()).setName(" ")
              .toItemStack()).put(53,
          new ItemBuilder(XMaterial.MAGENTA_STAINED_GLASS_PANE.parseItem().getType()).setName(" ")
              .toItemStack()).build());
}