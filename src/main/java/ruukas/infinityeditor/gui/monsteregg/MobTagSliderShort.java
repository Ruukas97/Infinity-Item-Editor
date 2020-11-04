package ruukas.infinityeditor.gui.monsteregg;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public class MobTagSliderShort extends MobTagSlider
{
    public MobTagSliderShort(String name, String key, float min, float max, float step, float normal) {
        super( name, key, min, max, step, normal );
    }
    
    public MobTagSliderShort(String name, String key, float min, float max, float step) {
        super( name, key, min, max, step );
    }
    
    public short getShort( ItemStack stack )
    {
        if ( stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey( "EntityTag", NBT.TAG_COMPOUND ) && stack.getSubCompound( "EntityTag" ).hasKey( key, NBT.TAG_SHORT ) )
        {
            return stack.getSubCompound( "EntityTag" ).getShort( key );
        }
        
        return (short) this.min;
    }
    
    @Override
    public float getFloat( ItemStack stack )
    {
        if ( stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey( "EntityTag", NBT.TAG_COMPOUND ) && stack.getSubCompound( "EntityTag" ).hasKey( key, NBT.TAG_SHORT ) )
        {
            return stack.getSubCompound( "EntityTag" ).getShort( key );
        }
        
        return super.getFloat( stack );
    }
    
    @Override
    public void setValue( ItemStack stack, float f )
    {
        if ( stack == null )
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
        
        stack.getSubCompound( "EntityTag" ).setShort( key, (short) f );
    }
}
