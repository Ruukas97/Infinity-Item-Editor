package ruukas.infinityeditor.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinityeditor.data.InfinityConfig;
import ruukas.infinityeditor.gui.GuiVillagerTrade.TradeRecipeStackHolder.Type;
import ruukas.infinityeditor.gui.GuiVillagerTrades.MerchantRecipeHolder;
import ruukas.infinityeditor.gui.action.GuiNumberField;

import java.io.IOException;

@SideOnly( Side.CLIENT )
public class GuiVillagerTrade extends GuiInfinity
{
    private final MerchantRecipeHolder currentRecipeHolder;
    private GuiNumberField maxUsesNumberField;
    private int maxUsesNumberFieldY = 0;
    
    public GuiVillagerTrade(GuiScreen lastScreen, ItemStackHolder stackHolder, MerchantRecipeHolder recHolder) {
        super( lastScreen, stackHolder );
        this.currentRecipeHolder = recHolder;
    }
    
    public static class TradeRecipeStackHolder extends GuiInfinity.ItemStackHolder
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
        
        public enum Type {
            BUY, SECONDBUY, SELL
        }
    }
    
    @Override
    public void initGui()
    {
        super.initGui();

        int maxUsesFieldWidth = width / 7;
        maxUsesNumberFieldY = 80;
        maxUsesNumberField = new GuiNumberField( 100, fontRenderer, (width - maxUsesFieldWidth)  / 2, maxUsesNumberFieldY, maxUsesFieldWidth, 16, 4);
        maxUsesNumberField.setValue(currentRecipeHolder.getMerchantRecipe().getMaxTradeUses());
        maxUsesNumberField.action = () -> {
            MerchantRecipe copy = currentRecipeHolder.getMerchantRecipe(); // Create copy of current recipe
            NBTTagCompound compound = copy.writeToTags(); // Get recipe as NBT
            compound.setInteger("maxUses", maxUsesNumberField.getIntValue()); // Update Max Uses
            copy.readFromTags(compound); // Reinstate copy from NBT
            currentRecipeHolder.setMerchantRecipe(copy); // Set new copy
        };

        setRenderStack( true, midX, 35, 1f );
        resetButton.enabled = false;
    }
    
    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(maxUsesNumberField.isFocused()) {
            maxUsesNumberField.textboxKeyTyped(typedChar, keyCode);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        maxUsesNumberField.mouseClicked(mouseX, mouseY, mouseButton);
        
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
        maxUsesNumberField.drawTextBox();
        
        int part = midX / 2;

        drawCenteredString( fontRenderer, "Max Uses", width / 2, maxUsesNumberFieldY - fontRenderer.FONT_HEIGHT - 4, InfinityConfig.MAIN_COLOR);

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
