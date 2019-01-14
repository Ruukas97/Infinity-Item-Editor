package ruukas.infinity.tab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import ruukas.infinity.Infinity;
import ruukas.infinity.data.InfinityConfig;
import ruukas.infinity.data.thevoid.VoidController;

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
        Infinity.REALM = new InfinityTabRealm( getNextID(), "realm" );
        
        if ( InfinityConfig.getIsUnavailableTabEnabled() )
        {
            Infinity.UNAVAILABLE = new InfinityTab( getNextID(), "unavailable" ) {
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
        }
        
        if ( InfinityConfig.getIsBannerTabEnabled() )
        {
            Infinity.BANNERS = new InfinityTabBanners( getNextID() );
        }
        
        if ( InfinityConfig.getIsHeadTabEnabled() )
        {
            Infinity.SKULLS = new InfinityTabSkulls( getNextID() );
        }
        
        if ( InfinityConfig.getIsThiefTabEnabled() )
        {
            Infinity.THIEF = new InfinityTabThief( getNextID() );
        }
        
        if ( InfinityConfig.getIsFireworkTabEnabled() )
        {
            Infinity.FIREWORKS = new InfinityTabFireworks( getNextID() );
        }
        
        if ( InfinityConfig.getIsVoidEnabled() )
        {
            Infinity.VOID = new InfinityTab( getNextID(), "void" ) {
                @Override
                public ItemStack getTabIconItem()
                {
                    return new ItemStack( Blocks.STAINED_GLASS, 1, 15 );
                }
                
                @Override
                public void displayAllRelevantItems( NonNullList<ItemStack> stackList )
                {
                    super.displayAllRelevantItems( stackList );
                    
                    VoidController.loadVoidToList( stackList );
                }
                
                @Override
                public boolean hasSearchBar()
                {
                    return true;
                }
            };
        }
    }
}
