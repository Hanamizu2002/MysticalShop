package com.mysticalkingdoms.mysticalshop.shop;

import com.mysticalkingdoms.mysticalshop.shop.items.ShopItem;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Shop {

    private final ShopSettings settings;
    private final Map<Integer, ShopItem> shopItems;
    private int timeUntilRefresh;
    private int shopRotation;
    public Shop(ShopSettings settings, Section storage) {
        this.settings = settings;
        this.shopItems = new HashMap<>();

        if (!settings.persistent()) {
            refreshItems();
            return;
        }

        if (storage != null) {
            timeUntilRefresh = storage.getInt("timeRemaining");
            for (String id : storage.getStringList("items")) {
                String[] split = id.split(";");
                int slot = Integer.parseInt(split[0]);

                shopItems.put(slot, settings.shopItems().getItemByName(split[1]));
            }
        }
    }

    public void refreshItems() {
        shopItems.clear();

        for (Integer slot : settings.shopSlots()) {
            shopItems.put(slot, generateUnique());
        }

        shopRotation++;
        timeUntilRefresh = settings.refreshTime();
    }

    public ShopItem generateUnique() {
        ShopItem shopItem = settings.shopItems().next();
        if (settings.shopSlots().size() > settings.shopItems().size()) {
            return shopItem;
        }

        if (settings.unique() && shopItems.containsValue(shopItem)) {
            return generateUnique();
        }

        return shopItem;
    }

    public void tick() {
        timeUntilRefresh--;

        if (timeUntilRefresh <= 0) {
            refreshItems();
        }
    }

    public Map<Integer, ShopItem> getShopItems() {
        return Collections.unmodifiableMap(shopItems);
    }

    public ShopSettings getSettings() {
        return settings;
    }

    public int getTimeUntilRefresh() {
        return timeUntilRefresh;
    }

    public int getShopRotation() {
        return shopRotation;
    }
}
