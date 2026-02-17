package cc.dreamcode.kits.kit;

import eu.okaeri.configs.OkaeriConfig;
import java.time.Duration;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class KitEntry extends OkaeriConfig {

  public List<ItemStack> items;
  public Duration cooldown;
  public int slot;
  public ItemStack icon;
  public int limit;
  public String rank;
  public String permission;

}
