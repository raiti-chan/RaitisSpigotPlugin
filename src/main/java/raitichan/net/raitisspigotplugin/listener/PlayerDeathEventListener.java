package raitichan.net.raitisspigotplugin.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerDeathEventListener implements Listener {

    @EventHandler
    public void OnPlayerDeath(PlayerDeathEvent e) {
        ItemStack headItemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) headItemStack.getItemMeta();
        if (skullMeta == null) {
            return;
        }
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(e.getEntity().getUniqueId()));
        headItemStack.setItemMeta(skullMeta);
        e.getDrops().add(headItemStack);
    }

}
