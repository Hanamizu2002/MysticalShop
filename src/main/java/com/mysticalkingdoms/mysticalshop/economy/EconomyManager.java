package com.mysticalkingdoms.mysticalshop.economy;

import com.mysticalkingdoms.mysticalshop.MysticalShop;
import com.mysticalkingdoms.mysticalshop.economy.impl.PlayerPointsEconomy;
import com.mysticalkingdoms.mysticalshop.economy.impl.UltraEconomyEconomy;
import com.mysticalkingdoms.mysticalshop.economy.impl.VaultEconomy;
import org.bukkit.Bukkit;

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
        registerEconomyManager("VAULT", new VaultEconomy());

        if (Bukkit.getPluginManager().isPluginEnabled("UltraEconomy")) {
            UltraEconomyEconomy.registerCurrencies(this);
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
            registerEconomyManager("PLAYERPOINTS", new PlayerPointsEconomy());
        }
    }

    public void registerEconomyManager(String name, IEconomy economy) {
        economyManagers.put(name, economy);
    }

    public IEconomy getEconomyManager(String manager) {
        return economyManagers.get(manager);
    }
}
