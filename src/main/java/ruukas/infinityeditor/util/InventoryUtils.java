package ruukas.infinityeditor.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import ruukas.infinityeditor.Infinity;

public class InventoryUtils {
	public static int countItem(InventoryPlayer inventory, Item item) {
		int count = 0;
		for (ItemStack stack : inventory.mainInventory) {
			if (stack.getItem() == item) {
				count++;
			}
		}
		return count;
	}

	public static int countItem(InventoryPlayer inventory, Item item, int meta) {
		int count = 0;
		for (ItemStack stack : inventory.mainInventory) {
			if (stack.getItem() == item && stack.getMetadata() == meta) {
				count++;
			}
		}
		return count;
	}

	public static int getEmptySlots(InventoryPlayer inventory) {
		int count = 0;
		for (ItemStack stack : inventory.mainInventory) {
			if (stack.isEmpty())
				count++;
		}
		return count;
	}

	public static int getEmptySlot(InventoryPlayer inventory) {
		int count = 0;
		for (ItemStack stack : inventory.mainInventory) {
			if (stack.isEmpty())
				break;
			count++;
		}
        
        if ( count <= 8 )
        {
            count += 36;
        }
        else if ( 36 <= count && count <= 39 )
        {
            count = 8 - (count % 4);
        }
        else if ( count == 40 )
        {
            count = 45;
        }
        return count;
	}
	
    /**
     * Called when a player uses 'pick block', calls new Entity and Block hooks.
     */
    public static void onPickBlock(RayTraceResult target, EntityPlayerSP player, World world)
    {
        ItemStack result;
        TileEntity te = null;

        if (target.typeOfHit == RayTraceResult.Type.BLOCK)
        {
            IBlockState state = world.getBlockState(target.getBlockPos());

            if (state.getBlock().isAir(state, world, target.getBlockPos()))
            {
                return;
            }

            if (state.getBlock().hasTileEntity(state))
                te = world.getTileEntity(target.getBlockPos());

            result = state.getBlock().getPickBlock(state, target, world, target.getBlockPos(), player);
        }
        else
        {
        	return;
        }

        if (result.isEmpty())
        {
            return;
        }

        if (te != null)
        {
            Minecraft.getMinecraft().storeTEInStack(result, te);
        }

        String s = GiveHelper.getStringFromItemStack( result );
        GuiScreen.setClipboardString( s );
        
        if(GuiScreen.isCtrlKeyDown()) {
            Infinity.realmController.addItemStack( player, result );
        }
    }
}
