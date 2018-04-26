package ruukas.infinity.gui.monsteregg;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class MobTagList extends MobTag
{
    int length, normal;
    boolean isNoTagDifferent;
    
    protected MobTagList(String name, String key, int length) {
        this( name, key, length, false );
    }
    
    protected MobTagList(String name, String key, int length, boolean isNoTagDifferent) {
        super( name, key );
        this.length = length;
        this.isNoTagDifferent = isNoTagDifferent;
        this.normal = 0;
    }
    
    protected MobTagList(String name, String key, int length, int normal) {
        this( name, key, length, false );
        this.normal = normal;
    }
    
    public int getValue( ItemStack stack )
    {
        if ( stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey( "EntityTag", NBT.TAG_COMPOUND ) && stack.getSubCompound( "EntityTag" ).hasKey( key, NBT.TAG_INT ) )
        {
            return stack.getSubCompound( "EntityTag" ).getInteger( key );
        }
        
        return isNoTagDifferent ? -1 : normal;
    }
    
    public void nextValue( ItemStack stack )
    {
        boolean startFromZero = false;
        if ( !stack.hasTagCompound() )
        {
            stack.setTagCompound( new NBTTagCompound() );
            startFromZero = true;
        }
        
        if ( !stack.getTagCompound().hasKey( "EntityTag", NBT.TAG_COMPOUND ) )
        {
            stack.getTagCompound().setTag( "EntityTag", new NBTTagCompound() );
            startFromZero = true;
        }
        
        if ( startFromZero )
        {
            stack.getSubCompound( "EntityTag" ).setInteger( key, 0 );
            return;
        }
        
        int currentValue = getValue( stack );
        
        if ( currentValue >= length - 1 )
        {
            setValue( isNoTagDifferent ? -1 : 0, stack );
        }
        else
        {
            setValue( currentValue + 1, stack );
        }
    }
    
    public void setValue( int value, ItemStack stack )
    {
        if ( !stack.hasTagCompound() )
        {
            stack.setTagCompound( new NBTTagCompound() );
        }
        
        if ( !stack.getTagCompound().hasKey( "EntityTag", NBT.TAG_COMPOUND ) )
        {
            stack.getTagCompound().setTag( "EntityTag", new NBTTagCompound() );
        }
        
        if ( value < 0 )
        {
            stack.getSubCompound( "EntityTag" ).removeTag( key );
        }
        else
        {
            stack.getSubCompound( "EntityTag" ).setInteger( key, value );
        }
    }
}
