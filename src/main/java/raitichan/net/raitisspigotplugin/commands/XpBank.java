package raitichan.net.raitisspigotplugin.commands;

import com.google.common.collect.ImmutableList;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import raitichan.net.raitisspigotplugin.RaitisSpigotPlugin;

import java.util.Arrays;
import java.util.List;

public class XpBank implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length < 1) return false;
        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;
        PersistentDataContainer playerDataContainer = player.getPersistentDataContainer();
        NamespacedKey bankLVKey = new NamespacedKey(RaitisSpigotPlugin.INSTANCE, "BankLV");
        if (!playerDataContainer.has(bankLVKey, PersistentDataType.INTEGER)) {
            playerDataContainer.set(bankLVKey, PersistentDataType.INTEGER, 0);
        }
        Integer bankLvObj = playerDataContainer.get(bankLVKey, PersistentDataType.INTEGER);
        if (bankLvObj == null) return false;
        int bankLv = bankLvObj;
        switch (strings[0]) {
            case "show":
                player.sendMessage("Bank Level : " + bankLv + "Lv");
                break;
            case "deposit":
                if (strings.length < 2) return false;
                try {
                    int lv = Integer.parseInt(strings[1]);
                    if (player.getLevel() < lv) return false;
                    player.giveExpLevels(-lv);
                    playerDataContainer.set(bankLVKey, PersistentDataType.INTEGER, bankLv + lv);
                } catch (NumberFormatException e) {
                    return false;
                }
                break;
            case "withdraw":
                if (strings.length < 2) return false;
                try {
                    int lv = Integer.parseInt(strings[1]);
                    if (bankLv < lv) return false;
                    player.giveExpLevels(lv);
                    playerDataContainer.set(bankLVKey, PersistentDataType.INTEGER, bankLv - lv);
                } catch (NumberFormatException e) {
                    return false;
                }
                break;
            case "splash":
                if (strings.length < 2) return false;
                try {
                    int lv = Integer.parseInt(strings[1]);
                    if (bankLv < lv) return false;
                    int xp = bankLv >= 15 ? 37 + (bankLv - 15) * 5 : 7 + bankLv * 2;
                    for (int i = 0; i < lv; i++) {
                        Entity xpOrb = player.getWorld().spawnEntity(player.getLocation(), EntityType.EXPERIENCE_ORB);
                        NBTEntity nbtEntity = new NBTEntity(xpOrb);
                        nbtEntity.setInteger("Value", xp);
                    }
                    playerDataContainer.set(bankLVKey, PersistentDataType.INTEGER, bankLv - lv);
                } catch (NumberFormatException e) {
                    return false;
                }
                break;
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            return Arrays.asList("show", "deposit", "withdraw", "splash");
        }
        return ImmutableList.of();
    }
}
