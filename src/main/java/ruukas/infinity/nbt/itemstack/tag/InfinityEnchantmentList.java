package ruukas.infinity.nbt.itemstack.tag;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import ruukas.infinity.nbt.itemstack.InfinityItemTag;
import ruukas.infinity.nbt.itemstack.tag.ench.InfinityEnchantmentTag;

public class InfinityEnchantmentList
{
    private String key;
    private final InfinityItemTag itemTag;
    
    public InfinityEnchantmentList(InfinityItemTag itemTag) {
        this.itemTag = itemTag;
        if ( itemTag.getItemStack().getItem() == Items.ENCHANTED_BOOK )
        {
            key = "StoredEnchantments";
        }
        else
        {
            key = "ench";
        }
    }
    
    public InfinityEnchantmentList(ItemStack stack) {
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
    
    public InfinityEnchantmentTag getEnchantment( Enchantment e, short level )
    {
        for ( InfinityEnchantmentTag enchantment : getAll() )
        {
            if ( enchantment.getEnchantment().equals( e ) )
            {
                return enchantment;
            }
        }
        
        return new InfinityEnchantmentTag( this, e, level );
    }
    
    public InfinityEnchantmentList set( Enchantment e, short level )
    {
        getEnchantment( e, level ).setLevel( level );
        return this;
    }
    
    public InfinityEnchantmentTag[] getAll()
    {
        InfinityEnchantmentTag[] array = new InfinityEnchantmentTag[ getTag().tagCount() ];
        int i = 0;
        for ( NBTBase b : getTag() )
        {
            array[i++] = new InfinityEnchantmentTag( this, (NBTTagCompound) b );
        }
        
        checkEmpty();
        
        return array;
    }
    
    public InfinityEnchantmentList removeEnchantment( int i )
    {
        getTag().removeTag( i );
        checkEmpty();
        return this;
    }
    
    public InfinityItemTag getItemTag()
    {
        return itemTag;
    }
    
    public InfinityEnchantmentList checkEmpty()
    {
        if ( getTag().hasNoTags() )
        {
            itemTag.getTag().removeTag( key );
        }
        
        return this;
    }
}
