package cc.dreamcode.kits.profile;

import eu.okaeri.configs.annotation.CustomKey;
import eu.okaeri.persistence.document.Document;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Profile extends Document {

  @CustomKey("cooldowns")
  public Map<String, Long> cooldowns = new HashMap<>();
  @CustomKey("kitUsage")
  public Map<String, Integer> kitUsage = new HashMap<>();
  @CustomKey("name")
  private String name;

  public long getCooldown(String kitName) {
    return this.cooldowns.getOrDefault(kitName, 0L);
  }

  public void setCooldown(String kitName, long time) {
    this.cooldowns.put(kitName, time);
  }

  public UUID getUniqueId() {
    return this.getPath().toUUID();
  }


  public int getKitUsage(String kitName) {
    return this.kitUsage.getOrDefault(kitName, 0);
  }

  public void incrementKitUsage(String kitName) {
    int usage = this.getKitUsage(kitName);
    this.kitUsage.put(kitName, usage + 1);
  }

  public boolean canClaim(String kitName, Duration cooldown, int limit) {

    if (limit > 0 && this.getKitUsage(kitName) >= limit) {
      return false;
    }

    long lastUse = this.getCooldown(kitName);
    long readyAt = lastUse + cooldown.toMillis();
    long now = System.currentTimeMillis();

    return now >= readyAt;
  }

  public long getRemainingCooldown(String kitName, Duration cooldown) {
    long lastUse = this.getCooldown(kitName);
    long readyAt = lastUse + cooldown.toMillis();
    long now = System.currentTimeMillis();

    return Math.max(0, readyAt - now);
  }
}
