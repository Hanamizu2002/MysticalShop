package com.mysticalkingdoms.mysticalshop.economy;

import org.bukkit.entity.Player;

public interface IEconomy {

    void depositPlayer(Player player, double amount);
    void withdrawPlayer(Player player, double amount);
    double getBalance(Player player);
}
