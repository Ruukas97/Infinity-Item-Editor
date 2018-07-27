package ruukas.infinity.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import ruukas.infinity.nbt.NBTHelper;
import ruukas.infinity.nbt.itemstack.tag.InfinitySkullOwnerTag;

public class GuiHeadCollection extends GuiScreen
{
    public static final String[] CATEGORIES = { "alphabet", "animals", "blocks", "decoration", "food-drinks", "humans", "humanoid", "miscellaneous", "monsters", "plants" };
    private static final String API_URL = "https://minecraft-heads.com/scripts/api.php?cat=";
    
    private static int selCat = 0;
    
    private final GuiScreen lastScreen;
    private boolean loadSuccess = false;
    private NonNullList<ItemStack> skulls = NonNullList.create();
    
    public GuiHeadCollection(GuiScreen lastScreen) {
        this.lastScreen = lastScreen;
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
        selCat = 0;
        try
        {
            loadSkulls();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        
    }
    
    @Override
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        
        int maxInRow = (width - 200) / 16;

        for ( int i = 0 ; i < maxInRow * 10 ; i++ )
        {
            int x = 100 + (16 * (i % maxInRow));
            int y = 50 + (16 * (i / maxInRow));
            if ( mouseX > x && mouseX < x + 16 && mouseY > y && mouseY < y + 16 )
            {
                mc.playerController.sendSlotPacket( skulls.get( i ), mc.player.inventory.currentItem + 36 ); // 36 is the index of the action (4 armor, 1 off hand, 5 crafting, and 27 inventory, if I remember correctly).
            }
        }
    }
    
    @Override
    protected void keyTyped( char typedChar, int keyCode ) throws IOException
    {
        if ( keyCode == 1 )
        {
            this.mc.displayGuiScreen( lastScreen );
            
            if ( this.mc.currentScreen == null )
            {
                this.mc.setIngameFocus();
            }
        }
    }
    
    @Override
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();
        itemRender.zLevel = 100.0F;
        int maxInRow = (width - 200) / 16;
        // int maxInRow = (width - 16) / 16;
        for ( int i = 0 ; i < maxInRow * 10 ; i++ )
        {
            int x = 100 + (16 * (i % maxInRow));
            int y = 50 + (16 * (i / maxInRow));
            itemRender.renderItemAndEffectIntoGUI( skulls.get( i ), x, y );
            if ( mouseX > x && mouseX < x + 16 && mouseY > y && mouseY < y + 16 )
            {
                drawRect( x, y, x+16, y+16, HelperGui.getColorFromRGB( 150, 150, 150, 150 ) );
            }
        }
        
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
    
    public void loadSkulls() throws IOException
    {
        URL url = new URL( API_URL + CATEGORIES[selCat] );
        
        String line;
        try ( Scanner s = new Scanner( url.openStream() ))
        {
            line = s.nextLine();
        }
        
        if ( line == null || line.length() < 1 )
        {
            return;
        }
        
        skulls.clear();
        
        JsonParser parser = new JsonParser();
        JsonArray array = (JsonArray) parser.parse( line );
        
        for ( JsonElement e : array )
        {
            ItemStack skull = new ItemStack( Items.SKULL, 1, 3 );
            JsonObject ob = e.getAsJsonObject();
            NBTHelper.getDisplayTag( skull ).setString( "Name", ob.get( "Name" ).getAsString() );
            new InfinitySkullOwnerTag( skull ).setId( ob.get( "UUID" ).getAsString() ).setValue( ob.get( "Value" ).getAsString() );
            
            skulls.add( skull );
        }
    }
    
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
