package ruukas.infinity;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;

@Config( modid = Infinity.MODID, name = "InfinityItemEditor" )
public class InfinityConfig
{
    public static boolean itemGuiSidebar = false;
    
    public static void setItemSidebar( boolean value )
    {
        itemGuiSidebar = value;
        ConfigManager.sync( Infinity.MODID, Type.INSTANCE );
    }
    
    public static boolean getItemSidebar()
    {
        return itemGuiSidebar;
    }
}
