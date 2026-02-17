package cc.dreamcode.kits.config;

import cc.dreamcode.kits.kit.KitEntry;
import cc.dreamcode.platform.bukkit.component.configuration.Configuration;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Header;
import java.util.HashMap;
import java.util.Map;

@Configuration(child = "kits.yml")
@Header("## Dream-Kits (Kit-Config) ##")
public class KitConfig extends OkaeriConfig {

  @Comment("Stworzone kity:")
  @CustomKey("kits")
  public Map<String, KitEntry> kits = new HashMap<>();


}
