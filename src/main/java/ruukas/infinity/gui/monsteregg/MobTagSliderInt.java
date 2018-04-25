package ruukas.infinity.gui.monsteregg;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class MobTagSliderInt extends MobTagSlider{
	public MobTagSliderInt(String name, String key, float min, float max, float step, float normal) {
		super(name, key, min, max, step, normal);
	}

	public MobTagSliderInt(String name, String key, float min, float max, float step) {
		super(name, key, min, max, step);
	}

	public int getInt(ItemStack stack) {
		if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("EntityTag", NBT.TAG_COMPOUND) && stack.getSubCompound("EntityTag").hasKey(key, NBT.TAG_INT)) {
			return stack.getSubCompound("EntityTag").getInteger(key);
		}

		return (int) this.min;
	}
	
	@Override
	public float getFloat(ItemStack stack) {
		if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("EntityTag", NBT.TAG_COMPOUND) && stack.getSubCompound("EntityTag").hasKey(key, NBT.TAG_INT)) {
			return stack.getSubCompound("EntityTag").getInteger(key);
		}

		return super.getFloat(stack);
	}
	
	@Override
	public void setValue(ItemStack stack, float f) {
		if(stack == null){
			return;
		}
		
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		
		if(!stack.getTagCompound().hasKey("EntityTag", NBT.TAG_COMPOUND)){
			stack.getTagCompound().setTag("EntityTag", new NBTTagCompound());
		}

		stack.getSubCompound("EntityTag").setInteger(key, (int) f);
	}
}
