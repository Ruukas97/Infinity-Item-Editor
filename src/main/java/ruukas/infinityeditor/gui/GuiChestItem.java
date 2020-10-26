package ruukas.infinity.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly( Side.CLIENT )
public class GuiChestItem extends GuiContainer
{
    
    /** The ResourceLocation containing the chest GUI texture. */
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation( "textures/gui/container/generic_54.png" );
    private final IInventory upperChestInventory;
    private final IInventory lowerChestInventory;
    /**
     * window height is calculated with these values; the more rows, the heigher
     */
    private final int inventoryRows;
    
    private final GuiScreen lastScreen;
    
    public GuiChestItem(GuiScreen lastScreen, IInventory playerInventory, IInventory chestInventory) {
        super( new ContainerChest( playerInventory, chestInventory, Minecraft.getMinecraft().player ) );
        this.upperChestInventory = playerInventory;
        this.lowerChestInventory = chestInventory;
        this.allowUserInput = false;
        
        this.lastScreen = lastScreen;
        
        this.inventoryRows = chestInventory.getSizeInventory() / 9;
        this.ySize = 114 + this.inventoryRows * 18;
    }
    
    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    @Override
    protected void handleMouseClick( Slot slotIn, int slotId, int mouseButton, ClickType type )
    {
        if ( slotIn != null )
        {
            slotId = slotIn.slotNumber;
        }
        
        this.inventorySlots.slotClick( slotId, mouseButton, type, mc.player );
    }
    
    @Override
    protected void keyTyped( char typedChar, int keyCode ) throws IOException
    {
        super.keyTyped( typedChar, keyCode );
        
        if ( keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches( keyCode ) )
        {
            this.mc.displayGuiScreen( lastScreen );
        }
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        // this.drawDefaultBackground();
        super.drawScreen( mouseX, mouseY, partialTicks );
        this.renderHoveredToolTip( mouseX, mouseY );
    }
    
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer( int mouseX, int mouseY )
    {
        this.fontRenderer.drawString( this.lowerChestInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752 );
        this.fontRenderer.drawString( this.upperChestInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752 );
        
        String s = I18n.format( "gui.equipment.inventoryclone" );
        this.fontRenderer.drawString( s, (width / 2) - guiLeft - (fontRenderer.getStringWidth( s ) / 2), -10, 0x7c2c87 );
    }
    
    /**
     * Draws the background layer of this container (behind the items).
     */
    protected void drawGuiContainerBackgroundLayer( float partialTicks, int mouseX, int mouseY )
    {
        GlStateManager.color( 1.0F, 1.0F, 1.0F, 1.0F );
        this.mc.getTextureManager().bindTexture( CHEST_GUI_TEXTURE );
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect( i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17 );
        this.drawTexturedModalRect( i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96 );
    }
}
