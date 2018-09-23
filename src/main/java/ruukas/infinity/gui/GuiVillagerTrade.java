package ruukas.infinity.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinity.data.InfinityConfig;
import ruukas.infinity.gui.GuiVillagerTrade.TradeRecipeStackHolder.Type;
import ruukas.infinity.gui.GuiVillagerTrades.MerchantRecipeHolder;

@SideOnly( Side.CLIENT )
public class GuiVillagerTrade extends GuiInfinity
{
    private final MerchantRecipeHolder currentRecipeHolder;
    
    public GuiVillagerTrade(GuiScreen lastScreen, ItemStackHolder stackHolder, MerchantRecipeHolder recHolder) {
        super( lastScreen, stackHolder );
        this.currentRecipeHolder = recHolder;
    }
    
    public static class TradeRecipeStackHolder extends ItemStackHolder
    {
        private final MerchantRecipeHolder recHolder;
        private final Type type;
        
        private TradeRecipeStackHolder(MerchantRecipeHolder recHolder, Type type) {
            super();
            this.recHolder = recHolder;
            this.type = type;
        }
        
        @Override
        public ItemStack getStack()
        {
            switch ( type )
            {
                case BUY:
                    return recHolder.getMerchantRecipe().getItemToBuy();
                
                case SECONDBUY:
                    return recHolder.getMerchantRecipe().getSecondItemToBuy();
                
                case SELL:
                    return recHolder.getMerchantRecipe().getItemToSell();
                
                default:
                    return recHolder.getMerchantRecipe().getItemToBuy();
            }
        }
        
        @Override
        public void setStack( ItemStack stack )
        {
            MerchantRecipe rec = recHolder.getMerchantRecipe();
            this.recHolder.setMerchantRecipe( new MerchantRecipe( type == Type.BUY ? stack : rec.getItemToBuy(), type == Type.SECONDBUY ? stack : rec.getSecondItemToBuy(), type == Type.SELL ? stack : rec.getItemToSell(), rec.getToolUses(), rec.getMaxTradeUses() ) );
        }
        
        public static enum Type {
            BUY, SECONDBUY, SELL;
        }
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
        setRenderStack( true, midX, 35, 1f );
        resetButton.enabled = false;
    }
    
    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        
        int part = midX / 2;
        
        if ( HelperGui.isMouseInRegion( mouseX, mouseY, part, midY, 16, 16 ) )
        {
            mc.displayGuiScreen( new GuiItem( this, new TradeRecipeStackHolder( currentRecipeHolder, Type.BUY ) ) );
        }
        
        else if ( HelperGui.isMouseInRegion( mouseX, mouseY, 2 * part, midY, 16, 16 ) )
        {
            mc.displayGuiScreen( new GuiItem( this, new TradeRecipeStackHolder( currentRecipeHolder, Type.SECONDBUY ) ) );
        }
        
        else if ( HelperGui.isMouseInRegion( mouseX, mouseY, 3 * part, midY, 16, 16 ) )
        {
            mc.displayGuiScreen( new GuiItem( this, new TradeRecipeStackHolder( currentRecipeHolder, Type.SELL ) ) );
        }
    }
    
    @Override
    protected void actionPerformed( GuiButton button ) throws IOException
    {
        super.actionPerformed( button );
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        super.drawScreen( mouseX, mouseY, partialTicks );
        
        int part = midX / 2;
        
        drawCenteredString( fontRenderer, "Price 1", part + 8, midY - 10, InfinityConfig.CONTRAST_COLOR );
        drawCenteredString( fontRenderer, "Price 2", 2 * part + 8, midY - 10, InfinityConfig.CONTRAST_COLOR );
        drawCenteredString( fontRenderer, "Product", 3 * part + 8, midY - 10, InfinityConfig.MAIN_COLOR );
        
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();
        
        ItemStack firstCost = currentRecipeHolder.getMerchantRecipe().getItemToBuy();
        ItemStack secondCost = currentRecipeHolder.getMerchantRecipe().getSecondItemToBuy();
        ItemStack ware = currentRecipeHolder.getMerchantRecipe().getItemToSell();
        
        this.itemRender.zLevel = 100.0F;
        this.itemRender.renderItemAndEffectIntoGUI( firstCost, part, midY );
        this.itemRender.renderItemOverlays( this.fontRenderer, firstCost, part, midY );
        
        if ( !secondCost.isEmpty() )
        {
            this.itemRender.renderItemAndEffectIntoGUI( secondCost, 2 * part, midY );
            this.itemRender.renderItemOverlays( this.fontRenderer, secondCost, 2 * part, midY );
        }
        
        this.itemRender.renderItemAndEffectIntoGUI( ware, 3 * part, midY );
        this.itemRender.renderItemOverlays( this.fontRenderer, ware, 3 * part, midY );
        this.itemRender.zLevel = 0.0F;
        GlStateManager.disableLighting();
        
        // Render tooltips
        if ( HelperGui.isMouseInRegion( mouseX, mouseY, part, midY, 16, 16 ) )
        {
            renderToolTip( firstCost, mouseX, mouseY );
            drawHoveringText( "Click to edit!", mouseX, mouseY - 16 );
        }
        
        else if ( HelperGui.isMouseInRegion( mouseX, mouseY, 2 * part, midY, 16, 16 ) )
        {
            renderToolTip( secondCost, mouseX, mouseY );
            drawHoveringText( "Click to edit!", mouseX, mouseY - 16 );
        }
        
        else if ( HelperGui.isMouseInRegion( mouseX, mouseY, 3 * part, midY, 16, 16 ) )
        {
            renderToolTip( ware, mouseX, mouseY );
            drawHoveringText( "Click to edit!", mouseX, mouseY - 16 );
        }
        
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
    }
    
    @Override
    protected String getNameUnlocalized()
    {
        return "trade";
    }
}
