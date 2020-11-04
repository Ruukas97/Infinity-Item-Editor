package ruukas.infinityeditor.gui.armorstand;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants.NBT;
import ruukas.infinityeditor.nbt.NBTHelper;

public class InventoryArmorStandEquipment implements IInventory
{
    public final NonNullList<ItemStack> armor = NonNullList.<ItemStack>withSize( 4, ItemStack.EMPTY );
    public final NonNullList<ItemStack> hands = NonNullList.<ItemStack>withSize( 2, ItemStack.EMPTY );
    private ItemStack armorStandStack;
    public EntityArmorStand entityStand;
    
    public InventoryArmorStandEquipment(ItemStack armorstand) {
        this.armorStandStack = armorstand;
        entityStand = new EntityArmorStand( Minecraft.getMinecraft().world );
        applyItemDataToMob();
    }
    
    public void applyItemDataToMob()
    {
        NBTTagCompound tag = armorStandStack.getTagCompound();
        
        if ( tag != null && tag.hasKey( "EntityTag", NBT.TAG_COMPOUND ) )
        {
            UUID uuid = entityStand.getUniqueID();
            entityStand.setUniqueId( uuid );
            entityStand.readFromNBT( tag.getCompoundTag( "EntityTag" ) );
        }
    }
    
    public static NonNullList<ItemStack> getStackList( ItemStack stack, boolean hands )
    {
        NonNullList<ItemStack> stackList = NonNullList.withSize( hands ? 2 : 4, ItemStack.EMPTY );
        
        if ( !NBTHelper.hasEntityTag( stack ) )
            return stackList;
        
        NBTTagCompound entityTag = NBTHelper.getEntityTag( stack );
        NBTTagList nbttaglist = entityTag.getTagList( hands ? "HandItems" : "ArmorItems", NBT.TAG_COMPOUND );
        
        for ( int i = 0 ; i < nbttaglist.tagCount() ; i++ )
        {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt( i );
            if ( nbttagcompound.hasNoTags() )
                continue;
            
            int j = nbttagcompound.getByte( "Slot" ) & 255;
            
            if ( j >= 0 && j < stackList.size() )
            {
                stackList.set( j, new ItemStack( nbttagcompound ) );
            }
        }
        
        return stackList;
    }
    
    public void saveToNBT( NonNullList<ItemStack> stackList, boolean hands )
    {
        NBTTagCompound tag = NBTHelper.getEntityTag( armorStandStack );
        
        NBTTagList nbttaglist = new NBTTagList();
        
        int empty = 0;
        for ( int i = 0 ; i < stackList.size() ; i++ )
        {
            ItemStack itemstack = stackList.get( i );
            
            if ( !itemstack.isEmpty() )
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte( "Slot", (byte) i );
                itemstack.writeToNBT( nbttagcompound );
                nbttaglist.appendTag( nbttagcompound );
            }
            else
            {
                nbttaglist.appendTag( new NBTTagCompound() );
                empty++;
            }
        }
        
        if ( empty == stackList.size() )
        {
            tag.removeTag( hands ? "HandItems" : "ArmorItems" );
        }
        else
        {
            tag.setTag( hands ? "HandItems" : "ArmorItems", nbttaglist );
        }
        
        NBTHelper.removeEntityTagIfEmpty( armorStandStack );
        
        applyItemDataToMob();
    }
    
    @Override
    public String getName()
    {
        return armorStandStack.getDisplayName() + " " + I18n.format( "tag.armorstand.inventory" );
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
        return 6;
    }
    
    @Override
    public boolean isEmpty()
    {
        for ( ItemStack itemstack : getStackList( armorStandStack, true ) )
        {
            if ( !itemstack.isEmpty() )
            {
                return false;
            }
        }
        
        for ( ItemStack itemstack : getStackList( armorStandStack, false ) )
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
        if ( index >= getSizeInventory() || index < 0 )
        {
            return ItemStack.EMPTY;
        }
        if ( index < 2 )
        {
            return getStackList( armorStandStack, true ).get( index );
        }
        return getStackList( armorStandStack, false ).get( index - 2 );
    }
    
    @Override
    public ItemStack decrStackSize( int index, int count )
    {
        boolean hands = index < 2;
        NonNullList<ItemStack> stackList = getStackList( armorStandStack, hands );
        ItemStack itemstack = ItemStackHelper.getAndSplit( stackList, hands ? index : index - 2, count );
        
        saveToNBT( stackList, hands );
        
        return itemstack;
    }
    
    @Override
    public ItemStack removeStackFromSlot( int index )
    {
        boolean hands = index < 2;
        NonNullList<ItemStack> stackList = getStackList( armorStandStack, hands );
        ItemStack stack = stackList.remove( hands ? index : index - 2 );
        
        saveToNBT( stackList, hands );
        
        return stack;
    }
    
    @Override
    public void setInventorySlotContents( int index, ItemStack stack )
    {
        boolean hands = index < 2;
        NonNullList<ItemStack> stackList = getStackList( armorStandStack, hands );
        stackList.set( hands ? index : index - 2, stack );
        saveToNBT( stackList, hands );
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
        saveToNBT( NonNullList.withSize( 2, ItemStack.EMPTY ), true );
        saveToNBT( NonNullList.withSize( 4, ItemStack.EMPTY ), false );
    }
}
