package raitichan.net.raitisspigotplugin;

import org.bukkit.plugin.java.JavaPlugin;
import raitichan.net.raitisspigotplugin.commands.*;

import java.util.Objects;

public final class RaitisSpigotPlugin extends JavaPlugin {

    public static RaitisSpigotPlugin INSTANCE;

    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;
        Objects.requireNonNull(this.getCommand("player-position")).setExecutor(new PlayerPosition());
        Objects.requireNonNull(this.getCommand("get-my-head")).setExecutor(new GetMyHead());
        Objects.requireNonNull(this.getCommand("xp-bank")).setExecutor(new XpBank());
        Objects.requireNonNull(this.getCommand("self-inventory")).setExecutor(new SelfInventory());
        getServer().getPluginManager().registerEvents(new SelfInventory.SelfInventoryEvents(), this);
        Objects.requireNonNull(this.getCommand("custom-enchantment")).setExecutor(new CustomEnchantment());
        getServer().getPluginManager().registerEvents(new CustomEnchantment.CustomEnchantmentEvents(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
