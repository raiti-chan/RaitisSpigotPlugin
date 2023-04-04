package raitichan.net.raitisspigotplugin.commands;

import com.google.common.collect.ImmutableList;
import de.tr7zw.changeme.nbtapi.*;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelfInventory implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length < 2) return false;
        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;
        String subCommand = strings[0];
        String inventoryName = strings[1];

        switch (subCommand) {
            case "create": {
                NBTCompound selfInventoryCompound = getSelfInventoryCompound(player);
                if (selfInventoryCompound.hasTag(inventoryName)) return false;
                selfInventoryCompound.addCompound(inventoryName);
                NBTCompoundList itemsCompoundList = getItemsCompoundList(player, inventoryName);
                if (itemsCompoundList == null) return false;
                for (int i = 0; i < 54; i++) {
                    itemsCompoundList.addCompound().setItemStack("ItemStack", new ItemStack(Material.AIR));
                }
                break;
            }
            case "destroy": {
                NBTCompound selfInventoryCompound = getSelfInventoryCompound(player);
                if (!selfInventoryCompound.hasTag(inventoryName)) return false;
                selfInventoryCompound.removeKey(inventoryName);
                break;
            }
            case "open": {
                NBTCompoundList itemsCompoundList = getItemsCompoundList(player, inventoryName);
                if (itemsCompoundList == null) return false;
                openInventory(player, inventoryName, itemsCompoundList);
                break;
            }
            case "get-open-item": {
                NBTCompound selfInventoryCompound = getSelfInventoryCompound(player);
                if (!selfInventoryCompound.hasTag(inventoryName)) return false;
                NBTItem nbtItem = new NBTItem(new ItemStack(Material.CHEST));
                NBTCompound display = nbtItem.addCompound("display");
                display.setString("Name", "{\"text\":\"" + inventoryName + "\",\"color\":\"light_purple\"}");
                display.getStringList("Lore").add("{\"text\":\"Owner:" + player.getDisplayName() + "\"}");

                NBTCompound selfInventoryItemCompound = nbtItem.addCompound("SelfInventoryItem");
                selfInventoryItemCompound.setUUID("Owner", player.getUniqueId());
                selfInventoryItemCompound.setString("InventoryName", inventoryName);
                player.getInventory().addItem(nbtItem.getItem());
                break;
            }
            default:
                return false;
        }
        return true;
    }

    public static NBTCompound getSelfInventoryCompound(Player player) {
        PersistentDataContainer playerDataContainer = player.getPersistentDataContainer();
        NBTPersistentDataContainer nbtPlayerDataContainer = new NBTPersistentDataContainer(playerDataContainer);
        NBTCompound selfInventoryCompound = nbtPlayerDataContainer.getCompound("SelfInventory");
        if (selfInventoryCompound == null) {
            selfInventoryCompound = nbtPlayerDataContainer.addCompound("SelfInventory");
        }
        return selfInventoryCompound;
    }

    public static NBTCompoundList getItemsCompoundList(Player player, String inventoryName) {
        NBTCompound selfInventoryCompound = getSelfInventoryCompound(player);
        NBTCompound inventoryCompound = selfInventoryCompound.getCompound(inventoryName);
        if (inventoryCompound == null) {
            return null;
        }

        return inventoryCompound.getCompoundList("Items");
    }

    public static void openInventory(Player player, String inventoryName, NBTCompoundList itemsCompoundList) {
        Inventory inventory = Bukkit.createInventory(player, 54, inventoryName);
        int index = 0;
        for (ReadWriteNBT itemCompound : itemsCompoundList) {
            ItemStack itemStack = itemCompound.getItemStack("ItemStack");
            inventory.setItem(index, itemStack);
            index++;
        }
        player.openInventory(inventory);
        player.addScoreboardTag("Open-SelfInventory");
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        switch (strings.length) {
            case 1: {
                return Arrays.asList("create", "destroy", "open", "get-open-item");
            }
            case 2: {
                if (!(commandSender instanceof Player)) break;
                Player player = (Player) commandSender;
                NBTCompound selfInventoryCompound = getSelfInventoryCompound(player);
                return new ArrayList<>(selfInventoryCompound.getKeys());
            }
        }
        return ImmutableList.of();
    }

    public static class SelfInventoryEvents implements Listener {

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            HumanEntity humanEntity = e.getPlayer();
            if (!(humanEntity instanceof Player)) return;
            if (!humanEntity.getScoreboardTags().contains("Open-SelfInventory")) return;
            Player player = (Player) humanEntity;
            player.removeScoreboardTag("Open-SelfInventory");
            Inventory inventory = e.getInventory();

            NBTCompoundList itemsCompoundList = SelfInventory.getItemsCompoundList(player, e.getView().getTitle());
            if (itemsCompoundList == null) return;
            int index = 0;
            for (ItemStack itemStack : inventory) {
                itemsCompoundList.get(index).setItemStack("ItemStack", itemStack);
                index++;
            }
        }

        @EventHandler
        public void onPlayerInteractEvent(PlayerInteractEvent e) {
            if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
            if (e.getMaterial() != Material.CHEST) return;

            ItemStack itemStack = e.getItem();
            if (itemStack == null) return;

            NBTItem nbtItem = new NBTItem(itemStack);
            if (!nbtItem.hasTag("SelfInventoryItem")) return;

            Player player = e.getPlayer();
            NBTCompound selfInventoryItemCompound = nbtItem.getCompound("SelfInventoryItem");
            if (!selfInventoryItemCompound.getUUID("Owner").equals(player.getUniqueId())) {
                nbtItem.removeKey("display");
                nbtItem.removeKey("SelfInventoryItem");
                nbtItem.applyNBT(itemStack);
                return;
            }

            String inventoryName = selfInventoryItemCompound.getString("InventoryName");
            NBTCompoundList itemsCompoundList = SelfInventory.getItemsCompoundList(player, inventoryName);
            if (itemsCompoundList == null) {
                nbtItem.removeKey("display");
                nbtItem.removeKey("SelfInventoryItem");
                nbtItem.applyNBT(itemStack);
                return;
            }

            SelfInventory.openInventory(player, inventoryName, itemsCompoundList);
            e.setCancelled(true);
        }
    }
}
