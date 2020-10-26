package ruukas.infinity.gui.monsteregg;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class MobTagToggle extends MobTag
{
    boolean normal = false;
    
    protected MobTagToggle(String name, String key) {
        super( name, key );
    }
    
    protected MobTagToggle(String name, String key, boolean normal) {
        super( name, key );
        this.normal = normal;
    }
    
    public boolean getValue( ItemStack stack )
    {
        if ( stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey( "EntityTag", NBT.TAG_COMPOUND ) && stack.getSubCompound( "EntityTag" ).hasKey( key, NBT.TAG_BYTE ) )
        {
            return stack.getSubCompound( "EntityTag" ).getBoolean( key );
        }
        
        return false;
    }
    
    public void switchToggle( ItemStack stack )
    {
        if ( !stack.hasTagCompound() )
        {
            stack.setTagCompound( new NBTTagCompound() );
            stack.getTagCompound().setTag( "EntityTag", new NBTTagCompound() );
            stack.getSubCompound( "EntityTag" ).setBoolean( key, !normal );
        }
        else if ( !stack.getTagCompound().hasKey( "EntityTag", NBT.TAG_COMPOUND ) )
        {
            stack.getTagCompound().setTag( "EntityTag", new NBTTagCompound() );
            stack.getSubCompound( "EntityTag" ).setBoolean( key, !normal );
        }
        else if ( stack.getSubCompound( "EntityTag" ).hasKey( key, NBT.TAG_BYTE ) )
        {
            stack.getSubCompound( "EntityTag" ).setBoolean( key, !stack.getSubCompound( "EntityTag" ).getBoolean( key ) );
        }
        else
        {
            stack.getSubCompound( "EntityTag" ).setBoolean( key, true );
        }
    }
    
    public void setValue( boolean value, ItemStack stack )
    {
        if ( !stack.hasTagCompound() )
        {
            stack.setTagCompound( new NBTTagCompound() );
        }
        
        if ( !stack.getTagCompound().hasKey( "EntityTag", NBT.TAG_COMPOUND ) )
        {
            stack.getTagCompound().setTag( "EntityTag", new NBTTagCompound() );
        }
        
        stack.getSubCompound( "EntityTag" ).setBoolean( key, value );
    }
}
