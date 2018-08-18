package ruukas.infinity.tab;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import ruukas.infinity.Infinity;

public class InfinityTabRealm extends InfinityTab
{
    private final boolean allignedRight;
    
    public InfinityTabRealm(int index, String label, boolean allignedRight) {
        super( index, label );
        this.allignedRight = allignedRight;
    }
    
    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack( Blocks.ENDER_CHEST );
    }
    
    @Override
    public boolean isAlignedRight()
    {
        return this.allignedRight;
    }
    
    @Override
    public void displayAllRelevantItems( NonNullList<ItemStack> stackList )
    {
        super.displayAllRelevantItems( stackList );
        
        stackList.addAll( Infinity.infinitySettings.getStackList() );
    }
}
