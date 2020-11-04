package ruukas.infinityeditor.nbt.itemstack.tag.infinity;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Constants.NBT;
import ruukas.infinityeditor.nbt.itemstack.tag.InfinityInfinityTag;

public class InfinityTextureTag
{
    private final InfinityInfinityTag infinityTag;
    
    public InfinityTextureTag(InfinityInfinityTag infinityTag) {
        this.infinityTag = infinityTag;
    }
    
    public InfinityTextureTag(ItemStack stack) {
        this( new InfinityInfinityTag( stack ) );
    }
    
    public int[] getPixels()
    {
        if ( infinityTag.exists() && infinityTag.getTag().hasKey( "texture", NBT.TAG_INT_ARRAY ) )
        {
            return infinityTag.getTag().getIntArray( "texture" );
        }
        
        return new int[ 64 ];
    }
    
    public void setPixels( int[] pixels )
    {
        if ( pixels.length == 64 )
        {
            infinityTag.getTag().setIntArray( "texture", pixels );
        }
    }
}
