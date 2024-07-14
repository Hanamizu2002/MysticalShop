package com.mysticalkingdoms.mysticalshop;

import com.mysticalkingdoms.mysticalshop.commands.MysticalShopCommands;
import com.mysticalkingdoms.mysticalshop.economy.EconomyManager;
import com.mysticalkingdoms.mysticalshop.locale.LocaleManager;
import com.mysticalkingdoms.mysticalshop.shop.Shop;
import com.mysticalkingdoms.mysticalshop.shop.ShopManager;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.exception.CommandErrorException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class MysticalShop extends JavaPlugin {

    private BukkitAudiences adventure;
    private BukkitCommandHandler commandManager;
    private EconomyManager economyManager;
    private LocaleManager localeManager;

    private ShopManager shopManager;

    @Override
    public void onEnable() {
        this.adventure = BukkitAudiences.create(this);
        this.commandManager = BukkitCommandHandler.create(this);
        this.economyManager = new EconomyManager(this);
        this.localeManager = new LocaleManager(this);

        commandManager.registerValueResolver(Shop.class, arguments -> {
            String value = arguments.pop();
            Shop shop = shopManager.getShop(value);
            if (shop == null) {
                throw new CommandErrorException(localeManager.getMessage("messages.invalidShop").toString());
            }

            return shop;
        });

        commandManager.getAutoCompleter().registerParameterSuggestions(Shop.class, (args, sender, command) -> shopManager.getShopNames());

        this.shopManager = new ShopManager(this);
        commandManager.register(new MysticalShopCommands(this));
    }

    @Override
    public void onDisable() {
        shopManager.unload();
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public BukkitAudiences getAdventure() {
        return adventure;
    }

    public LocaleManager getLocaleManager() {
        return localeManager;
    }

    public BukkitCommandHandler getCommandManager() {
        return commandManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    /**
     * Create a configuration file that does NOT update.
     * @param file File to create.
     * @return The configuration file created.
     */
    public YamlDocument createConfig(File file) {
        try {
            return YamlDocument.create(file, getResource(file.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Create a configuration file that automatically updates. Requires config-version to be defined.
     * @param file File to create.
     * @return The configuration file created.
     */
    public YamlDocument createUpdatingConfig(File file) {
        try {
            return YamlDocument.create(file, getResource(file.getName()),
                    GeneralSettings.DEFAULT,
                    LoaderSettings.builder().setAutoUpdate(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
