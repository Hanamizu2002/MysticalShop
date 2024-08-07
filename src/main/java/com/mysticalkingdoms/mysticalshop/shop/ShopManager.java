package com.mysticalkingdoms.mysticalshop.shop;

import com.google.common.io.ByteStreams;
import com.mysticalkingdoms.mysticalshop.MysticalShop;
import com.mysticalkingdoms.mysticalshop.shop.inventory.ShopInventory;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.DefaultFor;
import revxrsal.commands.orphan.OrphanCommand;
import revxrsal.commands.orphan.Orphans;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ShopManager {

    private final MysticalShop plugin;
    private final Map<String, Shop> activeShops = new HashMap<>();
    private final YamlDocument storage;
    public ShopManager(MysticalShop plugin) {
        this.plugin = plugin;
        this.storage = plugin.createConfig(new File(plugin.getDataFolder(), "storage.yml"));
        reloadItems();

        Bukkit.getScheduler().runTaskTimer(plugin, () -> activeShops.values().forEach(Shop::tick), 20L, 20L);
    }

    public Shop getShop(String name) {
        return activeShops.get(name);
    }

    public Set<String> getShopNames() {
        return activeShops.keySet();
    }

    private void reloadItems() {
        File folder = new File(plugin.getDataFolder(), "shops");
        if (!folder.exists()) {
            folder.mkdir();
            try {
                File newFile = new File(folder, "exampleShop.yml");
                newFile.createNewFile();
                try (InputStream in = plugin.getResource(newFile.getName());
                     OutputStream out = Files.newOutputStream(newFile.toPath())) {
                    ByteStreams.copy(in, out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (File file : folder.listFiles()) {
            if (!file.getName().endsWith(".yml")) {
                continue;
            }

            try {
                ShopSettings settings = ShopSettings.readShop(plugin, file);
                if (settings == null) {
                    plugin.getLogger().severe("Unable to read settings for shop " + file.getName() + ".");
                    continue;
                }

                Shop shop = new Shop(settings, storage.getSection("storage." + settings.key()));

                plugin.getCommandManager().register(Orphans.path(settings.command()).handler(new OrphanCommand() {
                    @DefaultFor("~")
                    public void onShop(Player player) {
                        if (!player.hasPermission("mysticalshop.shop." + settings.key().toLowerCase())) {
                            plugin.getLocaleManager().getMessage("messages.noShopPermission").sendMessage(player);
                            return;
                        }

                        new ShopInventory(plugin, shop, player).open();
                    }
                }));

                activeShops.put(shop.getSettings().key(), shop);
            } catch (IOException e) {
                plugin.getLogger().severe("An error occurred while trying to read settings for shop " + file.getName() + ".");
                e.printStackTrace();
            }
        }
    }

    public void unload() {
        activeShops.values().stream().filter(shop -> shop.getSettings().persistent()).forEach(shop -> {
            storage.set("storage." + shop.getSettings().key() + ".timeRemaining", shop.getTimeUntilRefresh());
            storage.set("storage." + shop.getSettings().key() + ".items", shop.getShopItems().entrySet().stream().map(entry -> entry.getKey() + ";" + entry.getValue().id()).toList());
        });

        try {
            storage.save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
