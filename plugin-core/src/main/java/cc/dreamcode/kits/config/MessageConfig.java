package cc.dreamcode.kits.config;

import cc.dreamcode.notice.bukkit.BukkitNotice;
import cc.dreamcode.platform.bukkit.component.configuration.Configuration;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.Headers;
import java.util.Arrays;
import java.util.List;

@Configuration(child = "message.yml")
@Headers({@Header("## Dream-Kits (Message-Config) ##"),
    @Header("Dostepne type: (DO_NOT_SEND, CHAT, ACTION_BAR, SUBTITLE, TITLE, TITLE_SUBTITLE)")})
public class MessageConfig extends OkaeriConfig {

  @CustomKey("command-usage")
  public BukkitNotice usage = BukkitNotice.chat("&7Przyklady uzycia komendy: &c{label}");
  @CustomKey("command-usage-help")
  public BukkitNotice usagePath = BukkitNotice.chat("&f{usage} &8- &7{description}");

  @CustomKey("command-usage-not-found")
  public BukkitNotice usageNotFound = BukkitNotice.chat(
      "&cNie znaleziono pasujacych do kryteriow komendy.");
  @CustomKey("command-path-not-found")
  public BukkitNotice pathNotFound = BukkitNotice.chat(
      "&cTa komenda jest pusta lub nie posiadasz dostepu do niej.");
  @CustomKey("command-no-permission")
  public BukkitNotice noPermission = BukkitNotice.chat("&cNie posiadasz uprawnien.");
  @CustomKey("command-not-player")
  public BukkitNotice notPlayer = BukkitNotice.chat(
      "&cTa komende mozna tylko wykonac z poziomu gracza.");
  @CustomKey("command-not-console")
  public BukkitNotice notConsole = BukkitNotice.chat(
      "&cTa komende mozna tylko wykonac z poziomu konsoli.");
  @CustomKey("command-invalid-format")
  public BukkitNotice invalidFormat = BukkitNotice.chat(
      "&cPodano nieprawidlowy format argumentu komendy. ({input})");

  @CustomKey("player-not-found")
  public BukkitNotice playerNotFound = BukkitNotice.chat("&cPodanego gracza nie znaleziono.");
  @CustomKey("world-not-found")
  public BukkitNotice worldNotFound = BukkitNotice.chat("&cPodanego swiata nie znaleziono.");
  @CustomKey("cannot-do-at-my-self")
  public BukkitNotice cannotDoAtMySelf = BukkitNotice.chat("&cNie mozesz tego zrobic na sobie.");
  @CustomKey("number-is-not-valid")
  public BukkitNotice numberIsNotValid = BukkitNotice.chat("&cPodana liczba nie jest cyfra.");

  @CustomKey("config-reloaded")
  public BukkitNotice reloaded = BukkitNotice.chat("&aPrzeladowano! &7({time})");
  @CustomKey("config-reload-error")
  public BukkitNotice reloadError = BukkitNotice.chat(
      "&cZnaleziono problem w konfiguracji: &6{error}");

  @CustomKey("kit-not-found")
  public BukkitNotice kitNotFound = BukkitNotice.chat("&cNie ma takiego kitu!");
  @CustomKey("kit-recieved")
  public BukkitNotice kitReceieved = BukkitNotice.chat("&cOdebrano kit {name}!");
  @CustomKey("kit-cooldown")
  public BukkitNotice kitOnCooldown = BukkitNotice.chat("&cOdczekaj jeszcze {cooldown}!");
  @CustomKey("kit-inventoryFull")
  public BukkitNotice inventoryFull = BukkitNotice.chat(
      "&cTwój ekwipunek jest pełny! Zrób miejsce, aby odebrać zestaw.");
  @CustomKey("kit-given-to-all")
  public BukkitNotice kitGivenToAll = BukkitNotice.chat(
      "&aKit &6{kit} &azostal rozdany wszystkim graczom!");

  @CustomKey("empty-random-kit")
  public BukkitNotice emptyRandomKit = BukkitNotice.chat(
      "&cNie można wylosować zestawu, ponieważ lista losowych zestawów jest pusta.");

  @CustomKey("reset-cooldown")
  public BukkitNotice resetCooldown = BukkitNotice.chat(
      "&aZresetowano cooldown graczowi {player} dla kitu &6{kit} &a!");
  @CustomKey("reset-cooldown-notification")
  public BukkitNotice resetCooldownNotification = BukkitNotice.chat(
      "&aTwój cooldown dla kitu &6{kit} &azostał zresetowany przez administratora!");
  @CustomKey("kit-removed")
  public BukkitNotice kitRemoved = BukkitNotice.chat("&aKit &6{kit} &azostał usunięty!");

  @CustomKey("kit-limit-reached")
  public BukkitNotice kitLimitReached = BukkitNotice.chat(
      "&cOsiągnąłeś już limit odbioru tego zestawu! (Limit: {limit})");
  @CustomKey("kit-cannot-be-empty")
  public BukkitNotice kitCannotBeEmpty = BukkitNotice.chat(
      "&cKit nie może być pusty! Dodaj do niego przynajmniej jeden przedmiot lub ustaw ikonę.");
  @CustomKey("kit-created")
  public BukkitNotice kitCreated = BukkitNotice.chat(
      "&aKit &6{name} &azostał utworzony lub zaktualizowany!");

  @Comment("Format nazwy ikony w GUI menu kitów")
  public String kitIconName = "&bZestaw: &f{name}";


  @Comment("random-kit-given-to-player")
  public BukkitNotice randomKitGivenToPlayer = BukkitNotice.chat(
      "&aPomyślnie nadano losowy zestaw &7{kit} &agraczowi &f{player}&a!");


  @Comment("Opis (lore) ikony, gdy kit jest gotowy do odbioru")
  public List<String> kitIconAvailableLore = Arrays.asList("&7Status: &aDostępny", "&7Dla kogo: &f{rank}",
      "&8&m------------------", "&7LPM, aby odebrać!", "&7PPM, aby zobaczyć zawartość!");

  @Comment("Opis (lore) ikony, gdy gracz musi poczekać na kit")
  public List<String> kitIconCooldownLore = Arrays.asList("&7Status: &cOczekiwanie", "&7Dla kogo: &f{rank}",
      "&7Dostępny za: &f{time}", "&8&m------------------", "&7PPM, aby zobaczyć zawartość");
}
