package ruukas.infinity.util;

import net.minecraft.item.ItemStack;

public class ItemStackUtil
{
    public static boolean isSameStack(ItemStack one, ItemStack two){
        if(one == two){
            return true;
        }
        
        if(one.isEmpty()){
            return two.isEmpty();
        }
        else if(two.isEmpty()){
            return false;
        }
        
        if(one.getItem() != two.getItem()){
            return false;
        }
        
        if(one.getItemDamage() != two.getItemDamage() && (one.isItemStackDamageable() || two.isItemStackDamageable())){
            return false;
        }
        
        if(one.getTagCompound() == null || one.getTagCompound().hasNoTags()){
            return two.getTagCompound() == null || two.getTagCompound().hasNoTags();
        }
        else if(two.getTagCompound() == null || two.getTagCompound().hasNoTags()){
            return false;
        }
        
        return one.getTagCompound().equals( two.getTagCompound() );
    }
}
