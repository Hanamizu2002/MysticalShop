package com.mysticalkingdoms.mysticalshop.shop.inventory;

import com.mysticalkingdoms.mysticalshop.MysticalShop;
import com.mysticalkingdoms.mysticalshop.economy.IEconomy;
import com.mysticalkingdoms.mysticalshop.shop.Shop;
import com.mysticalkingdoms.mysticalshop.shop.items.ShopItem;
import com.mysticalkingdoms.mysticalshop.utils.FormatUtils;
import com.mysticalkingdoms.mysticalshop.utils.ItemFactory;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
                String webhookUrl = plugin.getConfig().getString("webhook.url");
                if (webhookUrl != null && !webhookUrl.isEmpty()) {
                    sendWebhook(webhookUrl, player, item);
                } else {
                    plugin.getLogger().warning("Webhook URL 未配置！");
                }
            }));
        });

        gui.update();
    }

    private void sendWebhook(String webhookUrl, Player player, ShopItem item) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(webhookUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    // 构造 JSON 负载，将 prize 参数一起提交（这里 prize 直接调用 toString()，建议根据实际情况使用 JSON 库生成合法 JSON）
                    String prizeJson = item.prize().toString();
                    String jsonPayload = "{\"player\":\"" + player.getName() + "\","
                            + "\"item\":\"" + item.id() + "\","
                            + "\"price\":" + item.price() + ","
                            + "\"prize\":" + prizeJson + "}";
                    OutputStream os = conn.getOutputStream();
                    os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                    os.close();
                    int responseCode = conn.getResponseCode();
                    plugin.getLogger().info("Webhook sent, response code: " + responseCode);
                } catch (Exception e) {
                    plugin.getLogger().severe("发送 webhook 失败: " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
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
