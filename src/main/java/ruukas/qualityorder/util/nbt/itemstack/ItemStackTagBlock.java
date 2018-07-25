package ruukas.qualityorder.util.nbt.itemstack;

import net.minecraft.nbt.NBTTagCompound;
import ruukas.qualityorder.util.nbt.tileentity.TileEntityTag;

public class ItemStackTagBlock extends ItemStackTag{
	public ItemStackTagBlock(TileEntityTag teTag){
		setBlockEntityTag(teTag);
	}
	
	public ItemStackTagBlock(NBTTagCompound teTag){
		super(teTag);
		setBlockEntityTag(teTag.getCompoundTag("BlockEntityTag"));
	}
	
	//TODO CanPlaceOn
	
	public void setBlockEntityTag(TileEntityTag teTag){
		setTag("BlockEntityTag", teTag);
	}
	
	public void setBlockEntityTag(NBTTagCompound teTag){
		setTag("BlockEntityTag", teTag);
	}

	public TileEntityTag getBlockEntityTag(){
		return (TileEntityTag)getCompoundTag("BlockEntityTag");
	}
}
