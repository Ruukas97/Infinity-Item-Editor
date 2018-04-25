package ruukas.infinity.gui.nbt;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;

public class NBTListByte extends NBTListElement {
	public static ItemStack onIcon = new ItemStack(Items.REDSTONE);
	public static ItemStack offIcon = new ItemStack(Items.GUNPOWDER);

	public NBTListByte(String key, NBTTagByte tag, int x, int y) {
		super(key, tag, offIcon, x, y);
	}
	
	@Override
	public ItemStack getIconStack() {
		return getByteValue() == 0 ? offIcon : onIcon;
	}
	
	public NBTTagByte getByteTag(){
		return (NBTTagByte)tag;
	}
	
	public byte getByteValue(){
		return getByteTag().getByte();
	}
	
	@Override
	public String getTypeName() {
		return "Byte";
	}
}
