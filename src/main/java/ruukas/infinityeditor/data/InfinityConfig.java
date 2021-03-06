package ruukas.infinityeditor.data;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.Type;
import ruukas.infinityeditor.InfinityEditor;
import ruukas.infinityeditor.gui.HelperGui;
import net.minecraftforge.common.config.ConfigManager;

@Config( modid = InfinityEditor.MODID, name = "InfinityItemEditor" )
public class InfinityConfig
{
	@Ignore
    public static boolean itemGuiSidebar = false;
    public static boolean voidTab = true;
    public static boolean voidAddNotification = false;
    public static boolean voidTabHideHeads = false;
        
    public static boolean unavailableTab = true;
    public static boolean bannerTab = true;
    public static boolean headTab = true;
    public static boolean thiefTab = true;
    public static boolean fireworkTab = true;
    
    @Ignore
    public static int MAIN_COLOR = HelperGui.getColorFromRGB( 255, 150, 0, 200 );
    @Ignore
    public static int ALT_COLOR = HelperGui.getColorFromRGB( 255, 50, 20, 75 );
    @Ignore
    public static int CONTRAST_COLOR = HelperGui.getColorFromRGB( 255, 0, 100, 255 );
    
    public static void setItemSidebar( boolean value )
    {
        itemGuiSidebar = value;
        ConfigManager.sync( InfinityEditor.MODID, Type.INSTANCE );
    }
    
    public static boolean getItemSidebar()
    {
        return itemGuiSidebar;
    }
    
    public static boolean getIsVoidEnabled()
    {
        return voidTab;
    }
    
    public static boolean getIsUnavailableTabEnabled()
    {
        return unavailableTab;
    }
    
    public static boolean getIsBannerTabEnabled()
    {
        return bannerTab;
    }
    
    public static boolean getIsHeadTabEnabled()
    {
        return headTab;
    }
    
    public static boolean getIsThiefTabEnabled()
    {
        return thiefTab;
    }
    
    public static boolean getIsFireworkTabEnabled()
    {
        return fireworkTab;
    }
}
