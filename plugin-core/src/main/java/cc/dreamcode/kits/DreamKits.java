package cc.dreamcode.kits;

import cc.dreamcode.command.bukkit.BukkitCommandProvider;
import cc.dreamcode.kits.command.AkitCommand;
import cc.dreamcode.kits.config.KitConfig;
import cc.dreamcode.kits.listener.KitEditChatListener;
import cc.dreamcode.kits.menu.KitMenuSetup;
import cc.dreamcode.kits.menu.MenuConfig;
import cc.dreamcode.kits.service.KitService;
import cc.dreamcode.kits.suggestion.KitSuggestionSupplier;
import cc.dreamcode.menu.bukkit.BukkitMenuProvider;
import cc.dreamcode.menu.serializer.MenuBuilderSerializer;
import cc.dreamcode.notice.serializer.BukkitNoticeSerializer;
import cc.dreamcode.platform.DreamVersion;
import cc.dreamcode.platform.bukkit.DreamBukkitConfig;
import cc.dreamcode.platform.bukkit.DreamBukkitPlatform;
import cc.dreamcode.platform.bukkit.component.ConfigurationResolver;
import cc.dreamcode.platform.bukkit.serializer.ItemMetaSerializer;
import cc.dreamcode.platform.component.ComponentService;
import cc.dreamcode.platform.other.component.DreamCommandExtension;
import cc.dreamcode.platform.persistence.DreamPersistence;
import cc.dreamcode.platform.persistence.component.DocumentPersistenceResolver;
import cc.dreamcode.platform.persistence.component.DocumentRepositoryResolver;
import cc.dreamcode.kits.command.KitCommand;
import cc.dreamcode.kits.command.handler.InvalidInputHandlerImpl;
import cc.dreamcode.kits.command.handler.InvalidPermissionHandlerImpl;
import cc.dreamcode.kits.command.handler.InvalidSenderHandlerImpl;
import cc.dreamcode.kits.command.handler.InvalidUsageHandlerImpl;
import cc.dreamcode.kits.command.result.BukkitNoticeResolver;
import cc.dreamcode.kits.config.MessageConfig;
import cc.dreamcode.kits.config.PluginConfig;
import cc.dreamcode.kits.profile.ProfileRepository;
import cc.dreamcode.utilities.adventure.AdventureProcessor;
import cc.dreamcode.utilities.adventure.AdventureUtil;
import cc.dreamcode.utilities.bukkit.StringColorUtil;
import eu.okaeri.configs.serdes.OkaeriSerdesPack;
import eu.okaeri.configs.serdes.commons.serializer.InstantSerializer;
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit;
import eu.okaeri.configs.yaml.bukkit.serdes.itemstack.ItemStackFailsafe;
import eu.okaeri.configs.yaml.bukkit.serdes.serializer.ItemStackSerializer;
import eu.okaeri.persistence.document.DocumentPersistence;
import eu.okaeri.tasker.bukkit.BukkitTasker;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Instant;

public final class DreamKits extends DreamBukkitPlatform implements DreamBukkitConfig, DreamPersistence {

    @Getter private static DreamKits instance;

    @Override
    public void load(@NonNull ComponentService componentService) {
        instance = this;

        AdventureUtil.setRgbSupport(true);
        StringColorUtil.setColorProcessor(new AdventureProcessor());
    }

    @Override
    public void enable(@NonNull ComponentService componentService) {
        componentService.setDebug(false);

        this.registerInjectable(BukkitTasker.newPool(this));
        this.registerInjectable(BukkitMenuProvider.create(this));

        this.registerInjectable(BukkitCommandProvider.create(this));
        componentService.registerExtension(DreamCommandExtension.class);


        componentService.registerResolver(ConfigurationResolver.class);
        componentService.registerComponent(MessageConfig.class);

        componentService.registerComponent(BukkitNoticeResolver.class);
        componentService.registerComponent(InvalidInputHandlerImpl.class);
        componentService.registerComponent(InvalidPermissionHandlerImpl.class);
        componentService.registerComponent(InvalidSenderHandlerImpl.class);
        componentService.registerComponent(InvalidUsageHandlerImpl.class);

        componentService.registerComponent(KitConfig.class);
        componentService.registerComponent(MenuConfig.class);

        componentService.registerComponent(PluginConfig.class, pluginConfig -> {
            // register persistence + repositories
            this.registerInjectable(pluginConfig.storageConfig);

            componentService.registerResolver(DocumentPersistenceResolver.class);
            componentService.registerComponent(DocumentPersistence.class);
            componentService.registerResolver(DocumentRepositoryResolver.class);

            // enable additional logs and debug messages
            componentService.setDebug(pluginConfig.debug);
        });

        componentService.registerComponent(ProfileRepository.class);

        componentService.registerComponent(KitSuggestionSupplier.class);

        componentService.registerComponent(KitService.class);

        componentService.registerComponent(KitMenuSetup.class);

        componentService.registerComponent(KitEditChatListener.class);

        componentService.registerComponent(KitCommand.class);
        componentService.registerComponent(AkitCommand.class);

    }

    @Override
    public void disable() {
        // features need to be call when server is stopping
    }

    @Override
    public @NonNull DreamVersion getDreamVersion() {
        return DreamVersion.create("Dream-Template", "1.0-InDEV", "author");
    }

    @Override
    public @NonNull OkaeriSerdesPack getConfigSerdesPack() {
        return registry -> {
            registry.register(new BukkitNoticeSerializer());
            registry.register(new MenuBuilderSerializer());

            registry.registerExclusive(ItemStack.class, new ItemStackSerializer(ItemStackFailsafe.BUKKIT));
            registry.registerExclusive(ItemMeta.class, new ItemMetaSerializer());
            registry.registerExclusive(Instant.class, new InstantSerializer(false));
        };
    }

    @Override
    public @NonNull OkaeriSerdesPack getPersistenceSerdesPack() {
        return registry -> {
            registry.register(new SerdesBukkit());

            registry.registerExclusive(ItemStack.class, new ItemStackSerializer(ItemStackFailsafe.BASE64));
            registry.registerExclusive(ItemMeta.class, new ItemMetaSerializer());
            registry.registerExclusive(Instant.class, new InstantSerializer(false));
        };
    }

}
