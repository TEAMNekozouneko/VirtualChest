package com.nekozouneko.virtualchest;

import com.nekozouneko.virtualchest.cmd.MainCmd;
import com.nekozouneko.virtualchest.listener.InventoryClose;
import org.bukkit.plugin.java.JavaPlugin;

public final class VirtualChest extends JavaPlugin {

    public static VirtualChest instance;

    public static VirtualChest getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        getCommand("virtualchest").setExecutor(new MainCmd());
        getServer().getPluginManager().registerEvents(new InventoryClose(), this);
    }

    @Override
    public void onDisable() {
    }
}
