package ruukas.infinity.nbt.itemstack.tag;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import ruukas.infinity.nbt.itemstack.InfinityItemTag;
import ruukas.infinity.nbt.itemstack.tag.attributemodifiers.InfinityAttributeModifierTag;

public class InfinityAttributeModifierList
{
    private static String key = "AttributeModifiers";
    private final InfinityItemTag itemTag;
    
    public InfinityAttributeModifierList(InfinityItemTag itemTag) {
        this.itemTag = itemTag;
    }
    
    public InfinityAttributeModifierList(ItemStack stack) {
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
    
    public InfinityAttributeModifierTag addModifierTag( InfinityAttributeModifierTag modifierTag )
    {
        getTag().appendTag( modifierTag.getTag() );
        return modifierTag;
    }
    
    public InfinityAttributeModifierTag addModifier( AttributeModifier modifier )
    {
        NBTTagCompound tag = SharedMonsterAttributes.writeAttributeModifierToNBT( modifier );
        InfinityAttributeModifierTag modTag = new InfinityAttributeModifierTag( this, tag );
        getTag().appendTag( tag );
        return modTag;
    }

    
    public InfinityAttributeModifierTag[] getAll()
    {
        InfinityAttributeModifierTag[] array = new InfinityAttributeModifierTag[ getTag().tagCount() ];
        int i = 0;
        for ( NBTBase b : getTag() )
        {
            array[i++] = new InfinityAttributeModifierTag( this, (NBTTagCompound) b );
        }
        
        checkEmpty();
        
        return array;
    }
    
    public InfinityAttributeModifierList removeModifier( int i )
    {
        getTag().removeTag( i );
        checkEmpty();
        return this;
    }
    
    public InfinityItemTag getItemTag()
    {
        return itemTag;
    }
    
    public InfinityAttributeModifierList checkEmpty()
    {
        if ( getTag().hasNoTags() )
        {
            itemTag.getTag().removeTag( key );
        }
        
        return this;
    }
}
