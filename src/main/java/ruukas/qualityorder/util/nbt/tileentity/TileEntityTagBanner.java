package ruukas.qualityorder.util.nbt.tileentity;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.BannerPattern;
import net.minecraftforge.common.util.Constants;

public class TileEntityTagBanner extends TileEntityTag {
	public TileEntityTagBanner(NBTTagCompound tag){
		setBaseColor(tag.getInteger("Base"));
		setPatterns(tag.getTagList("Patterns", Constants.NBT.TAG_COMPOUND));
	}
	
	public TileEntityTagBanner(){
		this(EnumDyeColor.WHITE);
	}
	
	public TileEntityTagBanner(EnumDyeColor baseColor){
		setBaseColor(baseColor);
	}
	
	public TileEntityTagBanner(EnumDyeColor baseColor, NBTTagList patterns){
		setBaseColor(baseColor);
		setPatterns(patterns);
	}
	
	public EnumDyeColor getBaseColor(){
		return EnumDyeColor.byDyeDamage(getInteger("Base"));
	}
	
	public  void setBaseColor(EnumDyeColor dye){
		setInteger("Base", dye.getDyeDamage());
	}
	
	public  void setBaseColor(int dye){
		setInteger("Base", dye);
	}
	
	public NBTTagList getPatterns(){
		return getTagList("Patterns", Constants.NBT.TAG_COMPOUND);
	}
	
	public void setPatterns(NBTTagList patterns){
		setTag("Patterns", patterns);
	}
	
	public void addPattern(Pattern pattern){
		getPatterns().appendTag(pattern);
	}
	
	public static class Pattern extends NBTTagCompound{
		public Pattern(EnumDyeColor dye, BannerPattern pattern){
			setColor(dye);
			setPattern(pattern);
		}
		
		public void setColor(EnumDyeColor dye){
			setInteger("Color", dye.getDyeDamage());
		}
		
		public EnumDyeColor getColor(){
			return EnumDyeColor.byMetadata(getInteger("Color"));
		}
		
		public void setPattern(BannerPattern pattern){
			setString("Pattern", pattern.getHashname());
		}
		
		public BannerPattern getPattern(){
			return BannerPattern.byHash(getString("Pattern"));
		}
	}
}
