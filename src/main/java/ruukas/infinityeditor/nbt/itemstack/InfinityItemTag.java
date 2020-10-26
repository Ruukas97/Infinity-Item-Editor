package ruukas.infinity.nbt.itemstack;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import ruukas.infinity.gui.GuiHideFlags.Flags;
import ruukas.infinity.nbt.itemstack.tag.InfinityEnchantmentList;

public class InfinityItemTag
{
    private ItemStack stack;
    
    public InfinityItemTag(ItemStack stack) {
        this.stack = stack;
    }
    
    public InfinityEnchantmentList getEnchantmentList()
    {
        return new InfinityEnchantmentList( this );
    }
    
    public ItemStack getItemStack()
    {
        return stack;
    }
    
    public NBTTagCompound getTag()
    {
        if ( !stack.hasTagCompound() )
        {
            stack.setTagCompound( new NBTTagCompound() );
        }
        
        return stack.getTagCompound();
    }
    
    public boolean exists()
    {
        return stack.hasTagCompound();
    }
    
    public void checkEmpty()
    {
        if ( exists() && getTag().hasNoTags() )
        {
            stack.setTagCompound( null );
        }
    }
    
    public boolean getFlagHidden( Flags flag )
    {
        return stack.hasTagCompound() && getTag().hasKey( "HideFlags", NBT.TAG_INT ) && flag.hidden( getTag().getInteger( "HideFlags" ) );
    }
    
    public void switchFlag(Flags flag){
        int value = getTag().getInteger( "HideFlags" );
        value ^= flag.getDenom();
        
        if(value == 0){
            getTag().removeTag( "HideFlags" );
            checkEmpty();
        }
        else{
            getTag().setInteger( "HideFlags", value );
        }
    }
}
