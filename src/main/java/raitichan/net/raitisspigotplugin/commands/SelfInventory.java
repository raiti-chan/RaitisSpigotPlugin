package raitichan.net.raitisspigotplugin.commands;

import com.google.common.collect.ImmutableList;
import de.tr7zw.changeme.nbtapi.*;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import raitichan.net.raitisspigotplugin.nbtstruct.NBTPlayer;
import raitichan.net.raitisspigotplugin.nbtstruct.NBTSelfInventory;

import java.util.ArrayList;
import java.util.List;

public class SelfInventory implements TabCompleter, CommandExecutor {

    private static final List<String> SUB_COMMANDS = ImmutableList.of("create", "rename", "destroy", "open", "get-open-item");

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        switch (strings.length) {
            case 1: {
                return SUB_COMMANDS;
            }
            case 2: {
                if (!(commandSender instanceof Player)) break;
                Player player = (Player) commandSender;
                NBTSelfInventory nbtSelfInventory = new NBTPlayer(player).getSelfInventory();
                return new ArrayList<>(nbtSelfInventory.getInventoryNames());
            }
        }
        return ImmutableList.of();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length < 2) return false;
        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;
        NBTSelfInventory nbtSelfInventory = new NBTPlayer(player).getSelfInventory();

        String subCommand = strings[0];
        String inventoryName = strings[1];

        switch (subCommand) {
            case "create":
                return create(nbtSelfInventory, inventoryName);
            case "rename":
                if (strings.length < 3) return false;
                return rename(nbtSelfInventory, inventoryName, strings[2]);
            case "destroy":
                return destroy(nbtSelfInventory, inventoryName);
            case "open":
                return open(player, nbtSelfInventory, inventoryName);
            case "get-open-item":
                return getOpenItem(player, nbtSelfInventory, inventoryName);
            default:
                return false;
        }
    }

    private static boolean create(@NotNull NBTSelfInventory nbtSelfInventory, @NotNull String inventoryName) {
        if (nbtSelfInventory.hasInventory(inventoryName)) return false;
        NBTSelfInventory.NBTInventory nbtInventory = nbtSelfInventory.getInventory(inventoryName);
        NBTSelfInventory.NBTInventoryItemList nbtInventoryItemList = nbtInventory.getItems();
        for (int i = 0; i < 54; i++) {
            nbtInventoryItemList.addNBTStruct().setItemStack(new ItemStack(Material.AIR));
        }
        return true;
    }


    public static boolean rename(@NotNull NBTSelfInventory nbtSelfInventory, @NotNull String src, @NotNull String dst) {
        if (!nbtSelfInventory.hasInventory(src)) return false;
        if (nbtSelfInventory.hasInventory(dst)) return false;
        nbtSelfInventory.getInventory(dst).compound.mergeCompound(nbtSelfInventory.getInventory(src).compound);
        nbtSelfInventory.removeInventory(src);
        return true;
    }

    private static boolean destroy(@NotNull NBTSelfInventory nbtSelfInventory, @NotNull String inventoryName) {
        if (!nbtSelfInventory.hasInventory(inventoryName)) return false;
        nbtSelfInventory.removeInventory(inventoryName);
        return true;
    }

    private static boolean open(@NotNull Player player, @NotNull NBTSelfInventory nbtSelfInventory, @NotNull String inventoryName) {
        if (!nbtSelfInventory.hasInventory(inventoryName)) return false;
        openInventory(player, nbtSelfInventory.getInventory(inventoryName));
        return true;
    }

    private static boolean getOpenItem(@NotNull Player player, @NotNull NBTSelfInventory nbtSelfInventory, @NotNull String inventoryName) {
        if (!nbtSelfInventory.hasInventory(inventoryName)) return false;
        NBTItem nbtItem = new NBTItem(new ItemStack(Material.CHEST));
        NBTCompound display = nbtItem.addCompound("display");
        display.setString("Name", "{\"text\":\"" + inventoryName + "\",\"color\":\"light_purple\"}");
        display.getStringList("Lore").add("{\"text\":\"Owner:" + player.getDisplayName() + "\"}");

        NBTCompound selfInventoryItemCompound = nbtItem.addCompound("SelfInventoryItem");
        selfInventoryItemCompound.setUUID("Owner", player.getUniqueId());
        selfInventoryItemCompound.setString("InventoryName", inventoryName);
        player.getInventory().addItem(nbtItem.getItem());
        return true;
    }

    public static void openInventory(Player player, NBTSelfInventory.NBTInventory nbtInventory) {
        Inventory inventory = Bukkit.createInventory(player, 54, nbtInventory.getInventoryName());
        int index = 0;
        for (NBTSelfInventory.NBTInventoryItem nbtInventoryItem : nbtInventory.getItems()) {
            ItemStack itemStack = nbtInventoryItem.getItemStack();
            inventory.setItem(index, itemStack);
            index++;
        }
        player.openInventory(inventory);
        player.addScoreboardTag("Open-SelfInventory");
    }





    public static class SelfInventoryEvents implements Listener {

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            HumanEntity humanEntity = e.getPlayer();
            if (!(humanEntity instanceof Player)) return;
            if (!humanEntity.getScoreboardTags().contains("Open-SelfInventory")) return;
            Player player = (Player) humanEntity;
            player.removeScoreboardTag("Open-SelfInventory");

            NBTSelfInventory nbtSelfInventory = new NBTPlayer(player).getSelfInventory();
            NBTSelfInventory.NBTInventory nbtInventory = nbtSelfInventory.getInventory(e.getView().getTitle());
            NBTSelfInventory.NBTInventoryItemList nbtInventoryItemList = nbtInventory.getItems();
            Inventory inventory = e.getInventory();

            int index = 0;
            for (ItemStack itemStack : inventory) {
                nbtInventoryItemList.get(index).setItemStack(itemStack);
                index++;
            }
        }

        @EventHandler
        public void onPlayerInteractEvent(PlayerInteractEvent e) {
            if (!(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
            if (e.getMaterial() != Material.CHEST) return;

            ItemStack itemStack = e.getItem();
            if (itemStack == null) return;

            NBTItem nbtItem = new NBTItem(itemStack, true);
            if (!nbtItem.hasTag("SelfInventoryItem")) return;

            Player player = e.getPlayer();
            NBTCompound selfInventoryItemCompound = nbtItem.getCompound("SelfInventoryItem");
            if (!selfInventoryItemCompound.getUUID("Owner").equals(player.getUniqueId())) {
                removeInventoryItemTags(nbtItem);
                return;
            }

            String inventoryName = selfInventoryItemCompound.getString("InventoryName");
            NBTSelfInventory nbtSelfInventory = new NBTPlayer(player).getSelfInventory();
            if (!nbtSelfInventory.hasInventory(inventoryName)) {
                removeInventoryItemTags(nbtItem);
                return;
            }
            SelfInventory.openInventory(player, nbtSelfInventory.getInventory(inventoryName));
            e.setCancelled(true);
        }

        public static void removeInventoryItemTags(NBTItem nbtItem) {
            nbtItem.removeKey("display");
            nbtItem.removeKey("SelfInventoryItem");
        }
    }
}
