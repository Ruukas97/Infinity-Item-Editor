package ruukas.infinityeditor.gui.monsteregg;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinityeditor.data.InfinityConfig;

@SideOnly( Side.CLIENT )
public class GuiEditString extends GuiScreen
{
    /** Text field containing the command block's command. */
    private GuiTextField textField;
    private GuiButton doneBtn;
    private GuiButton cancelBtn;
    
    private MobTagString tag;
    private GuiScreen parentScreen;
    private ItemStack stack;
    
    public GuiEditString(GuiScreen parentScreen, MobTagString tag, ItemStack stack) {
        this.parentScreen = parentScreen;
        this.stack = stack;
        this.tag = tag;
    }
    
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.textField.updateCursorCounter();
    }
    
    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents( true );
        this.buttonList.clear();
        this.doneBtn = this.addButton( new GuiButton( 0, this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.format( "gui.done" ) ) );
        this.cancelBtn = this.addButton( new GuiButton( 1, this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.format( "gui.cancel" ) ) );
        this.textField = new GuiTextField( 2, this.fontRenderer, this.width / 2 - 150, 50, 300, 20 );
        this.textField.setMaxStringLength( 32500 );
        this.textField.setFocused( true );
        this.textField.setText( tag.getValue( stack ) );
    }
    
    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents( false );
        
    }
    
    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed( GuiButton button ) throws IOException
    {
        if ( button.enabled )
        {
            if ( button.id == 1 )
            {
                this.mc.displayGuiScreen( this.parentScreen );
            }
            else if ( button.id == 0 )
            {
                this.tag.setValue( this.textField.getText(), stack );
                this.mc.displayGuiScreen( this.parentScreen );
            }
        }
    }
    
    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped( char typedChar, int keyCode ) throws IOException
    {
        this.textField.textboxKeyTyped( typedChar, keyCode );
        
        if ( keyCode != 28 && keyCode != 156 )
        {
            if ( keyCode == 1 )
            {
                this.actionPerformed( this.cancelBtn );
            }
        }
        else
        {
            this.actionPerformed( this.doneBtn );
        }
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        this.textField.mouseClicked( mouseX, mouseY, mouseButton );
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        this.drawDefaultBackground();
        this.drawCenteredString( this.fontRenderer, I18n.format( "gui.editstring" ), this.width / 2, 20, InfinityConfig.MAIN_COLOR );
        this.drawString( this.fontRenderer, tag.getTranslatedName(), this.width / 2 - 150, 40, 10526880 );
        this.textField.drawTextBox();
        // int i = 75;
        // int j = 0;
        // this.drawString(this.fontRenderer, I18n.format("advMode.nearestPlayer"), this.width / 2 - 140, i + j++ * this.fontRenderer.FONT_HEIGHT, 10526880);
        // this.drawString(this.fontRenderer, I18n.format("advMode.randomPlayer"), this.width / 2 - 140, i + j++ * this.fontRenderer.FONT_HEIGHT, 10526880);
        // this.drawString(this.fontRenderer, I18n.format("advMode.allPlayers"), this.width / 2 - 140, i + j++ * this.fontRenderer.FONT_HEIGHT, 10526880);
        // this.drawString(this.fontRenderer, I18n.format("advMode.allEntities"), this.width / 2 - 140, i + j++ * this.fontRenderer.FONT_HEIGHT, 10526880);
        // this.drawString(this.fontRenderer, I18n.format("advMode.self"), this.width / 2 - 140, i + j++ * this.fontRenderer.FONT_HEIGHT, 10526880);
        
        super.drawScreen( mouseX, mouseY, partialTicks );
    }
}