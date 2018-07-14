package ruukas.infinity.nbt.itemstack.tag.attributemodifiers;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.nbt.NBTTagCompound;
import ruukas.infinity.nbt.itemstack.tag.InfinityAttributeModifierList;

public class InfinityAttributeModifierTag
{
    private final InfinityAttributeModifierList attributeList;
    private final NBTTagCompound tag;
    
    private static final String[] slots = new String[] { "mainhand", "offhand", "head", "chest", "legs", "feet" };
    
    public InfinityAttributeModifierTag(InfinityAttributeModifierList list, AttributeModifier m) {
        this( list, SharedMonsterAttributes.writeAttributeModifierToNBT( m ) );
        tag.setString( "AttributeName", getName() );
        attributeList.getTag().appendTag( tag );
    }
    
    public InfinityAttributeModifierTag(InfinityAttributeModifierList list, NBTTagCompound tag) {
        attributeList = list;
        this.tag = tag;
    }
    
    public NBTTagCompound getTag()
    {
        return this.tag;
    }
    
    public String getAttributeName()
    {
        return tag.getString( "AttributeName" );
    }
    
    public String getName()
    {
        return tag.getString( "Name" );
    }
    
    public AttributeModifier getAttributeModifier()
    {
        return SharedMonsterAttributes.readAttributeModifierFromNBT( tag );
    }
    
    public void setOperation( int operation )
    {
        tag.setInteger( "Operation", operation );
    }
    
    public int getOperation()
    {
        return tag.hasKey( "Operation" ) ? tag.getInteger( "Operation" ) : 0;
    }
    
    public void setSlot( int slot )
    {
        if ( slot > 0 && slot < 7 )
        {
            tag.setString( "Slot", slots[slot] );
        }
        else
        {
            tag.removeTag( "Slot" );
        }
    }
    
    public String getSlot()
    {
        return tag.hasKey( "Slot" ) ? tag.getString( "Slot" ) : "any";
    }
    
    public String getDisplayString()
    {
        return I18n.format( "attribute.name." + getName() ) + " " + new String[] { "+", "*", "**" }[getOperation() % 3] + getAmount() + " (" + getSlot() + ")";
    }

    private double getAmount()
    {
        return tag.hasKey( "Amount" ) ? tag.getDouble( "Amount" ) : 0;
    }
}
