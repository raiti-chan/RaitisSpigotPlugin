package raitichan.net.raitisspigotplugin.nbtstruct;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTPersistentDataContainer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NBTPlayer extends NBTStructBase {

    public final Player player;

    public NBTPlayer(@NotNull Player player) {
        super(new NBTPersistentDataContainer(player.getPersistentDataContainer()));
        this.player = player;
    }

    @NotNull
    public NBTXpBank getXpBank() {
        NBTCompound nbtCompound = this.compound.getOrCreateCompound("XpBank");
        return new NBTXpBank(nbtCompound);
    }

    @NotNull
    public NBTSelfInventory getSelfInventory() {
        NBTCompound nbtCompound = this.compound.getOrCreateCompound("SelfInventory");
        return new NBTSelfInventory(nbtCompound);
    }
}
