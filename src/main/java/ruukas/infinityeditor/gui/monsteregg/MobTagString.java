package ruukas.infinityeditor.gui.monsteregg;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class MobTagString extends MobTag
{
    protected MobTagString(String name, String key) {
        super( name, key );
    }
    
    public String getValue( ItemStack stack )
    {
        if ( stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey( "EntityTag", NBT.TAG_COMPOUND ) && stack.getSubCompound( "EntityTag" ).hasKey( key, NBT.TAG_BYTE ) )
        {
            return stack.getSubCompound( "EntityTag" ).getString( key );
        }
        
        return "";
    }
    
    public void setValue( String value, ItemStack stack )
    {
        if ( value == null || value.length() < 1 )
        {
            return;
        }
        
        if ( !stack.hasTagCompound() )
        {
            stack.setTagCompound( new NBTTagCompound() );
        }
        
        if ( !stack.getTagCompound().hasKey( "EntityTag", NBT.TAG_COMPOUND ) )
        {
            stack.getTagCompound().setTag( "EntityTag", new NBTTagCompound() );
        }
        
        stack.getSubCompound( "EntityTag" ).setString( key, value );
    }
}
