package ruukas.infinityeditor.tab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import ruukas.infinityeditor.InfinityEditor;
import ruukas.infinityeditor.data.InfinityConfig;
import ruukas.infinityeditor.data.thevoid.VoidController;

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
        int tabs = 7
                - (InfinityConfig.getIsVoidEnabled() ? 0 : 1)
                - (InfinityConfig.getIsUnavailableTabEnabled() ? 0 : 1)
                - (InfinityConfig.getIsBannerTabEnabled() ? 0 : 1)
                - (InfinityConfig.getIsHeadTabEnabled() ? 0 : 1)
                - (InfinityConfig.getIsThiefTabEnabled() ? 0 : 1)
                - (InfinityConfig.getIsFireworkTabEnabled() ? 0 : 1);
        
        int id = getNextID();
        
        boolean successRealm = false;
        int foundId = id;
        
        for ( int i = foundId ; i < foundId + tabs ; i++ )
        {
            if ( (i - 16) % 5 == 0 )
            {
                foundId = i;
                successRealm = true;
                break;
            }
        }
        
        InfinityEditor.REALM = new InfinityTabRealm( successRealm ? foundId : id++, "realm", successRealm );
        
        if ( InfinityConfig.getIsUnavailableTabEnabled() )
        {
            InfinityEditor.UNAVAILABLE = new InfinityTab( id >= foundId ? 1 + id++ : id++, "unavailable" ) {
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
            InfinityEditor.BANNERS = new InfinityTabBanners( id >= foundId ? 1 + id++ : id++ );
        }
        
        if ( InfinityConfig.getIsHeadTabEnabled() )
        {
            InfinityEditor.SKULLS = new InfinityTabSkulls( id >= foundId ? 1 + id++ : id++ );
        }
        
        if ( InfinityConfig.getIsThiefTabEnabled() )
        {
            InfinityEditor.THIEF = new InfinityTabThief( id >= foundId ? 1 + id++ : id++ );
        }
        
        if ( InfinityConfig.getIsFireworkTabEnabled() )
        {
            InfinityEditor.FIREWORKS = new InfinityTabFireworks( id >= foundId ? 1 + id++ : id++ );
        }
        
        if ( InfinityConfig.getIsVoidEnabled() )
        {
            InfinityEditor.VOID = new InfinityTab( id >= foundId ? 1 + id++ : id++, "void" ) {
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
