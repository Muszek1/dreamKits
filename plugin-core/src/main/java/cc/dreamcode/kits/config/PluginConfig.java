package cc.dreamcode.kits.config;

import cc.dreamcode.platform.bukkit.component.configuration.Configuration;
import cc.dreamcode.platform.persistence.StorageConfig;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Header;
import java.util.Arrays;
import java.util.List;

@Configuration(child = "config.yml")
@Header("## Dream-Kits (Main-Config) ##")
public class PluginConfig extends OkaeriConfig {

  @Comment
  @Comment("Debug pokazuje dodatkowe informacje do konsoli. Lepiej wylaczyc. :P")
  @CustomKey("debug")
  public boolean debug = true;

  @Comment
  @Comment("Ponizej znajduja sie dane do logowania bazy danych:")
  @CustomKey("storage-config")
  public StorageConfig storageConfig = new StorageConfig("dreamkits");

  @Comment
  @Comment("Czy zablokować możliwość otrzymania kitu, gdy gracz ma pełny ekwipunek? Pozostałe itemy zostaną upuszczone.")
  @CustomKey("prevent-reveive-full-inventory")
  public boolean preventReceiveIfFullInventory = false;

  @Comment("Lista nazw zestawów, które mogą zostać wylosowane w komendzie /kit losujzestaw")
  public List<String> randomKits = Arrays.asList("vip", "gracz", "jedzenie");

  @Comment("Materiał przycisku powrotu w podglądzie (np. DARK_OAK_DOOR, BARRIER, ARROW)")
  public String previewBackMaterial = "DARK_OAK_DOOR";

  @Comment("Nazwa przycisku powrotu w podglądzie zestawu")
  public String previewBackName = "&c&lWróć do menu";

  @Comment("Opis (lore) przycisku powrotu w podglądzie zestawu")
  public List<String> previewBackLore = Arrays.asList("&7Kliknij, aby wrócić do",
      "&7listy zestawów.");
}
