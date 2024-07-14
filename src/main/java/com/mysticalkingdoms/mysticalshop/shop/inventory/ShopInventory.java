package com.mysticalkingdoms.mysticalshop.shop.inventory;

import com.mysticalkingdoms.mysticalshop.MysticalShop;
import com.mysticalkingdoms.mysticalshop.economy.IEconomy;
import com.mysticalkingdoms.mysticalshop.shop.Shop;
import com.mysticalkingdoms.mysticalshop.utils.FormatUtils;
import com.mysticalkingdoms.mysticalshop.utils.ItemFactory;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class ShopInventory {

    private final MysticalShop plugin;
    private final Shop shop;
    private final Player player;
    private final Gui gui;
    // this is used to keep track of the rotation the player is viewing.
    private int shopRotation;
    public ShopInventory(MysticalShop plugin, Shop shop, Player player) {
        this.plugin = plugin;
        this.shop = shop;
        this.player = player;
        this.gui = Gui.gui()
                .title(shop.getSettings().title())
                .rows(shop.getSettings().rows())
                .disableAllInteractions()
                .create();
    }

    public void open() {
        updateItems();
        updateFiller();

        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (shop.getShopRotation() != shopRotation) {
                    updateItems();
                    return;
                }

                updateFiller();
            }
        };
        runnable.runTaskTimer(plugin, 0L, 20L);
        gui.setCloseGuiAction(event -> runnable.cancel());

        gui.open(player);
    }

    private void updateItems() {
        this.shopRotation = shop.getShopRotation();

        shop.getShopItems().forEach((slot, item) -> {
            if (item.requiredPermission() != null && !player.hasPermission(item.requiredPermission())) {
                gui.setItem(slot, ItemBuilder.from(item.noPermissionItem()).asGuiItem());
                return;
            }

            if (item.boughtPermission() != null && player.hasPermission(item.boughtPermission())) {
                gui.setItem(slot, ItemBuilder.from(item.boughtItem()).asGuiItem());
                return;
            }

            gui.setItem(slot, ItemBuilder.from(item.availableItem()).asGuiItem(event -> {
                IEconomy economy = shop.getSettings().economy();
                player.closeInventory();
                if (economy.getBalance(player) < item.price()) {
                    plugin.getLocaleManager().getMessage("messages.notEnoughBalance").sendMessage(player);
                    return;
                }

                economy.withdrawPlayer(player, item.price());
                item.commands().forEach(command -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName())));
            }));
        });

        gui.update();
    }

    private void updateFiller() {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%timeRemaining%", FormatUtils.formatTime(shop.getTimeUntilRefresh()));

        shop.getSettings().fillerItems().forEach((slot, section) -> {
            gui.setItem(slot, ItemBuilder.from(ItemFactory.createItem(section, placeholders)).asGuiItem(event -> {
                for (String command : section.getStringList("commands")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
                }
            }));
        });

        gui.update();
    }
}
