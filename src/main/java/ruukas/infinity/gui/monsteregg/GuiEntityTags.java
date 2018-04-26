package ruukas.infinity.gui.monsteregg;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import ruukas.infinity.gui.HelperGui;

public class GuiEntityTags extends GuiScreen
{
    
    public final GuiScreen parentScreen;
    
    protected String screenTitle = "Entity Tags";// TODO Localization
    
    private GuiListExtended optionsRowList;
    
    private ItemStack stack;
    
    private final MobTag[] tags;
    
    public GuiEntityTags(GuiScreen parentScreen, ItemStack stack, MobTag[] tags) {
        this.parentScreen = parentScreen;
        this.tags = tags;
        this.stack = stack;
    }
    
    public void initGui()
    {
        this.screenTitle = I18n.format( "gui.entitytags" );
        this.buttonList.clear();
        this.buttonList.add( new GuiButton( 200, this.width / 2 - 100, this.height - 27, I18n.format( "gui.done" ) ) );
        
        this.optionsRowList = new GuiTagRowList( this.mc, this.width, this.height, 32, this.height - 32, 25, this, stack, tags );
    }
    
    /**
     * Handles mouse input.
     */
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        this.optionsRowList.handleMouseInput();
    }
    
    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped( char typedChar, int keyCode ) throws IOException
    {
        if ( keyCode == 1 )
        {
            this.mc.gameSettings.saveOptions();
        }
        
        super.keyTyped( typedChar, keyCode );
    }
    
    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed( GuiButton button ) throws IOException
    {
        if ( button.enabled )
        {
            if ( button.id == 200 )
            {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen( this.parentScreen );
            }
        }
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        this.optionsRowList.mouseClicked( mouseX, mouseY, mouseButton );
    }
    
    /**
     * Called when a mouse button is released.
     */
    protected void mouseReleased( int mouseX, int mouseY, int state )
    {
        super.mouseReleased( mouseX, mouseY, state );
        this.optionsRowList.mouseReleased( mouseX, mouseY, state );
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        this.drawDefaultBackground();
        this.optionsRowList.drawScreen( mouseX, mouseY, partialTicks );
        this.drawCenteredString( this.fontRenderer, this.screenTitle, this.width / 2, 5, HelperGui.TITLE_PURPLE );
        super.drawScreen( mouseX, mouseY, partialTicks );
    }
    
    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
    }
}
