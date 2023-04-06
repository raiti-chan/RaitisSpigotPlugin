package raitichan.net.raitisspigotplugin.nbtstruct;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import org.jetbrains.annotations.NotNull;

public abstract class NBTStructBase {
    @NotNull
    public final NBTCompound compound;
    protected NBTStructBase(@NotNull NBTCompound compound) {
        this.compound = compound;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof NBTStructBase) {
            NBTStructBase nbtStructBase = (NBTStructBase)obj;
            return this.compound.equals(nbtStructBase.compound);
        }
        return false;
    }
}
