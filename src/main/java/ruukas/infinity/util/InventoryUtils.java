package ruukas.infinity.util;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

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
}
