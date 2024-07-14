package com.mysticalkingdoms.mysticalshop.economy.impl;

import com.mysticalkingdoms.mysticalshop.economy.EconomyManager;
import com.mysticalkingdoms.mysticalshop.economy.IEconomy;
import me.TechsCode.UltraEconomy.UltraEconomy;
import me.TechsCode.UltraEconomy.objects.Account;
import me.TechsCode.UltraEconomy.objects.Currency;
import org.bukkit.entity.Player;

import java.util.Optional;

public class UltraEconomyEconomy implements IEconomy {

    private final Currency currency;
    public UltraEconomyEconomy(Currency currency) {
        this.currency = currency;
    }

    @Override
    public void depositPlayer(Player player, double amount) {
        UltraEconomy.getAPI().getAccounts().uuid(player.getUniqueId()).ifPresent(account -> {
            account.addBalance(currency, amount);
        });
    }

    @Override
    public void withdrawPlayer(Player player, double amount) {
        UltraEconomy.getAPI().getAccounts().uuid(player.getUniqueId()).ifPresent(account -> {
            account.removeBalance(currency, amount);
        });
    }

    @Override
    public double getBalance(Player player) {
        Optional<Account> optional = UltraEconomy.getAPI().getAccounts().uuid(player.getUniqueId());
        return optional.map(account -> account.getBalance(currency).getOnHand()).orElse(0.0);
    }

    public static void registerCurrencies(EconomyManager economyManager) {
        for (Currency currency : UltraEconomy.getAPI().getCurrencies()) {
            economyManager.registerEconomyManager("ULTRAECONOMY_" + currency.getName(), new UltraEconomyEconomy(currency));
        }
    }
}
