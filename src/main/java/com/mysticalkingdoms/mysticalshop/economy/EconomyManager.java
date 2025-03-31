package com.mysticalkingdoms.mysticalshop.economy;

import com.mysticalkingdoms.mysticalshop.MysticalShop;
import com.mysticalkingdoms.mysticalshop.economy.impl.PlayerPointsEconomy;

import java.util.HashMap;
import java.util.Map;

public class EconomyManager {

    private final MysticalShop plugin;
    private final Map<String, IEconomy> economyManagers = new HashMap<>();

    public EconomyManager(MysticalShop plugin) {
        this.plugin = plugin;
        loadInternalManagers();
    }

    private void loadInternalManagers() {
        registerEconomyManager("PLAYERPOINTS", new PlayerPointsEconomy());
        plugin.getLogger().info("Loaded currencies: " + String.join(", ", economyManagers.keySet()));
    }

    public void registerEconomyManager(String name, IEconomy economy) {
        economyManagers.put(name, economy);
    }

    public IEconomy getEconomyManager(String manager) {
        return economyManagers.get(manager);
    }
}
