package com.mysticalkingdoms.mysticalshop.shop;

import com.mysticalkingdoms.mysticalshop.MysticalShop;
import com.mysticalkingdoms.mysticalshop.economy.IEconomy;
import com.mysticalkingdoms.mysticalshop.shop.items.ShopItem;
import com.mysticalkingdoms.mysticalshop.utils.RandomCollection;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ShopSettings(YamlDocument config,
                           String key,
                           String command,
                           int refreshTime,
                           boolean persistent,
                           boolean unique,
                           IEconomy economy,
                           Component title,
                           int rows,
                           List<Integer> shopSlots,
                           RandomCollection shopItems,
                           Map<Integer, Section> fillerItems) {

    public static ShopSettings readShop(MysticalShop plugin, File file) throws IOException {
        YamlDocument config = YamlDocument.create(file);

        String key = config.getString("settings.key");
        if (key == null) {
            plugin.getLogger().warning("No key found for shop " + file.getName());
            return null;
        }

        String command = config.getString("settings.command");
        boolean persistent = config.getBoolean("settings.persistent");
        boolean unique = config.getBoolean("settings.unique");

        IEconomy economy = plugin.getEconomyManager().getEconomyManager(config.getString("settings.economy"));
        if (economy == null) {
            plugin.getLogger().warning("No valid economy found for shop " + file.getName());
            return null;
        }

        int refreshTime = config.getInt("settings.refreshTime");

        Component title = MiniMessage.miniMessage().deserialize(config.getString("settings.inventory.title"));
        int rows = config.getInt("settings.inventory.rows");
        List<Integer> shopSlots = config.getIntList("settings.inventory.shopSlots");

        RandomCollection shopItems = new RandomCollection();
        for (String itemKey : config.getSection("items").getRoutesAsStrings(false)) {
            Section section = config.getSection("items." + itemKey);

            ShopItem shopItem = ShopItem.readItem(section);
            shopItems.add(shopItem);
        }

        Map<Integer, Section> fillerItems = new HashMap<>();
        if (config.isSection("filler-items")) {
            for (String itemKey : config.getSection("filler-items").getRoutesAsStrings(false)) {
                Section section = config.getSection("filler-items." + itemKey);
                List<Integer> slots = section.getIntList("slots");

                for (Integer slot : slots) {
                    fillerItems.put(slot, section);
                }
            }
        }

        return new ShopSettings(config, key, command, refreshTime, persistent, unique, economy, title, rows, shopSlots, shopItems, fillerItems);
    }
}
