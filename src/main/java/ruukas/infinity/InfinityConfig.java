package ruukas.infinity;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import ruukas.infinity.gui.HelperGui;

@Config( modid = Infinity.MODID, name = "InfinityItemEditor", category = "general" )
public class InfinityConfig
{
    public static boolean itemGuiSidebar = false;
    public static boolean voidTab = true;
    public static boolean voidAddNotification = false;
    
    @Ignore
    public static int MAIN_COLOR = HelperGui.getColorFromRGB( 255, 150, 0, 200 );
    @Ignore
    public static int ALT_COLOR = HelperGui.getColorFromRGB( 255, 50, 20, 75 );
    @Ignore
    public static int CONTRAST_COLOR = HelperGui.getColorFromRGB( 255, 0, 100, 255 );
    
    public static void setItemSidebar( boolean value )
    {
        itemGuiSidebar = value;
        ConfigManager.sync( Infinity.MODID, Type.INSTANCE );
    }
    
    public static boolean getItemSidebar()
    {
        return itemGuiSidebar;
    }
    
    public static boolean getIsVoidEnabled()
    {
        return voidTab;
    }
}
