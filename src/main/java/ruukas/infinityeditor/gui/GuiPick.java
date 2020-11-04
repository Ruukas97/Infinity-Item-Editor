package ruukas.infinityeditor.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.NonNullList;
import ruukas.infinityeditor.Infinity;
import ruukas.infinityeditor.data.InfinityConfig;
import ruukas.infinityeditor.data.thevoid.VoidController;

public class GuiPick extends GuiInfinity
{
    private PickList pickList;
    private NonNullList<ItemStack> filteredList = NonNullList.create();
    
    private int currentElement = 0;
    private String filteredString = null;
    private String searchString = "";
    
    private int maxInRow;
    private int amountInPage;
    
    public static final PickList realmList = new PickList() {
        
        @Override
        public NonNullList<ItemStack> getStackList()
        {
            return Infinity.realmController.getStackList();
        }
        
        @Override
        public String getName()
        {
            return I18n.format( "itemGroup.realm" );
        }
    };
    
    public static final PickList voidList = new PickList() {
        
        @Override
        public NonNullList<ItemStack> getStackList()
        {
            NonNullList<ItemStack> voidStackList = NonNullList.create();
            VoidController.loadVoidToList( voidStackList );
            return voidStackList;
        }
        
        @Override
        public String getName()
        {
            return I18n.format( "itemGroup.void" );
        }
    };
    
    public static final PickList inventoryList = new PickList() {
        
        @Override
        public NonNullList<ItemStack> getStackList()
        {
            InventoryPlayer inv = Minecraft.getMinecraft().player.inventory;
            NonNullList<ItemStack> invStackList = NonNullList.create();
            
            for ( int i = 0 ; i < inv.getSizeInventory() ; i++ )
            {
                ItemStack stack = inv.getStackInSlot( i );
                if ( !stack.isEmpty() )
                {
                    invStackList.add( stack );
                }
            }
            
            return invStackList;
        }
        
        @Override
        public String getName()
        {
            return Minecraft.getMinecraft().player.inventory.getDisplayName().getFormattedText();
        }
    };
    
    public GuiPick(GuiScreen lastScreen, ItemStackHolder itemStackHolder, PickList pickList) {
        super( lastScreen, itemStackHolder );
        this.pickList = pickList;
    }
    
    public GuiPick(GuiScreen lastScreen, ItemStackHolder itemStackHolder) {
        this( lastScreen, itemStackHolder, realmList );
    }
    
    public static abstract class PickList
    {
        public abstract NonNullList<ItemStack> getStackList();
        
        public abstract String getName();
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
        resetButton.enabled = false;
        
        Keyboard.enableRepeatEvents( true );
        
        setRenderStack( true, midX, 35, 1.0F );
        
        if ( !searchString.equals( filteredString ) )
        {
            filteredList.clear();
            
            for ( ItemStack s : pickList.getStackList() )
            {
                if ( s.getDisplayName().toLowerCase().contains( searchString.toLowerCase() ) )
                {
                    filteredList.add( s );
                }
            }
            
            currentElement = 0;
            filteredString = searchString;
        }
        
