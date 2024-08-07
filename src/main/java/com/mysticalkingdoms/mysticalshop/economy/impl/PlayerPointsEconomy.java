package com.mysticalkingdoms.mysticalshop.economy.impl;

import com.mysticalkingdoms.mysticalshop.economy.IEconomy;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PlayerPointsEconomy implements IEconomy {

    private final PlayerPointsAPI api;
    public PlayerPointsEconomy() {
        this.api = PlayerPoints.getInstance().getAPI();
    }

    @Override
    public void withdrawPlayer(Player player, double amount) {
        api.take(player.getUniqueId(), (int) amount);
    }

    @Override
    public double getBalance(Player player) {
        return api.look(player.getUniqueId());
    }
}
