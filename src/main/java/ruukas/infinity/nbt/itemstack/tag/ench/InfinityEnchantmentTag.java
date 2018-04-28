package ruukas.infinity.nbt.itemstack.tag.ench;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.nbt.NBTTagCompound;
import ruukas.infinity.nbt.itemstack.tag.InfinityEnchantmentList;

public class InfinityEnchantmentTag
{
    private final InfinityEnchantmentList enchantmentList;
    private final NBTTagCompound tag;
    
    public InfinityEnchantmentTag(InfinityEnchantmentList list, Enchantment e, short level) {
        this( list, new NBTTagCompound() );
        enchantmentList.getTag().appendTag( tag );
        
        setEnchantment( e );
        setLevel( level );
    }
    
    public InfinityEnchantmentTag(InfinityEnchantmentList list, NBTTagCompound tag) {
        enchantmentList = list;
        this.tag = tag;
    }
    
    public short getLevel()
    {
        return tag.getShort( "lvl" );
    }
    
    public InfinityEnchantmentTag setLevel( short level )
    {
        tag.setShort( "lvl", level );
        return this;
    }
    
    public short getID()
    {
        return tag.getShort( "id" );
    }
    
    public InfinityEnchantmentTag setEnchantment( Enchantment e )
    {
        tag.setShort( "id", (short) Enchantment.getEnchantmentID( e ) );
        return this;
    }
    
    public Enchantment getEnchantment()
    {
        return Enchantment.getEnchantmentByID( getID() );
    }
}
