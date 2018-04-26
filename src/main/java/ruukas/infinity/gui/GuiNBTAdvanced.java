package ruukas.infinity.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinity.Infinity;
import ruukas.infinity.gui.action.GuiInfinityButton;
import ruukas.infinity.gui.nbt.NBTListElement;
import ruukas.infinity.gui.nbt.NBTListRoot;

@SideOnly( Side.CLIENT )
public class GuiNBTAdvanced extends GuiScreen
{
    
    private ItemStack stack = ItemStack.EMPTY;
    
    private final GuiScreen lastScreen;
    
    private GuiInfinityButton backButton;
    
    protected String title = I18n.format( "gui.nbtadv" );
    
    protected String info = "";
    protected int infoColor;
    
    private NBTListElement rootElement;
    
    public static int windowX = 20, windowY = 40;
    public static int windowWidth, windowHeight;
    
    public GuiNBTAdvanced(GuiScreen lastScreen, ItemStack stack) {
        this.lastScreen = lastScreen;
        this.stack = stack;
    }
    
    @Override
    public void initGui()
    {
        this.rootElement = new NBTListRoot( stack );
        
        windowWidth = width - 20;
        windowHeight = height - 20;
        
        backButton = addButton( new GuiInfinityButton( 200, this.width / 2 - 60, this.height - 25, 60, 20, I18n.format( "gui.back" ) ) );
    }
    
    @Override
    public void onGuiClosed()
    {
    }
    
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
    }
    
    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped( char typedChar, int keyCode ) throws IOException
    {
        if ( keyCode == 1 )
        {
            this.actionPerformed( this.backButton );
        }
        else if ( keyCode == 28 || keyCode == 156 )
        {
            // On ENTER
            // this.actionPerformed(this.updateNbtButton);
        }
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        
        rootElement.mouseClicked( mouseX, mouseY, mouseButton );
    }
    
    @Override
    protected void actionPerformed( GuiButton button ) throws IOException
    {
        if ( button.id == backButton.id )
        {
            this.mc.displayGuiScreen( lastScreen );
            
            if ( this.mc.currentScreen == null )
            {
                this.mc.setIngameFocus();
            }
        }
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        // this.drawDefaultBackground();
        // this.drawGradientRect(0, 0, width, 18, GuiHelper.getColorFromRGB(150, 230, 115, 30), -1072689136);
        // this.drawGradientRect(0, 17, this.width, this.height, -1072689136, -804253680);
        
        int mainColor = HelperGui.getColorFromRGB( 255, 30, 200, 255 );
        // Purple: GuiHelper.getColorFromRGB(255, 130, 30, 130)
        
        // Bar
        drawGradientRectHorizontal( 18, 18, width - 18, 38, HelperGui.getColorFromRGB( 255, 50, 50, 50 ), mainColor );
        
        // Frame
        drawRect( 20, 40, width - 20, height - 20, HelperGui.getColorFromRGB( 222, 50, 50, 50 ) );
        // Borders
        drawRect( 18, 38, width - 18, 40, mainColor );
        drawRect( 18, 40, 20, height - 20, mainColor );
        drawRect( width - 20, 40, width - 18, height - 20, mainColor );
        drawRect( 18, height - 20, width - 18, height - 18, mainColor );
        
        // this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
        
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();
        /*
         * itemRender.renderItemAndEffectIntoGUI(stack, 10, 25);
         * 
         * if(stack.hasTagCompound()){ itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.CHEST), 25, 51); }else{ itemRender.renderItemAndEffectIntoGUI(new ItemStack(Blocks.ENDER_CHEST), 25, 51); }
         */
        
        rootElement.drawIcon( itemRender );
        
        GlStateManager.popMatrix();
        
        // Stack name
        // drawString(fontRenderer, stack.getDisplayName(), 32, 30, 0xffffff);
        
        // Tag
        // drawString(fontRenderer, stack.hasTagCompound() ? "tag" : I18n.format("gui.nbt.inittag"), 47, 56, 0xffffff);
        
        rootElement.draw( mc, mouseX, mouseY );
        if ( info != null && info.length() > 0 )
        {
            drawCenteredString( fontRenderer, info, width / 2, 20, infoColor );
            setInfo( "", 0 );
        }
        
        drawString( fontRenderer, Infinity.NAME + " - " + title, 25, 26, HelperGui.getColorFromRGB( 255, 0, 255, 255 ) );
        
        String unf = "This is not finished and will include more features in the future!";
        drawString( fontRenderer, unf, width - fontRenderer.getStringWidth( unf ) - 25, height - 30, HelperGui.getColorFromRGB( 255, 255, 50, 50 ) );
        
        super.drawScreen( mouseX, mouseY, partialTicks );
    }
    
    protected void drawGradientRectHorizontal( int left, int top, int right, int bottom, int startColor, int endColor )
    {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate( GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO );
        GlStateManager.shadeModel( 7425 );
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin( 7, DefaultVertexFormats.POSITION_COLOR );
        bufferbuilder.pos( (double) right, (double) bottom, (double) this.zLevel ).color( f1, f2, f3, f ).endVertex();
        bufferbuilder.pos( (double) right, (double) top, (double) this.zLevel ).color( f1, f2, f3, f ).endVertex();
        bufferbuilder.pos( (double) left, (double) top, (double) this.zLevel ).color( f5, f6, f7, f4 ).endVertex();
        bufferbuilder.pos( (double) left, (double) bottom, (double) this.zLevel ).color( f5, f6, f7, f4 ).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel( 7424 );
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    
    public void setInfo( String info, int color )
    {
        this.info = info;
        this.infoColor = color;
    }
    
    public static void setInfoStatic( String info, int color )
    {
        if ( Minecraft.getMinecraft().currentScreen instanceof GuiNBTAdvanced )
        {
            ((GuiNBTAdvanced) Minecraft.getMinecraft().currentScreen).setInfo( info, color );
        }
    }
    
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
    
    public ItemStack getItemStack()
    {
        return stack;
    }
}
