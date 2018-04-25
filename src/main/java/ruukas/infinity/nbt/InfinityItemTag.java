package ruukas.infinity.nbt;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class InfinityItemTag {
	private ItemStack stack;
	
	public InfinityItemTag(ItemStack stack){
		this.stack = stack;
	}

	public InfinityEnchantmentList getEnchantmentList(){
		return new InfinityEnchantmentList(this);
	}
	
	public ItemStack getItemStack(){
		return stack;
	}
	
	NBTTagCompound getTag(){
		if(!stack.hasTagCompound()){
			stack.setTagCompound(new NBTTagCompound());
		}
		
		return stack.getTagCompound();
	}
	
	public boolean exists(){
		return stack.hasTagCompound();
	}

	public void checkEmpty() {
		if(exists() && getTag().hasNoTags()){
			stack.setTagCompound(null);
		}
	}
}
