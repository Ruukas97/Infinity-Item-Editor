package ruukas.infinity.tab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import ruukas.infinity.Infinity;
import ruukas.infinity.InfinitySettings;
import ruukas.infinity.InfinityVoid;

public abstract class InfinityTab extends CreativeTabs
{
    public InfinityTab(String label) {
        super( label );
    }
    
    public InfinityTab(int index, String label) {
        super( index, label );
    }
    
    public static void initTabs()
    {
        Infinity.UNAVAILABLE = new InfinityTab( 12, "unavailable") {
            @Override
            public ItemStack getTabIconItem()
            {
                return new ItemStack( Blocks.BARRIER );
            }
            
            @Override
            public void displayAllRelevantItems( NonNullList<ItemStack> stackList )
            {
                super.displayAllRelevantItems( stackList );
                
                stackList.add( new ItemStack( Items.SPAWN_EGG ) );
                stackList.add( new ItemStack( Items.POTIONITEM ) );
                stackList.add( new ItemStack( Items.SPLASH_POTION ) );
                stackList.add( new ItemStack( Items.LINGERING_POTION ) );
                stackList.add( new ItemStack( Items.TIPPED_ARROW ) );
                stackList.add( new ItemStack( Items.ENCHANTED_BOOK ) );
            }
        };
        
        Infinity.BANNERS = new InfinityTabBanners();
        
        Infinity.SKULLS = new InfinityTabSkulls();
        
        Infinity.THIEF = new InfinityTabThief();
        
        Infinity.REALM = new InfinityTab( 16, "realm") {
            @Override
            public ItemStack getTabIconItem()
            {
                return new ItemStack( Blocks.ENDER_CHEST );
            }
            
            @Override
            public boolean isAlignedRight()
            {
                return true;
            }
            
            @Override
            public void displayAllRelevantItems( NonNullList<ItemStack> stackList )
            {
                super.displayAllRelevantItems( stackList );
                
                stackList.addAll( Infinity.infinitySettings.getStackList() );
            }
        };
        
        Infinity.FIREWORKS = new InfinityTabFireworks();
        
        Infinity.VOID = new InfinityTab( "void") {
            @Override
            public ItemStack getTabIconItem()
            {
                return new ItemStack( Blocks.BEDROCK );
            }
            
            @Override
            public void displayAllRelevantItems( NonNullList<ItemStack> stackList )
            {
                super.displayAllRelevantItems( stackList );
                
                InfinityVoid.loadVoid( stackList );
            }
        };
        
        Infinity.infinitySettings = new InfinitySettings( Infinity.dataDir );
    }
}
