package raitichan.net.raitisspigotplugin.nbtstruct;

import de.tr7zw.changeme.nbtapi.NBTCompound;

public class NBTXpBank extends NBTStructBase {
    NBTXpBank(NBTCompound compound) {
        super(compound);
    }

    public int getLevel() {
        if (!this.compound.hasTag("Level")){
            this.setLevel(0);
        }
        return this.compound.getInteger("Level");
    }

    public void setLevel(int level) {
        this.compound.setInteger("Level", level);
    }


}
