package cc.dreamcode.kits.suggestion;

import cc.dreamcode.command.suggestion.supplier.SuggestionSupplier;
import cc.dreamcode.kits.config.KitConfig;
import cc.dreamcode.platform.other.component.annotation.SuggestionKey;
import eu.okaeri.injector.annotation.Inject;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@SuggestionKey("@allkits")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class KitSuggestionSupplier implements SuggestionSupplier {

  private final KitConfig kitConfig;

  @Override
  public List<String> supply(@NonNull Class<?> paramType) {
    return new ArrayList<>(this.kitConfig.kits.keySet());
  }
}
