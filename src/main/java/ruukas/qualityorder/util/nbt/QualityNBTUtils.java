package ruukas.qualityorder.util.nbt;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

public class QualityNBTUtils {
	public static boolean[] trueFalseArray = {true, false};
	public static boolean[] falseTrueArray = {false, true};
	
	public static byte getByteFromBool(boolean bool){
		return (byte) (bool?1:0);
	}
	
	public static boolean getBoolFromByte(byte b){
		return (b==1?true:false);
	}
	
	public static void setOrRemoveByteTag(NBTTagCompound tag, String str, boolean bool){
		if(bool){
			tag.setByte(str, getByteFromBool(bool));
		}else if(tag.hasKey(str, Constants.NBT.TAG_BYTE)){
			tag.removeTag(str);
		}
	}
}
