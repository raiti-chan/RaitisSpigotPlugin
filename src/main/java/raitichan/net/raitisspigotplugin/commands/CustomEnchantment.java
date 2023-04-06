package raitichan.net.raitisspigotplugin.commands;

import com.google.common.collect.ImmutableList;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTCompoundList;
import de.tr7zw.changeme.nbtapi.NBTItem;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CustomEnchantment implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;
        Inventory inventory = Bukkit.createInventory(player, 54, "CustomEnchantment");
        for (Enchant enchantment : Enchant.values()) {
            inventory.addItem(createEnchantUIElement(enchantment));
        }

        player.openInventory(inventory);
        player.addScoreboardTag("Open-CustomEnchantment-UI");
        return false;
    }

    private static ItemStack createEnchantUIElement(Enchant enchant) {
        NBTItem nbtItem = new NBTItem(new ItemStack(Material.ENCHANTED_BOOK));
        NBTCompoundList storedEnchantmentsCompoundList = nbtItem.getCompoundList("StoredEnchantments");
        NBTCompound enchantCompound = storedEnchantmentsCompoundList.addCompound();
        enchantCompound.setShort("lvl", (short) 1);
        enchantCompound.setString("id", enchant.id);
        return nbtItem.getItem();
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return ImmutableList.of();
    }

    public static class CustomEnchantmentEvents implements Listener {

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            HumanEntity humanEntity = e.getWhoClicked();
            if (!(humanEntity instanceof Player)) return;
            if (!humanEntity.getScoreboardTags().contains("Open-CustomEnchantment-UI")) return;
            e.setCancelled(true);


            if (e.getInventory() != e.getClickedInventory()) return;

            Player player = (Player) humanEntity;
            ItemStack targetItem = player.getInventory().getItemInMainHand();
            if (targetItem.getType() == Material.AIR) return;

            ItemStack clickItem = e.getCurrentItem();
            if (clickItem == null) return;

            int upLevel = (e.isLeftClick() ? 1 : -1) * (e.isShiftClick() ? 10 : 1);

            NBTItem targetNbtItem = new NBTItem(targetItem);
            NBTCompoundList targetEnchantmentsCompoundList = targetNbtItem.getCompoundList("Enchantments");

            NBTItem clickNbtItem = new NBTItem(clickItem);
            NBTCompoundList storedEnchantmentsCompoundList = clickNbtItem.getCompoundList("StoredEnchantments");

            for (ReadWriteNBT storedEnchantmentCompound : storedEnchantmentsCompoundList) {
                String id = storedEnchantmentCompound.getString("id");
                boolean isFind = false;
                for (ReadWriteNBT targetEnchantmentCompound : targetEnchantmentsCompoundList) {
                    if (targetEnchantmentCompound.getString("id").equals(id)) {
                        short currentLv = targetEnchantmentCompound.getShort("lvl");
                        targetEnchantmentCompound.setShort("lvl", (short) (currentLv + upLevel));
                        isFind = true;
                        break;
                    }
                }
                targetEnchantmentsCompoundList.removeIf(targetEnchantmentCompound -> targetEnchantmentCompound.getShort("lvl") <= 0);

                if (isFind || e.isRightClick()) continue;
                NBTCompound addEnchantmentCompound = targetEnchantmentsCompoundList.addCompound();
                addEnchantmentCompound.setShort("lvl", (short) upLevel);
                addEnchantmentCompound.setString("id", id);
            }
            targetNbtItem.applyNBT(targetItem);
        }
        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e) {
            HumanEntity humanEntity = e.getPlayer();
            if (!(humanEntity instanceof Player)) return;
            if (!humanEntity.getScoreboardTags().contains("Open-CustomEnchantment-UI")) return;
            Player player = (Player) humanEntity;
            player.removeScoreboardTag("Open-CustomEnchantment-UI");
        }

    }

    public enum Enchant {
        PROTECTION("minecraft:protection"),
        FIRE_PROTECTION("minecraft:fire_protection"),
        FEATHER_FALLING("minecraft:feather_falling"),
        BLAST_PROTECTION("minecraft:blast_protection"),
        PROJECTILE_PROTECTION("minecraft:projectile_protection"),
        THORNS("minecraft:thorns"),
        RESPIRATION("minecraft:respiration"),
        DEPTH_STRIDER("minecraft:depth_strider"),
        AQUA_AFFINITY("minecraft:aqua_affinity"),
        SHARPNESS("minecraft:sharpness"),
        SMITE("minecraft:smite"),
        BANE_OF_ARTHROPODS("minecraft:bane_of_arthropods"),
        KNOCKBACK("minecraft:knockback"),
        FIRE_ASPECT("minecraft:fire_aspect"),
        LOOTING("minecraft:looting"),
        EFFICIENCY("minecraft:efficiency"),
        SILK_TOUCH("minecraft:silk_touch"),
        UNBREAKING("minecraft:unbreaking"),
        FORTUNE("minecraft:fortune"),
        POWER("minecraft:power"),
        PUNCH("minecraft:punch"),
        FLAME("minecraft:flame"),
        INFINITY("minecraft:infinity"),
        LUCK_OF_THE_SEA("minecraft:luck_of_the_sea"),
        LURE("minecraft:lure"),
        FROST_WALKER("minecraft:frost_walker"),
        MENDING("minecraft:mending"),
        BINDING_CURSE("minecraft:binding_curse"),
        VANISHING_CURSE("minecraft:vanishing_curse"),
        IMPALING("minecraft:impaling"),
        RIPTIDE("minecraft:riptide"),
        LOYALTY("minecraft:loyalty"),
        CHANNELING("minecraft:channeling"),
        SWEEPING("minecraft:sweeping"),
        MULTISHOT("minecraft:multishot"),
        PIERCING("minecraft:piercing"),
        QUICK_CHARGE("minecraft:quick_charge"),
        SOUL_SPEED("minecraft:soul_speed"),
        SWIFT_SNEAK("minecraft:swift_sneak")
        ;

        public final String id;

        Enchant(String id) {
            this.id = id;
        }
    }
}
