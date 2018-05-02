package ruukas.infinity.nbt.itemstack.tag;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.common.util.Constants.NBT;
import ruukas.infinity.nbt.itemstack.InfinityItemTag;
import ruukas.infinity.nbt.itemstack.tag.custompotioneffects.InfinityPotionEffectTag;

public class InfinityCustomPotionEffectList
{
    private static String key = "CustomPotionEffects";
    private final InfinityItemTag itemTag;
    
    public InfinityCustomPotionEffectList(InfinityItemTag itemTag) {
        this.itemTag = itemTag;
    }
    
    public InfinityCustomPotionEffectList(ItemStack stack) {
        this( new InfinityItemTag( stack ) );
    }
    
    public NBTTagList getTag()
    {
        if ( !itemTag.getTag().hasKey( key, NBT.TAG_LIST ) )
        {
            itemTag.getTag().setTag( key, new NBTTagList() );
        }
        
        return itemTag.getTag().getTagList( key, NBT.TAG_COMPOUND );
    }
    
    public InfinityPotionEffectTag getEffectTag( PotionEffect t )
    {
        for ( InfinityPotionEffectTag effect : getAll() )
        {
            if ( effect.getEffect().getPotion().equals( t.getPotion() ) )
            {
                return effect;
            }
        }
        
        return new InfinityPotionEffectTag( this, t );
    }
    
    public InfinityCustomPotionEffectList set( PotionEffect t )
    {
        getEffectTag( t );
        return this;
    }
    
    public InfinityPotionEffectTag[] getAll()
    {
        InfinityPotionEffectTag[] array = new InfinityPotionEffectTag[ getTag().tagCount() ];
        int i = 0;
        for ( NBTBase b : getTag() )
        {
            array[i++] = new InfinityPotionEffectTag( this, (NBTTagCompound) b );
        }
        
        checkEmpty();
        
        return array;
    }
    
    public InfinityCustomPotionEffectList removePotionEffect( int i )
    {
        getTag().removeTag( i );
        checkEmpty();
        return this;
    }
    
    public InfinityItemTag getItemTag()
    {
        return itemTag;
    }
    
    public InfinityCustomPotionEffectList checkEmpty()
    {
        if ( getTag().hasNoTags() )
        {
            itemTag.getTag().removeTag( key );
        }
        
        return this;
    }
}
