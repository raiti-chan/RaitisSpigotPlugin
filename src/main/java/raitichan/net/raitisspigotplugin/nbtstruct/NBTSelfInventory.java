package raitichan.net.raitisspigotplugin.nbtstruct;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTCompoundList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class NBTSelfInventory extends NBTStructBase {
    NBTSelfInventory(@NotNull NBTCompound compound) {
        super(compound);
    }

    @NotNull
    public NBTSelfInventory.NBTInventory getInventory(@NotNull String inventoryName) {
        NBTCompound nbtCompound = this.compound.getOrCreateCompound(inventoryName);
        return new NBTInventory(nbtCompound);
    }
    public void removeInventory(@NotNull String inventoryName) {
        this.compound.removeKey(inventoryName);
    }

    public boolean hasInventory(@NotNull String inventoryName) {
        return this.compound.hasTag(inventoryName);
    }

    @NotNull
    public Set<String> getInventoryNames() {
        return this.compound.getKeys();
    }


    public static class NBTInventory extends NBTStructBase {

        protected NBTInventory(@NotNull NBTCompound compound) {
            super(compound);
        }

        public String getInventoryName() {
            return this.compound.getName();
        }


        @NotNull
        public NBTInventoryItemList getItems() {
            NBTCompoundList nbtCompoundList = this.compound.getCompoundList("Items");
            return new NBTInventoryItemList(nbtCompoundList);
        }

    }
    public static class NBTInventoryItemList extends NBTStructListBase<NBTInventoryItem> {

        protected NBTInventoryItemList(NBTCompoundList compoundList) {
            super(compoundList);
        }

        @Override
        @NotNull
        protected NBTInventoryItem createStructInstance(NBTCompound compound) {
            return new NBTInventoryItem(compound);
        }
    }

    public static class NBTInventoryItem extends NBTStructBase {

        protected NBTInventoryItem(@NotNull NBTCompound compound) {
            super(compound);
        }

        public ItemStack getItemStack() {
            return this.compound.getItemStack("ItemStack");
        }
        public void setItemStack(@NotNull ItemStack itemStack) {
            this.compound.setItemStack("ItemStack", itemStack);
        }
    }
}
