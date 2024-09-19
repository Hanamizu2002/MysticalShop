package com.mysticalkingdoms.mysticalshop.economy.impl;

import com.mysticalkingdoms.mysticalshop.economy.EconomyManager;
import com.mysticalkingdoms.mysticalshop.economy.IEconomy;
import dev.unnm3d.rediseconomy.api.RedisEconomyAPI;
import dev.unnm3d.rediseconomy.currency.Currency;
import org.bukkit.entity.Player;

public class RedisEconomyEconomy implements IEconomy {

    private final Currency currency;
    public RedisEconomyEconomy(Currency currency) {
        this.currency = currency;
    }

    @Override
    public void withdrawPlayer(Player player, double amount) {
        currency.withdrawPlayer(player, amount);
    }

    @Override
    public double getBalance(Player player) {
        return currency.getBalance(player);
    }

    public static void registerCurrencies(EconomyManager economyManager) {
        for (Currency currency : RedisEconomyAPI.getAPI().getCurrencies()) {
            economyManager.registerEconomyManager("REDISECONOMY_" + currency.getCurrencyName(), new RedisEconomyEconomy(currency));
        }
    }
}
