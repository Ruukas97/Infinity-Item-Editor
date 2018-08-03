package ruukas.infinity;

import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import ruukas.infinity.tab.InfinityTabBanners;
import ruukas.infinity.tab.InfinityTabFireworks;
import ruukas.infinity.tab.InfinityTabSkulls;
import ruukas.infinity.tab.InfinityTabThief;

@Mod( modid = Infinity.MODID, name = Infinity.NAME, version = Infinity.VERSION, clientSideOnly = true )
public class Infinity
{
    public static final String MODID = "infinity";
    public static final String NAME = "Infinity Item Editor";
    public static final String VERSION = "0.11pre1";
    
    public static Logger logger;
    
    public static KeyBinding keybind;
    public static KeyBinding keybindCopy;
    
    public static CreativeTabs UNAVAILABLE, BANNERS, SKULLS, FIREWORKS, THIEF;
    
    // TODO
    // ADD Config
    // - Chose between different preset interface colors or vanilla style
    // Allow in-game disabling and reloading
    // Implement code for displaying armor stands items as models (I've already done the code for this on laptop)
    // Support forge update json
    // remove "this." from code where it's unneeded for better readability
    // Special characters in chat (GuiUtils.UNDO_CHAR)
    
    // Attributes GUI:
    // EntityZombie SPAWN_REINFORCEMENTS_CHANCE
    // AbstractHorse JUMP_STRENGTH
    // EntityPlayer REACH_DISTANCE is from forge
    
    // Item GUI:
    // Add picker for ID and Meta (list that shows items)
    // Set "maxDamage" to lowest value which exists (has texture?)
    // Tab complete
    // Remove quotes around field names in pretty printing
    // Change display of pretty printing and wrap lines
    // id field doesn't work very well with numbers
    // Do not allow id 0/Air
    
    // Monster Egg gui:
    // Implement more NBT
    // Player picker GUI for tamed etc
    // Support attributes
    
    // Head gui:
    // Add list to pick from players on the server
    
    // Export GUI
    // Export as give command
    // Export to clipboard
    // Export in FTBUtil format
    
    // Armor Stand gui:
    // Armor stand rotation for viewing
    // Disable hand slots, if arms are disabled - not sure if it's a good idea or not
    
    // Color GUI
    // Are there other items than leather armor that support colors? Potions
    // Color pallete (save colors)
    
    // Rendering
    // Custom render in hand for armor stand and spawn eggs
    
    // Enchanting
    // Book in background
    // Write high levels with roman numerals - https://stackoverflow.com/questions/12967896
    // Support enchanted_book
    // Sort circle alphabetically
    
    // Potions
    // Show particles toggle button (See PotionEffect.class)
    // Curative item (e.g. which item cures the effect - default is milk_bucket)
    // Vanilla potion types
    
    // Gui overlay:
    // Similar to the right click in NBT explorer
    // Will feature things such as item picker!
    
    // Hide flags:
    // Add button to clear lore
    
    // CHANGELOG:
    // Made U usable inside a gui
    // Added head collection gui
    // Added lore gui
    // Can now use U with empty hand on multiplayer
    // Changed "Infinity - Heads" tab icon
    // Fix gui title of banner maker
    // Fixed crash related to unknown potion and enchanting id's
    // Fixed last lore line didn't have clear button
    
    @EventHandler
    public void preInit( FMLPreInitializationEvent event )
    {
        logger = event.getModLog();
    }
    
    @EventHandler
    public void init( FMLInitializationEvent event )
    {
        // At the moment, it's alright to keep this part here, as the mod won't be loaded on serverside.
        keybind = new KeyBinding( "key.infinity.desc", Keyboard.KEY_U, "key.infinity.category" );
        ClientRegistry.registerKeyBinding( keybind );
        
        keybindCopy = new KeyBinding( "key.infinitycopy.desc", Keyboard.KEY_V, "key.infinity.category" );
        ClientRegistry.registerKeyBinding( keybindCopy );
        
        UNAVAILABLE = new CreativeTabs( "unavailable") {
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
            }
        };
        
        BANNERS = new InfinityTabBanners();
        
        SKULLS = new InfinityTabSkulls();
        
        FIREWORKS = new InfinityTabFireworks();
        
        THIEF = new InfinityTabThief();
    }
    
    @EventHandler
    public void postInit( FMLPostInitializationEvent event )
    {
        /*
         * Goes through each registered item and block that has been registered and adds them to UNAVAILABLE, if they haven't been assigned a tab. This gives the player access to item that are not unavailable such as barriers and command blocks, and potentially items that are added by other mods too.
         * 
         * It's also possible to go through the list, whenever the tab is opened, but this is better for performance reasons.
         */
        for ( Item item : Item.REGISTRY )
        {
            if ( item != null && item != Items.AIR && item.getCreativeTab() == null && item != Items.ENCHANTED_BOOK )
            {
                item.setCreativeTab( UNAVAILABLE );
                logger.info( "Item: " + item.getUnlocalizedName() + " was not added to a tab. Adding it to Unavailable." ); // Perhaps it should only print a count of how many items were added.
            }
        }
        
        for ( Block block : Block.REGISTRY )
        {
            if ( block != null && block != Blocks.AIR && block.getCreativeTabToDisplayOn() == null )
            {
                block.setCreativeTab( UNAVAILABLE );
                logger.info( "Block: " + block.getUnlocalizedName() + " was not added to a tab. Adding it to Unavailable." );
            }
        }
    }
}
