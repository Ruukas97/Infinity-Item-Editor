package ruukas.infinityeditor.nbt.itemstack.tag;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import ruukas.infinityeditor.nbt.itemstack.InfinityItemTag;

public class InfinityBlockEntityTag
{
    private static final String key = "BlockEntityTag";
    private final InfinityItemTag itemTag;
    
    public InfinityBlockEntityTag(InfinityItemTag itemTag) {
        this.itemTag = itemTag;
    }
    
    public InfinityBlockEntityTag(ItemStack stack) {
        this( new InfinityItemTag( stack ) );
    }
    
    public boolean exists(){
        return itemTag.exists() && itemTag.getTag().hasKey( key, NBT.TAG_COMPOUND );
    }
    
    public NBTTagCompound getTag()
    {
        if ( !itemTag.getTag().hasKey( key, NBT.TAG_COMPOUND ) )
        {
            itemTag.getTag().setTag( key, new NBTTagCompound() );
        }
        
        return itemTag.getTag().getCompoundTag( key );
    }
    
    public InfinityItemTag getItemTag()
    {
        return itemTag;
    }
    
    public InfinityBlockEntityTag checkEmpty()
    {
        if ( getTag().hasNoTags() )
        {
            itemTag.getTag().removeTag( key );
            itemTag.checkEmpty();
        }
        
        return this;
    }
}
