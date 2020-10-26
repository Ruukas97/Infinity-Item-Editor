package ruukas.infinity.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.village.MerchantRecipe;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinity.data.InfinityConfig;
import ruukas.infinity.util.MerchantRecipeUtil;

@SideOnly( Side.CLIENT )
public class GuiVillagerTrades extends GuiInfinity
{
    private MerchantRecipeHolderList buyingList;
    
    public GuiVillagerTrades(GuiScreen lastScreen, ItemStackHolder stackHolder) {
        super( lastScreen, stackHolder );
        
        // renderTag = true;
        setRenderStack( true, midX, 35, 1.0F );
        
        NBTTagCompound entityTag = getItemStack().getOrCreateSubCompound( "EntityTag" );
        if ( !entityTag.hasKey( "Offers", NBT.TAG_COMPOUND ) )
        {
            entityTag.setTag( "Offers", new NBTTagCompound() );
        }
        
        buyingList = new MerchantRecipeHolderList( entityTag.getCompoundTag( "Offers" ) );
    }
    
    public static class MerchantRecipeHolder
    {
        private MerchantRecipe rec;
        
        public MerchantRecipeHolder(MerchantRecipe rec) {
            this.rec = rec;
        }
        
        public MerchantRecipe getMerchantRecipe()
        {
            return this.rec;
        }
        
        public void setMerchantRecipe( MerchantRecipe rec )
        {
            this.rec = rec;
        }
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
        
        NBTTagCompound recs = buyingList.getRecipiesAsTags();
        
        if ( !recs.hasNoTags() )
        {
            getItemStack().getOrCreateSubCompound( "EntityTag" ).setTag( "Offers", recs );
        }
    }
    
    @Override
    protected void reset()
    {
        buyingList.clear();
        onGuiClosed();
    }
    
    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        NBTTagCompound recs = buyingList.getRecipiesAsTags();
        
        getItemStack().getOrCreateSubCompound( "EntityTag" ).setTag( "Offers", recs );
    }
    
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        super.updateScreen();
    }
    
    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped( char typedChar, int keyCode ) throws IOException
    {
        super.keyTyped( typedChar, keyCode );
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        
        if ( mouseButton != 0 && mouseButton != 1 )
        {
            return;
        }
        
        int size = buyingList.size();
        
        boolean sel = false;
        for ( int i = 0 ; i < size ; i++ )
        {
            MerchantRecipeHolder rH = buyingList.get( i );
            String rString = MerchantRecipeUtil.getMerchantRecipeDisplayString( rH.getMerchantRecipe() );
            int rWidth = fontRenderer.getStringWidth( rString );
            if ( HelperGui.isMouseInRegion( mouseX, mouseY, midX - rWidth / 2, midY - 20 * (size + 1) / 2 + 20 * i, rWidth, 8 ) )
            {
                if ( mouseButton == 0 )
                    mc.displayGuiScreen( new GuiVillagerTrade( this, stackHolder, rH ) );
                if ( mouseButton == 1 )
                {
                    buyingList.remove( i );
                }
                break;
            }
        }
        
        if ( !sel && mouseButton == 0 )
        {
            int addWidth = fontRenderer.getStringWidth( I18n.format( "gui.villagertrades.addtrade" ) );
            if ( HelperGui.isMouseInRegion( mouseX, mouseY, midX - (addWidth / 2), midY - 20 * (size + 1) / 2 + 20 * size, addWidth, 8 ) )
            {
                buyingList.add( new MerchantRecipeHolder( new MerchantRecipe( new ItemStack( Items.EMERALD ), Items.EMERALD ) ) );
            }
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
        
        int size = buyingList.size();
        
        boolean foundSel = false;
        for ( int i = 0 ; i < size ; i++ )
        {
            String rString = MerchantRecipeUtil.getMerchantRecipeDisplayString( buyingList.get( i ).getMerchantRecipe() );
            boolean sel = false;
            if ( !foundSel )
            {
                int rWidth = fontRenderer.getStringWidth( rString );
                sel = foundSel = HelperGui.isMouseInRegion( mouseX, mouseY, midX - rWidth / 2, midY - 20 * (size + 1) / 2 + 20 * i, rWidth, 8 );
            }
            
            drawCenteredString( fontRenderer, rString, midX, midY - 20 * (size + 1) / 2 + 20 * i, sel ? InfinityConfig.CONTRAST_COLOR : InfinityConfig.MAIN_COLOR );
        }
        
        int addWidth = fontRenderer.getStringWidth( I18n.format( "gui.villagertrades.addtrade" ) );
        boolean inRegion = !foundSel && HelperGui.isMouseInRegion( mouseX, mouseY, midX - (addWidth / 2), midY - 20 * (size + 1) / 2 + 20 * size, addWidth, 8 );
        drawCenteredString( fontRenderer, I18n.format( "gui.villagertrades.addtrade" ), midX, midY - 20 * (size + 1) / 2 + 20 * size, inRegion ? InfinityConfig.CONTRAST_COLOR : InfinityConfig.MAIN_COLOR );
        
        if ( foundSel )
        {
            drawHoveringText( new ArrayList<>( Arrays.asList( new String[] { I18n.format( "gui.villagertrades.leftclick" ), I18n.format( "gui.villagertrades.rightclick" ) } ) ), mouseX, mouseY );
        }
        else
        {
            HelperGui.addTooltip( resetButton, mouseX, mouseY, I18n.format( "gui.villagertrades.reset" ) );
        }
    }
    
    @Override
    protected String getNameUnlocalized()
    {
        return "villagertrades";
    }
}
