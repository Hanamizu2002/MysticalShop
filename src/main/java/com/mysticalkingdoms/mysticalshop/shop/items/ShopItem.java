package com.mysticalkingdoms.mysticalshop.shop.items;

import com.mysticalkingdoms.mysticalshop.utils.ItemFactory;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public record ShopItem(String id, ItemStack availableItem, ItemStack boughtItem, String boughtPermission, ItemStack noPermissionItem, String requiredPermission, List<String> prize, double price, double weight) {

    public static ShopItem readItem(Section section) {
        ItemStack availableItem = ItemFactory.createItem(section.getSection("available-item"), Collections.emptyMap());

        ItemStack boughtItem = ItemFactory.createItem(section.getSection("bought-item"), Collections.emptyMap());
        String boughtPermission = section.getString("bought-permission");

        ItemStack noPermissionItem = ItemFactory.createItem(section.getSection("no-permission-item"), Collections.emptyMap());
        String requiredPermission = section.getString("required-permission");

        List<String> prize = section.getStringList("prizes");
        double price = section.getDouble("price");
        double weight = section.getDouble("weight");

        return new ShopItem(section.getNameAsString(), availableItem, boughtItem, boughtPermission, noPermissionItem, requiredPermission, prize, price, weight);
    }
}
