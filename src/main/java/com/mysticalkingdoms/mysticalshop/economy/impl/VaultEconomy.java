package com.mysticalkingdoms.mysticalshop.economy.impl;

import com.mysticalkingdoms.mysticalshop.economy.IEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultEconomy implements IEconomy {

    private final Economy economy;
    public VaultEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            throw new IllegalStateException("Vault not found when enabling the Vault economy manager.");
        }

        RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            throw new IllegalStateException("Could not find an economy plugin!");
        }

        this.economy = rsp.getProvider();
    }

    @Override
    public void depositPlayer(Player player, double amount) {
        economy.depositPlayer(player, amount);
    }

    @Override
    public void withdrawPlayer(Player player, double amount) {
        economy.withdrawPlayer(player, amount);
    }

    @Override
    public double getBalance(Player player) {
        return economy.getBalance(player);
    }
}
