package com.nekozouneko.virtualchest;

import com.nekozouneko.virtualchest.cmd.MainCmd;
import com.nekozouneko.virtualchest.listener.InventoryClose;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class VirtualChest extends JavaPlugin {

    public static VirtualChest instance;
    private static Economy econ;

    public static Economy getVE() {return econ;}

    public static VirtualChest getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        getCommand("virtualchest").setExecutor(new MainCmd());
        getServer().getPluginManager().registerEvents(new InventoryClose(), this);

        if (getConfig().getString("buy-type").equalsIgnoreCase("vault")) {
            setupEconomy();
        }
    }

    @Override
    public void onDisable() {
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
