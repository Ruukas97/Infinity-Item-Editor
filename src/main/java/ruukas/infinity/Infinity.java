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
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod( modid = Infinity.MODID, name = Infinity.NAME, version = Infinity.VERSION, clientSideOnly = true )
public class Infinity
{
    public static final String MODID = "infinity";
    public static final String NAME = "Infinity Item Editor";
    public static final String VERSION = "0.4";
    
    public static Logger logger;
    
    public static KeyBinding keybind;
    
    // TODO
    // ADD Config
    // - Chose between different preset interface colors or vanilla style
    // Allow in-game disabling and reloading
    // Add Gui class that other guis extend
    // Implement code for displaying armor stands items as models (I've already done the code for this on laptop)
    // Support forge update json
    // remove "this." where it's unneeded for better readability
    
    // Item GUI:
    // Add picker for ID and Meta (list that shows items)
    // Set "maxDamage" to lowest value which exists (has texture?)
    // Tab complete and arrow keys support for ID
    // Remove quotes around field names in pretty printing
    // Change display of pretty printing and wrap lines
    // Render itemoverlay
    // Remove lore button shouldn't appear, if that line hasn't been added yet
    // id field doesn't work very well with numbers
    
    // Monster Egg gui:
    // Implement more NBT
    // Player picker GUI for tamed etc
    // Support attributes
    
    // Head gui:
    // Add list to pick from players on the server
    // Add list of premade heads
    
    // Export GUI
    // Export as give command
    // Export to clipboard
    // Export in FTBUtil format
    
    // Armor Stand gui:
    // Marker tag desc from wiki on tooltip?
    // Armor stand rotation for viewingT
    // Reset part pose
    
    // Color GUI
    // Are there other items than leather armor that support colors?
    // Color pallete (save colors)
    
    // Rendering
    // Custom render in hand for armor stand and spawn eggs
    // Redesign colors (custom textures for buttons and colorful text etc.)
    // Use GuiLabel in Guis
    
    // Enchanting
    // Book in background
    // Write high levels with roman numerals - https://stackoverflow.com/questions/12967896
    // Support enchanted_book
    
    // CHANGELOG:
    // Fixed pig "saddled" showing as "unsaddled" in Spawnegg gui
    // The very long tags "Signature" and "Value" for player heads are now "snipped", when viewing the tag.
    // Added item overlays to Item Gui
    
    /**
     * A creative tab that contains all item that weren't added to any other tabs at {@link #postInit}
     */
    public static CreativeTabs UNAVAILABLE;
    
    @EventHandler
    public void preInit( FMLPreInitializationEvent event )
    {
        logger = event.getModLog();
    }
    
    @EventHandler
    public void init( FMLInitializationEvent event )
    {
        keybind = new KeyBinding( "key.infinity.desc", Keyboard.KEY_U, "key.infinity.category" );
        // At the moment, it's alright to keep this line here, as the mod won't be loaded on serverside.
        ClientRegistry.registerKeyBinding( keybind );
        
        UNAVAILABLE = new CreativeTabs( "unavailable" ) {
            @Override
            public ItemStack getTabIconItem()
            {
                return new ItemStack( Blocks.BARRIER );
            }
        };
    }
    
    @EventHandler
    public void postInit( FMLPostInitializationEvent event )
    {
        /*
         * Goes through each registered item and block that has been registered and adds them to UNAVAILABLE. This gives the player access to item that are not unavailable such as barriers and command blocks, and potentially items that are added by other mods too.
         * 
         * It's also possible to go through the list, whenever the tab is opened, but this seems to be the favorable way of doing it.
         */
        for ( Item item : Item.REGISTRY )
        {
            if ( item != null && item != Items.AIR && item.getCreativeTab() == null && item != Items.ENCHANTED_BOOK)
            {
                item.setCreativeTab( UNAVAILABLE );
                logger.info( "Item: " + item.getUnlocalizedName() + " was not added to a tab. Adding it to Unavailable." ); // Perhaps it should only print a count of how many items were added.
            }
        }
        
        for ( Block block : Block.REGISTRY )
        {
            if ( block != null && block.getCreativeTabToDisplayOn() == null )
            {
                block.setCreativeTab( UNAVAILABLE );
                logger.info( "Block: " + block.getUnlocalizedName() + " was not added to a tab. Adding it to Unavailable." );
            }
        }
    }
}
