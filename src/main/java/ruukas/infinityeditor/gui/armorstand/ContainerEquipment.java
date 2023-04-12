package ruukas.infinityeditor.gui.armorstand;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinityeditor.InfinityEditor;
import ruukas.infinityeditor.gui.HelperGui;

public class ContainerEquipment extends Container
{
    private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] { EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET };
    public final InventoryArmorStandEquipment inventory;
    
    public ContainerEquipment(InventoryArmorStandEquipment equipmentInventory) {
        this.inventory = equipmentInventory;
        
        EntityPlayer player = Minecraft.getMinecraft().player;
        InventoryPlayer playerInventory = HelperGui.getInventoryPlayerCopy( player.inventory );
        
        for ( int k = 0 ; k < 4 ; ++k )
        {
            final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
            // Player
            this.addSlotToContainer( new Slot( playerInventory, 39 - k, 8, 8 + k * 18 ) {
                
                public int getSlotStackLimit()
                {
                    return 1;
                }
                
                public boolean isItemValid( ItemStack stack )
                {
                    return stack.getItem().isValidArmor( stack, entityequipmentslot, player );
                }
                
                public boolean canTakeStack( EntityPlayer playerIn )
                {
                    ItemStack itemstack = this.getStack();
                    return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.canTakeStack(playerIn);
                }
                
                @Nullable
                @SideOnly( Side.CLIENT )
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
                }
            } );
            
            // Other
            this.addSlotToContainer( new Slot( equipmentInventory, 5 - k, 152, 8 + k * 18 ) {
                
                public int getSlotStackLimit()
                {
                    return 1;
                }
                
                public boolean isItemValid( ItemStack stack )
                {
                    return stack.getItem().isValidArmor( stack, entityequipmentslot, player );
                }
                
                public boolean canTakeStack( EntityPlayer playerIn )
                {
                    ItemStack itemstack = this.getStack();
                    return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.canTakeStack(playerIn);
                }
                
                @Nullable
                @SideOnly( Side.CLIENT )
                public String getSlotTexture()
                {
                    return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
                }
            } );
        }
        
        for ( int l = 0 ; l < 3 ; ++l )
        {
            for ( int j1 = 0 ; j1 < 9 ; ++j1 )
            {
                this.addSlotToContainer( new Slot( playerInventory, j1 + (l + 1) * 9, 8 + j1 * 18, 84 + l * 18 ) );
            }
        }
        
        for ( int i1 = 0 ; i1 < 9 ; ++i1 )
        {
            this.addSlotToContainer( new Slot( playerInventory, i1, 8 + i1 * 18, 142 ) );
        }
        
        // Off-hand
        this.addSlotToContainer( new Slot( playerInventory, 40, 77, 62 ) {
            @SideOnly( Side.CLIENT )
            public String getSlotTexture()
            {
                return "minecraft:items/empty_armor_slot_shield";
            }
        } );
        
        // Equipment hands
        this.addSlotToContainer( new Slot( equipmentInventory, 0, 83, 8 ) {
            @SideOnly( Side.CLIENT )
            public String getSlotTexture()
            {
                return InfinityEditor.MODID + ":items/empty_armor_slot_sword";
            }
        } );
        
        this.addSlotToContainer( new Slot( equipmentInventory, 1, 83, 26 ) {
            @SideOnly( Side.CLIENT )
            public String getSlotTexture()
            {
                return "minecraft:items/empty_armor_slot_shield";
            }
        } );
    }
    
    /**
     * Determines whether supplied player can use this container
     */
    public boolean canInteractWith( EntityPlayer playerIn )
    {
        return true;
    }
    
    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player inventory and the other inventory(s).
     */
    public ItemStack transferStackInSlot( EntityPlayer playerIn, int index )
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get( index );
        
        if ( slot != null && slot.getHasStack() )
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack( itemstack );
            
            if ( index == 0 )
            {
                if ( !this.mergeItemStack( itemstack1, 9, 45, true ) )
                {
                    return ItemStack.EMPTY;
                }
                
                slot.onSlotChange( itemstack1, itemstack );
            }
            else if ( index >= 1 && index < 5 )
            {
                if ( !this.mergeItemStack( itemstack1, 9, 45, false ) )
                {
                    return ItemStack.EMPTY;
                }
            }
            else if ( index >= 5 && index < 9 )
            {
                if ( !this.mergeItemStack( itemstack1, 9, 45, false ) )
                {
                    return ItemStack.EMPTY;
                }
            }
            else if ( entityequipmentslot.getSlotType() == EntityEquipmentSlot.Type.ARMOR && !this.inventorySlots.get( 8 - entityequipmentslot.getIndex() ).getHasStack() )
            {
                int i = 8 - entityequipmentslot.getIndex();
                
                if ( !this.mergeItemStack( itemstack1, i, i + 1, false ) )
                {
                    return ItemStack.EMPTY;
                }
            }
            else if ( entityequipmentslot == EntityEquipmentSlot.OFFHAND && !this.inventorySlots.get( 45 ).getHasStack() )
            {
                if ( !this.mergeItemStack( itemstack1, 45, 46, false ) )
                {
                    return ItemStack.EMPTY;
                }
            }
            else if ( index >= 9 && index < 36 )
            {
                if ( !this.mergeItemStack( itemstack1, 36, 45, false ) )
                {
                    return ItemStack.EMPTY;
                }
            }
            else if ( index >= 36 && index < 45 )
            {
                if ( !this.mergeItemStack( itemstack1, 9, 36, false ) )
                {
                    return ItemStack.EMPTY;
                }
            }
            else if ( !this.mergeItemStack( itemstack1, 9, 45, false ) )
            {
                return ItemStack.EMPTY;
            }
            
            if ( itemstack1.isEmpty() )
            {
                slot.putStack( ItemStack.EMPTY );
            }
            else
            {
                slot.onSlotChanged();
            }
            
            if ( itemstack1.getCount() == itemstack.getCount() )
            {
                return ItemStack.EMPTY;
            }
            
            ItemStack itemstack2 = slot.onTake( playerIn, itemstack1 );
            
            if ( index == 0 )
            {
                playerIn.dropItem( itemstack2, false );
            }
        }
        
        return itemstack;
    }
}
