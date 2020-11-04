package ruukas.infinityeditor.gui.monsteregg;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants.NBT;

public class MobTagSlider extends MobTag
{
    
    protected float min, max, step, normal;
    
    public MobTagSlider(String name, String key, float min, float max, float step) {
        this( name, key, min, max, step, min );
    }
    
    public MobTagSlider(String name, String key, float min, float max, float step, float normal) {
        super( name, key );
        this.setMin( min );
        this.setMax( max );
        this.setStep( step );
    }
    
    public float getFloat( ItemStack stack )
    {
        if ( stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey( "EntityTag", NBT.TAG_COMPOUND ) && stack.getSubCompound( "EntityTag" ).hasKey( key, NBT.TAG_FLOAT ) )
        {
            return stack.getSubCompound( "EntityTag" ).getFloat( key );
        }
        
        return this.min;
    }
    
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
        
        stack.getSubCompound( "EntityTag" ).setFloat( key, f );
    }
    
    public String getKey()
    {
        return key;
    }
    
    public float getMin()
    {
        return min;
    }
    
    public void setMin( float min )
    {
        this.min = min;
    }
    
    public float getMax()
    {
        return max;
    }
    
    public void setMax( float max )
    {
        this.max = max;
    }
    
    public float getStep()
    {
        return step;
    }
    
    public void setStep( float step )
    {
        this.step = step;
    }
    
    public float normalizeValue( float value )
    {
        return MathHelper.clamp( (this.snapToStepClamp( value ) - this.min) / (this.max - this.min), 0.0F, 1.0F );
    }
    
    public float denormalizeValue( float value )
    {
        return this.snapToStepClamp( this.min + (this.max - this.min) * MathHelper.clamp( value, 0.0F, 1.0F ) );
    }
    
    public float snapToStepClamp( float value )
    {
        value = this.snapToStep( value );
        return MathHelper.clamp( value, this.min, this.max );
    }
    
    private float snapToStep( float value )
    {
        if ( this.step > 0.0F )
        {
            value = this.step * (float) Math.round( value / this.step );
        }
        
        return value;
    }
}
