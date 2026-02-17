package cc.dreamcode.kits.profile;

import eu.okaeri.persistence.repository.DocumentRepository;
import eu.okaeri.persistence.repository.annotation.DocumentCollection;
import java.util.UUID;
import lombok.NonNull;

@DocumentCollection(path = "profiles", keyLength = 36)
public interface ProfileRepository extends DocumentRepository<UUID, Profile> {

  default Profile findOrCreate(@NonNull UUID uuid, String profileName) {

    Profile profile = this.findOrCreateByPath(uuid);
    if (profileName != null) {
      profile.setName(profileName);
    }

    return profile;
  }

}
