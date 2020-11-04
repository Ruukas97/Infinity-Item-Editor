package ruukas.infinityeditor.nbt.itemstack.tag;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import ruukas.infinityeditor.nbt.itemstack.InfinityItemTag;

public class InfinitySkullOwnerTag
{
    private static String key = "SkullOwner";
    private final InfinityItemTag itemTag;
    
    public InfinitySkullOwnerTag(InfinityItemTag itemTag) {
        this.itemTag = itemTag;
    }
    
    public InfinitySkullOwnerTag(ItemStack stack) {
        this( new InfinityItemTag( stack ) );
    }
    
    public NBTTagCompound getTag()
    {
        if ( !itemTag.getTag().hasKey( key, NBT.TAG_COMPOUND ) )
        {
            itemTag.getTag().setTag( key, new NBTTagCompound() );
        }
        
        return itemTag.getTag().getCompoundTag( key );
    }
    
    public InfinitySkullOwnerTag setId( String uuid )
    {
        getTag().setString( "Id", uuid );
        return this;
    }
    
    public InfinitySkullOwnerTag setName( String name )
    {
        getTag().setString( "Name", name );
        return this;
    }
    
    public InfinitySkullOwnerTag setValue( String value )
    {
        
        if ( !getTag().hasKey( "Properties", NBT.TAG_COMPOUND ) )
        {
            getTag().setTag( "Properties", new NBTTagCompound() );
        }
        
        NBTTagCompound properties = getTag().getCompoundTag( "Properties" );
        
        if ( !properties.hasKey( "textures", NBT.TAG_LIST ) )
        {
            properties.setTag( "textures", new NBTTagList() );
        }
        
        NBTTagList textures = properties.getTagList( "textures", NBT.TAG_COMPOUND );
        
        if(textures.hasNoTags()){
            textures.appendTag( new NBTTagCompound() );
        }
        
        NBTTagCompound texture = textures.getCompoundTagAt( 0 );
        
        texture.setString( "Value", value );
        
        return this;
    }
}
