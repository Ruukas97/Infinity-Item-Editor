package ruukas.infinityeditor.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinityeditor.data.InfinityConfig;
import ruukas.infinityeditor.gui.action.GuiActionTextField;
import ruukas.infinityeditor.gui.action.GuiInfinityButton;

@SideOnly( Side.CLIENT )
public class GuiNBT extends GuiScreen
{
    
    private final ItemStack stack;
    
    private final GuiScreen lastScreen;
    
    private GuiInfinityButton updateNbtButton;
    private GuiInfinityButton backButton, resetButton;
    private GuiInfinityButton[] colorButtons;
    
    private GuiActionTextField nbtTextField;
    
    protected String title = I18n.format( "gui.nbt" );
    
    protected ArrayList<String> prettyNBTList = new ArrayList<>();
    
    private String feedback = "";
    private boolean good = false;
    
    public GuiNBT(GuiScreen lastScreen, ItemStack stack) {
        this.lastScreen = lastScreen;
        this.stack = stack;
    }
    
    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents( true );
        nbtTextField = new GuiActionTextField( 100, this.fontRenderer, this.width / 4, 80, this.width / 2, 16 );
        nbtTextField.setMaxStringLength( 20000 );
        nbtTextField.setText( stack.hasTagCompound() ? Objects.requireNonNull(stack.getTagCompound()).toString() : "{}" );
        
        updateNbtButton = this.addButton( new GuiInfinityButton( 105, 3 * width / 7, 100, width / 7, 20, I18n.format( "gui.nbt.update" ) ) );
        
        backButton = addButton( new GuiInfinityButton( 200, this.width / 2 - 60, this.height - 25, 60, 20, I18n.format( "gui.back" ) ) );
        resetButton = addButton( new GuiInfinityButton( 201, this.width / 2, this.height - 25, 60, 20, I18n.format( "gui.reset" ) ) );
        
        // COLOR BUTTONS
        TextFormatting[] formats = TextFormatting.values();
        int colorAmount = 2 + formats.length;
        colorButtons = new GuiInfinityButton[ colorAmount ];
        colorButtons[0] = addButton( new GuiInfinityButton( 130, width - 1 - 13 * ((colorAmount + 2) / 2) + (13), height - 30, 13, 15, formats[0].toString().substring( 0, 1 ) ) );
        colorButtons[1] = addButton( new GuiInfinityButton( 131, width - 1 - 13 * ((colorAmount + 2) / 2) + (13 * 2), height - 30, 13, 15, TextFormatting.DARK_RED + "%" ) );
        
        for ( int i = 2 ; i < colorAmount ; i++ )
        {
            TextFormatting f = formats[i - 2];
            colorButtons[i] = addButton( new GuiInfinityButton( 130 + i, width - 1 - 13 * ((colorAmount + 2) / 2) + (13 * ((i % (colorAmount / 2)) + 1)), height - 30 + (15 * (i / (colorAmount / 2))), 13, 15, f.toString() + f.toString().substring( 1 ) ) );
        }
    }
    
    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents( false );
    }
    
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        nbtTextField.updateCursorCounter();
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
            this.actionPerformed( this.updateNbtButton );
        }
        else
        {
            nbtTextField.textboxKeyTyped( typedChar, keyCode );
        }
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        
        nbtTextField.mouseClicked( mouseX, mouseY, mouseButton );
    }
    
    @Override
    protected void actionPerformed( GuiButton button )
    {
        if ( button.id == updateNbtButton.id )
        {
            try
            {
                stack.setTagCompound( JsonToNBT.getTagFromJson( nbtTextField.getText() ) );
                good = true;
                feedback = "Looks good";
            }
            catch ( NBTException nbtexception )
            {
                good = false;
                feedback = nbtexception.getMessage();
            }
        }
        
        else if ( button.id == backButton.id )
        {
            this.mc.displayGuiScreen( lastScreen );
            
            if ( this.mc.currentScreen == null )
            {
                this.mc.setIngameFocus();
            }
        }
        
        else if ( button.id >= 130 && button.id < 130 + colorButtons.length )
        {
            GuiTextField f = nbtTextField;
            if ( f.isFocused() )
            {
                if ( button.id == 130 )
                {
                    f.setText( f.getText().substring( 0, f.getCursorPosition() ) + TextFormatting.values()[0].toString().charAt( 0) + f.getText().substring( f.getCursorPosition()) );
                }
                
                else if ( button.id == 131 )
                {
                    f.setText(Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(f.getText())));
                }
                
                else
                {
                    f.setText( f.getText().substring( 0, f.getCursorPosition() ) + TextFormatting.values()[button.id - 132] + f.getText().substring( f.getCursorPosition()) );
                }
            }
        }
        
        else if ( button.id == resetButton.id )
        {
            if ( stack.hasTagCompound() )
            {
                stack.setTagCompound( new NBTTagCompound() );
            }
        }
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        this.drawDefaultBackground();
        
        GlStateManager.pushMatrix();
        GL11.glScalef( 0.8f, 0.8f, 0.8f );
        this.renderToolTip( stack, 0, 25 );
        
        String s = stack.hasTagCompound() ? Objects.requireNonNull(stack.getTagCompound()).toString() : "{}";
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse( s );
        s = gson.toJson( je );
        
        prettyNBTList.clear();
        Collections.addAll(prettyNBTList, s.split("\\n"));
        
        drawHoveringText( prettyNBTList, 0, this.height );
        
        GlStateManager.popMatrix();
        
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();
        this.itemRender.renderItemAndEffectIntoGUI( stack, (width / 2) - 8, 30 );
        GlStateManager.popMatrix();
        
        this.drawCenteredString( fontRenderer, title, width / 2, 15, InfinityConfig.MAIN_COLOR );
        
        this.drawCenteredString( fontRenderer, feedback, width / 2, 130, good ? HelperGui.GOOD_GREEN : HelperGui.BAD_RED );
        
        nbtTextField.drawTextBox();
        
        super.drawScreen( mouseX, mouseY, partialTicks );
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