        maxInRow = 8;
        amountInPage = maxInRow * 10;
    }
    
    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents( false );
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
        else if ( keyCode == Keyboard.KEY_BACK )
        {
            searchString = searchString.substring( 0, Math.max( searchString.length() - 1, 0 ) );
            if ( searchString.length() < 1 && !searchString.equals( filteredString ) )
            {
                initGui();
            }
        }
        else if ( (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER) && !searchString.equals( filteredString ) )
        {
            initGui();
        }
        else if ( ChatAllowedCharacters.isAllowedCharacter( typedChar ) )
        {
            if ( searchString.length() < 20 )
            {
                searchString += typedChar;
            }
        }
    }
    
    @Override
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        
        if ( mouseButton != 0 )
        {
            return;
        }
        
        int space = (width - (maxInRow * 16)) / 2;
        int nextPageW = fontRenderer.getStringWidth( "-->" );
        int topbar = 20;
        
        int currentPage = currentElement / amountInPage;
        int amountPages = ((int) Math.ceil( filteredList.size() / amountInPage ) + 1);
        
        int searchW = fontRenderer.getStringWidth( searchString );
        if ( searchString.length() > 0 && !searchString.equals( filteredString ) && HelperGui.isMouseInRegion( mouseX, mouseY, midX - searchW / 2, 56, searchW, 8 ) )
        {
            initGui();
        }
        
        // Next page
        else if ( currentPage + 1 < amountPages && HelperGui.isMouseInRegion( mouseX, mouseY, midX + 10, 70 + topbar + 168, nextPageW, 8 ) )
        {
            currentElement = Math.min( filteredList.size() - 1, (currentPage + 1) * amountInPage );
            return;
        }
        else if ( currentPage > 0 && HelperGui.isMouseInRegion( mouseX, mouseY, midX - 10 - nextPageW, 70 + topbar + 168, nextPageW, 8 ) )
        {
            currentElement = Math.max( 0, (currentPage - 1) * amountInPage );
            return;
        }
        
        else if ( mouseX < space )
        {
            
            if ( pickList != realmList && HelperGui.isMouseInRegion( mouseX, mouseY, space - fontRenderer.getStringWidth( realmList.getName() ) - 10, 100, fontRenderer.getStringWidth( realmList.getName() ), 8 ) )
            {
                pickList = realmList;
                filteredString = null;
                initGui();
            }
            
            else if ( pickList != voidList && HelperGui.isMouseInRegion( mouseX, mouseY, space - fontRenderer.getStringWidth( voidList.getName() ) - 10, 120, fontRenderer.getStringWidth( voidList.getName() ), 8 ) )
            {
                pickList = voidList;
                filteredString = null;
                initGui();
            }
            
            else if ( pickList != inventoryList && HelperGui.isMouseInRegion( mouseX, mouseY, space - fontRenderer.getStringWidth( inventoryList.getName() ) - 10, 140, fontRenderer.getStringWidth( inventoryList.getName() ), 8 ) )
            {
                pickList = inventoryList;
                filteredString = null;
                initGui();
            }
            
            return;
        }
        
        if ( filteredList.size() > 0 )
        {
            for ( int i = (int) Math.min( filteredList.size() - 1, currentPage * amountInPage ) ; i < (int) Math.min( filteredList.size(), (currentPage + 1) * amountInPage ) ; i++ )
            {
                int x = space + (16 * (i % maxInRow));
                int y = 70 + topbar + (16 * ((i % amountInPage) / maxInRow));
                if ( mouseX > x && mouseX < x + 16 && mouseY > y && mouseY < y + 16 )
                {
                    stackHolder.setStack( filteredList.get( i ) );
                }
            }
        }
    }
    
    @Override
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        super.drawScreen( mouseX, mouseY, partialTicks );
        
        int topbar = 20;
        int space = (width - (maxInRow * 16 + 3)) / 2;
        int currentPage = currentElement / amountInPage;
        int blandColor = HelperGui.getColorFromRGB( 255, 150, 200, 255 );
        int amountPages = ((int) Math.ceil( filteredList.size() / amountInPage ) + 1);
        
        drawCenteredString( fontRenderer, pickList.getName(), midX, 60, InfinityConfig.CONTRAST_COLOR );
        drawCenteredString( fontRenderer, searchString.length() > 0 ? searchString : I18n.format( "gui.headcollection.typesearch" ), midX, 73, blandColor );
        
        drawString( fontRenderer, realmList.getName(), space - fontRenderer.getStringWidth( realmList.getName() ) - 10, 100, pickList == realmList ? InfinityConfig.CONTRAST_COLOR : InfinityConfig.MAIN_COLOR );
        drawString( fontRenderer, voidList.getName(), space - fontRenderer.getStringWidth( voidList.getName() ) - 10, 120, pickList == voidList ? InfinityConfig.CONTRAST_COLOR : InfinityConfig.MAIN_COLOR );
        drawString( fontRenderer, inventoryList.getName(), space - fontRenderer.getStringWidth( inventoryList.getName() ) - 10, 140, pickList == inventoryList ? InfinityConfig.CONTRAST_COLOR : InfinityConfig.MAIN_COLOR );
        
        String nextPage = "-->";
        int nextPageW = fontRenderer.getStringWidth( nextPage );
        if ( currentPage + 1 < amountPages )
        {
            boolean selectedN = HelperGui.isMouseInRegion( mouseX, mouseY, midX + 10, 70 + topbar + 168, nextPageW, 8 );
            drawString( fontRenderer, nextPage, midX + 10, 70 + topbar + 168, selectedN ? InfinityConfig.CONTRAST_COLOR : blandColor );
        }
        
        drawCenteredString( fontRenderer, "" + (currentPage + 1), midX, 70 + topbar + 168, blandColor );
        
        if ( currentPage > 0 )
        {
            String previousPage = "<--";
            boolean selectedP = HelperGui.isMouseInRegion( mouseX, mouseY, midX - nextPageW - 10, 70 + topbar + 168, nextPageW, 8 );
            drawString( fontRenderer, previousPage, midX - nextPageW - 10, 70 + topbar + 168, selectedP ? InfinityConfig.CONTRAST_COLOR : blandColor );
        }
        
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        
        ItemStack hovered = null;
        if ( filteredList.size() > 0 )
        {
            GlStateManager.enableLighting();
            itemRender.zLevel = 100.0F;
            for ( int i = (int) Math.min( filteredList.size() - 1, currentPage * amountInPage ) ; i < (int) Math.min( filteredList.size(), (currentPage + 1) * amountInPage ) ; i++ )
            {
                int x = space + (16 * (i % maxInRow));
                int y = 70 + topbar + (16 * ((i % amountInPage) / maxInRow));
                itemRender.renderItemAndEffectIntoGUI( filteredList.get( i ), x, y );
                itemRender.renderItemOverlayIntoGUI( fontRenderer, filteredList.get( i ), x, y, null );
                if ( mouseX > x && mouseX < x + 16 && mouseY > y && mouseY < y + 16 )
                {
                    drawRect( x, y, x + 16, y + 16, HelperGui.getColorFromRGB( 150, 150, 150, 150 ) );
                    hovered = filteredList.get( i );
                }
            }
            
            GlStateManager.disableLighting();
        }
        
        int searchW = fontRenderer.getStringWidth( searchString );
        
        if ( hovered != null )
        {
            renderToolTip( hovered, mouseX, mouseY );
        }
        else if ( !searchString.equals( filteredString ) && HelperGui.isMouseInRegion( mouseX, mouseY, midX - searchW / 2, 56, searchW, 8 ) )
        {
            drawHoveringText( I18n.format( "gui.headcollection.clicksearch" ), mouseX, mouseY );
        }
        else if ( HelperGui.isMouseInRegion( mouseX, mouseY, midX - 8, 27, 16, 16 ) )
        {
            renderToolTip( getItemStack(), mouseX, mouseY );
        }
        
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
    }
    
    @Override
    protected String getNameUnlocalized()
    {
        return "pick";
    }
    
}
