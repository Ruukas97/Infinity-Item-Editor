package ruukas.infinity.data.thevoid;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants.NBT;

public class VoidElement
{
    private ItemStack stack;
    private NBTTagList uuids = null;
    
    public VoidElement(ItemStack stack) {
        this.stack = stack;
    }
    
    public ItemStack getStack()
    {
        return stack;
    }
    
    public static VoidElement readFromNBT( NBTTagCompound tag )
    {
        // TODO update in few versions to no longer support old save format
        ItemStack readStack = new ItemStack( tag );
        NBTTagList ids = null;
        
        if ( readStack.isEmpty() )
        {
            readStack = new ItemStack( tag.getCompoundTag( "stack" ) );
            ids = tag.getTagList( "uuids", NBT.TAG_STRING );
        }
        
        VoidElement e = new VoidElement( readStack );
        if ( ids != null && !ids.hasNoTags() )
        {
            e.uuids = ids;
        }
        return e;
    }
    
    public NBTTagCompound writeToNBT( NBTTagCompound tag )
    {
        tag.setTag( "stack", stack.writeToNBT( new NBTTagCompound() ) );
        
        if ( uuids != null )
        {
            tag.setTag( "uuids", uuids );
        }
        
        return tag;
    }
    
    public boolean hasUUID( String id )
    {
        if ( uuids == null )
        {
            return false;
        }
        
        for ( NBTBase tag : uuids )
        {
            if ( id.equals( ((NBTTagString) tag).getString() ) )
            {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean addUUID( String id, boolean doCheck )
    {
        if ( id == null || id.isEmpty() || (doCheck && hasUUID( id )) )
        {
            return false;
        }
        
        if ( uuids == null )
        {
            uuids = new NBTTagList();
        }
        
        uuids.appendTag( new NBTTagString( id ) );
        return true;
    }
}
