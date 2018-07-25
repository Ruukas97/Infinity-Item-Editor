package ruukas.qualityorder.util.nbt.itemstack;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import ruukas.qualityorder.util.nbt.QualityNBTUtils;

public class ItemStackTag extends NBTTagCompound{
	public ItemStackTag(){
	}
	
	public ItemStackTag(NBTTagCompound rootTag){
	}
	//http://minecraft.gamepedia.com/Player.dat_format#Item_structure
	//This is from the root tag which is defined in the ItemStack itself.
	/*public void setCount(byte b){
		setByte("Count", b);
	}
	
	public byte getCount(){
		return getByte("Count");
	}
	
	//"Slot" byte - probably not relevant
	
	public void setDamage(short s){
		setShort("Damage", s);
	}
	
	public short getDamage(){
		return getShort("Damage");
	}
	
	public void setID(String id){
		setString("id", id);
	}
	
	public void setID(Item item){
		setString("id", item.getRegistryName().toString());
	}
	
	public String getID(){
		return getString("id");
	}*/
	
	public void setUnbreakable(boolean bool){
		QualityNBTUtils.setOrRemoveByteTag(this, "Unbreakable", bool);
	}
	
	public boolean getIsUnbreakalbe(){
		return QualityNBTUtils.getBoolFromByte(getByte("Unbreakable"));
	}

	//Enchantments
	
	//Atributes
	
	//Potion Effects
	
	//"CanDestroy" TagList - Blocks the item can destroy in adventure mode - Contains String IDs of blocks
	
	
	//Display
	public void setDisplayTag(DisplayTag displayTag){
		displayTag.setTag(DisplayTag.getKeyName(), displayTag);
	}
	
	public DisplayTag getDisplayTag(){
		if(hasKey(DisplayTag.getKeyName(), Constants.NBT.TAG_COMPOUND)){
			return new DisplayTag(getCompoundTag(DisplayTag.getKeyName()));
		}else{
			DisplayTag dTag = new DisplayTag(getCompoundTag(DisplayTag.getKeyName()));
			setDisplayTag(dTag);
			return dTag;
		}
	}
	
	public static class DisplayTag extends NBTTagCompound {
		public DisplayTag(){}
		
		public DisplayTag(NBTTagCompound compoundTag) {
			if(compoundTag.hasKey("Name", Constants.NBT.TAG_STRING)){
				setName(compoundTag.getString("Name"));
			}
			if(compoundTag.hasKey("Lore", Constants.NBT.TAG_LIST)){
				setTag("Lore", compoundTag.getTagList("Lore", Constants.NBT.TAG_STRING));
			}
		}
		
		public static String getKeyName(){
			return "display";
		}

		public void setName(String name){
			setString("Name", name);
		}
		
		public void addLore(String lore){
			if(!hasKey("Lore", Constants.NBT.TAG_LIST)){
				setTag("Lore", new NBTTagList());
			}
			getTagList("Lore", Constants.NBT.TAG_STRING).appendTag(new NBTTagString(lore));
		}
		
		//TODO Colored Armor
	}
	
	//Books
	
	//Skulls
	
	//Fireworks
	
	//Armorstands (and Spawneggs - already did in ItemStackTagEgg)
	
	//Maps
}
