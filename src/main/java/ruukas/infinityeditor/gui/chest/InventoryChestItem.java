package ruukas.infinityeditor.gui.chest;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class InventoryChestItem implements IInventory
{
    ItemStack chest;
    
    public InventoryChestItem(ItemStack chest) {
        this.chest = chest;
    }
    
    public static NonNullList<ItemStack> getStackList( ItemStack stack )
    {
        NonNullList<ItemStack> stackList = NonNullList.withSize( 27, ItemStack.EMPTY );
        
        ItemStackHelper.loadAllItems( stack.getOrCreateSubCompound( "BlockEntityTag" ), stackList );
        
        return stackList;
    }
    
    public void saveToNBT( NonNullList<ItemStack> stackList )
    {
        ItemStackHelper.saveAllItems( chest.getOrCreateSubCompound( "BlockEntityTag" ), stackList );
    }
    
    @Override
    public String getName()
    {
        return chest.getDisplayName();
    }
    
    @Override
    public boolean hasCustomName()
    {
        return false;
    }
    
    @Override
    public ITextComponent getDisplayName()
    {
        return new TextComponentString( this.getName() );
    }
    
    @Override
    public int getSizeInventory()
    {
        return 27;
    }
    
    @Override
    public boolean isEmpty()
    {
        for ( ItemStack itemstack : getStackList( chest ) )
        {
            if ( !itemstack.isEmpty() )
            {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public ItemStack getStackInSlot( int index )
    {
        if ( index >= getSizeInventory() )
        {
            return ItemStack.EMPTY;
        }
        return getStackList( chest ).get( index );
    }
    
    @Override
    public ItemStack decrStackSize( int index, int count )
    {
        NonNullList<ItemStack> stackList = getStackList( chest );
        ItemStack itemstack = ItemStackHelper.getAndSplit( stackList, index, count );
        
        if ( !itemstack.isEmpty() )
        {
            saveToNBT( stackList );
        }
        
        return itemstack;
    }
    
    @Override
    public ItemStack removeStackFromSlot( int index )
    {
        NonNullList<ItemStack> stackList = getStackList( chest );
        ItemStack stack = stackList.remove( index );
        markDirty();
        
        return stack;
    }
    
    @Override
    public void setInventorySlotContents( int index, ItemStack stack )
    {
        NonNullList<ItemStack> stackList = getStackList( chest );
        stackList.set( index, stack );
        saveToNBT( stackList );
    }
    
    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }
    
    @Override
    public void markDirty()
    {
    }
    
    @Override
    public boolean isUsableByPlayer( EntityPlayer player )
    {
        return true;
    }
    
    @Override
    public void openInventory( EntityPlayer player )
    {
        
    }
    
    @Override
    public void closeInventory( EntityPlayer player )
    {
        // int slot = player.inventory.currentItem;
        // Minecraft.getMinecraft().playerController.sendSlotPacket(chest, 36 + slot);
    }
    
    @Override
    public boolean isItemValidForSlot( int index, ItemStack stack )
    {
        return true;
    }
    
    @Override
    public int getField( int id )
    {
        return 0;
    }
    
    @Override
    public void setField( int id, int value )
    {
        
    }
    
    @Override
    public int getFieldCount()
    {
        return 0;
    }
    
    @Override
    public void clear()
    {
        saveToNBT( NonNullList.withSize( getSizeInventory(), ItemStack.EMPTY ) );
    }
}
