package ruukas.infinityeditor.nbt.itemstack.tag.custompotioneffects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import ruukas.infinityeditor.nbt.itemstack.tag.InfinityCustomPotionEffectList;

public class InfinityPotionEffectTag
{
    private final InfinityCustomPotionEffectList potionList;
    private final NBTTagCompound tag;
    
    public InfinityPotionEffectTag(InfinityCustomPotionEffectList list, PotionEffect effect) {
        this( list, new NBTTagCompound() );
        potionList.getTag().appendTag( tag );
        setEffect( effect );
    }
    
    public InfinityPotionEffectTag(InfinityCustomPotionEffectList list, NBTTagCompound tag) {
        potionList = list;
        this.tag = tag;
    }
    
    public PotionEffect getEffect()
    {
        return PotionEffect.readCustomPotionEffectFromNBT( tag );
    }
    
    public InfinityPotionEffectTag setEffect( PotionEffect effect )
    {
        effect.writeCustomPotionEffectToNBT( tag );
        return this;
    }
    
    public byte getId()
    {
        return tag.getByte( "Id" );
    }
}
