package com.mysticalkingdoms.mysticalshop.commands;

import com.mysticalkingdoms.mysticalshop.MysticalShop;
import com.mysticalkingdoms.mysticalshop.shop.Shop;
import com.mysticalkingdoms.mysticalshop.shop.inventory.ShopInventory;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command("mysticalshop")
public class MysticalShopCommands {

    private final MysticalShop plugin;
    public MysticalShopCommands(MysticalShop plugin) {
        this.plugin = plugin;
    }

    @Subcommand("refresh")
    @CommandPermission("mysticalshop.refresh")
    public void onRefresh(CommandSender sender, Shop shop) {
        shop.refreshItems();
        plugin.getLocaleManager().getMessage("messages.shopRefreshed").sendMessage(sender);
    }

    @Subcommand("open")
    @CommandPermission("mysticalshop.open")
    public void onOpen(Player player, Shop shop) {
        if (!player.hasPermission("mysticalshop.shop." + shop.getSettings().key().toLowerCase())) {
            plugin.getLocaleManager().getMessage("messages.noShopPermission").sendMessage(player);
            return;
        }

        new ShopInventory(plugin, shop, player).open();
    }
}
