package ruukas.qualityorder.util.itemstack;

import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import ruukas.qualityorder.util.nbt.itemstack.ItemStackTagBlock;
import ruukas.qualityorder.util.nbt.tileentity.TileEntityTagBanner;

public class QualityBanner{
	private ItemStack banner;
	private ItemStackTagBlock bannerTag;
	private TileEntityTagBanner bannerTETag;
	
	
	public QualityBanner(TileEntityTagBanner entityTag){
		this(entityTag, false);
	}
	
	public QualityBanner(TileEntityTagBanner entityTag, boolean isShield){
		bannerTETag = entityTag;
		banner = new ItemStack(isShield?Items.SHIELD:Items.BANNER);
		if(!isShield){
			banner.setItemDamage(bannerTETag.getBaseColor().getDyeDamage());
		}
		bannerTag = new ItemStackTagBlock(entityTag);
		banner.setTagCompound(bannerTag);
	}
	
	public QualityBanner(NBTTagCompound compound, boolean isTeTag){
		this(compound, isTeTag, false);
	}
	
	public QualityBanner(NBTTagCompound compound, boolean isTeTag, boolean isShield){
		bannerTETag = new TileEntityTagBanner(isTeTag?compound:compound.getCompoundTag("BlockEntityTag"));
		banner = new ItemStack(isShield?Items.SHIELD:Items.BANNER);
		if(!isShield){
			banner.setItemDamage(bannerTETag.getBaseColor().getDyeDamage());
		}
		bannerTag = new ItemStackTagBlock(bannerTETag);
		banner.setTagCompound(bannerTag);
	}
	
	public TileEntityTagBanner getTileEntityTagBanner(){
		return bannerTETag;
	}
	
	public ItemStack getItemStack(){
		return banner;
	}
	
	public ItemStackTagBlock getItemStackTagBlock(){
		return bannerTag;
	}
	
	public void addToList(List<ItemStack> stackList){
		stackList.add(banner);
	}
}
