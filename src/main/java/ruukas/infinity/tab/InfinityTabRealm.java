package ruukas.infinity.tab;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import ruukas.infinity.Infinity;

public class InfinityTabRealm extends InfinityTab
{    
    public InfinityTabRealm(int index, String label) {
        super( index, label );
    }
    
    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack( Blocks.ENDER_CHEST );
    }
    
    @Override
    public void displayAllRelevantItems( NonNullList<ItemStack> stackList )
    {
        super.displayAllRelevantItems( stackList );
        
        stackList.addAll( Infinity.realmController.getStackList() );
    }
}
