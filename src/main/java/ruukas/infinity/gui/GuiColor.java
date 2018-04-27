package ruukas.infinity.gui;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiSlider.ISlider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinity.Infinity;
import ruukas.infinity.gui.action.GuiActionTextField;
import ruukas.infinity.gui.action.GuiInfinityButton;
import ruukas.infinity.nbt.NBTHelper;

@SideOnly( Side.CLIENT )
public class GuiColor extends GuiScreen
{
    
    private ItemStack stack = ItemStack.EMPTY;
    
    private final GuiScreen lastScreen;
    
    private GuiInfinityButton backButton, resetButton, dropButton;
    
    private GuiSlider redSlider, greenSlider, blueSlider;
    
    private GuiActionTextField hexText;
    
    protected String title = I18n.format( "gui.color" );
    
    protected ArrayList<String> prettyNBTList = new ArrayList<>();
    
    public GuiColor(GuiScreen lastScreen, ItemStack stack) {
        this.lastScreen = lastScreen;
        this.stack = stack;
    }
    
    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents( true );
        
        hexText = new GuiActionTextField( 100, this.fontRenderer, (this.width / 2) - 25, this.height / 2 - 85, 50, 20 );
        hexText.setMaxStringLength( 7 );
        hexText.setText( '#' + Integer.toHexString( NBTHelper.ColorNBTHelper.getColorAsInt( stack ) ) );
        
        hexText.action = () -> {
            String text = hexText.getText();
            int length = text.length();
            
            if ( length == 7 )
            {
                text = text.substring( 1 );
            }
            
            if ( length == 6 )
            {
                try
                {
                    NBTHelper.ColorNBTHelper.setColor( stack, (int) Long.parseLong( text, 16 ) );
                }
                catch ( NumberFormatException e )
                {
                    Infinity.logger.error( "Could not parse " + text + " as a hex color." );
                }
            }
        };
        
        backButton = addButton( new GuiInfinityButton( 200, this.width / 2 - 90, this.height - 25, 60, 20, I18n.format( "gui.back" ) ) );
        resetButton = addButton( new GuiInfinityButton( 201, this.width / 2 - 30, this.height - 25, 60, 20, I18n.format( "gui.reset" ) ) );
        dropButton = addButton( new GuiInfinityButton( 202, this.width / 2 + 30, this.height - 25, 60, 20, I18n.format( "gui.drop" ) ) );
        
        redSlider = this.addButton( new GuiSlider( 300, this.width / 2 - 80, this.height / 2 - 50, 160, 20, "Red: ", "", 0d, 255d, NBTHelper.ColorNBTHelper.getRed( stack ), false, true, new ISlider() {
            @Override
            public void onChangeSliderValue( GuiSlider slider )
            {
                NBTHelper.ColorNBTHelper.setRed( stack, slider.getValueInt() );
                hexText.setText( "#" + Integer.toHexString( NBTHelper.ColorNBTHelper.getColorAsInt( stack ) ) );
            }
        } ) );
        
        greenSlider = this.addButton( new GuiSlider( 301, this.width / 2 - 80, this.height / 2 - 10, 160, 20, "Green: ", "", 0d, 255d, NBTHelper.ColorNBTHelper.getGreen( stack ), false, true, new ISlider() {
            @Override
            public void onChangeSliderValue( GuiSlider slider )
            {
                NBTHelper.ColorNBTHelper.setGreen( stack, slider.getValueInt() );
                hexText.setText( "#" + Integer.toHexString( NBTHelper.ColorNBTHelper.getColorAsInt( stack ) ) );
            }
        } ) );
        
        blueSlider = this.addButton( new GuiSlider( 302, this.width / 2 - 80, this.height / 2 + 30, 160, 20, "Blue: ", "", 0d, 255d, NBTHelper.ColorNBTHelper.getBlue( stack ), false, true, new ISlider() {
            @Override
            public void onChangeSliderValue( GuiSlider slider )
            {
                NBTHelper.ColorNBTHelper.setBlue( stack, slider.getValueInt() );
                hexText.setText( "#" + Integer.toHexString( NBTHelper.ColorNBTHelper.getColorAsInt( stack ) ) );
            }
        } ) );
    }
    
    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        
        Keyboard.enableRepeatEvents( false );
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
        else
        {
            hexText.textboxKeyTyped( typedChar, keyCode );
        }
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        
        hexText.mouseClicked( mouseX, mouseY, mouseButton );
        
        if ( mouseButton == 0 && HelperGui.isMouseInRegion( mouseX, mouseY, blueSlider.x, blueSlider.y + blueSlider.height + 10, 160, 40 ) )
        {
            int horizontal = (mouseX - blueSlider.x) / 20;
            int vertical = 8 * ((mouseY - (blueSlider.y + blueSlider.height + 10)) / 20);
            NBTHelper.ColorNBTHelper.addDye( stack, EnumDyeColor.values()[horizontal + vertical] );
            
            hexText.setText( Integer.toHexString( NBTHelper.ColorNBTHelper.getColorAsInt( stack ) ) );
            
            redSlider.setValue( NBTHelper.ColorNBTHelper.getRed( stack ) );
            redSlider.updateSlider();
            greenSlider.setValue( NBTHelper.ColorNBTHelper.getGreen( stack ) );
            greenSlider.updateSlider();
            blueSlider.setValue( NBTHelper.ColorNBTHelper.getBlue( stack ) );
            blueSlider.updateSlider();
        }
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
        
        else if ( button.id == resetButton.id )
        {
            if ( stack.hasTagCompound() )
            {
                stack.setTagCompound( new NBTTagCompound() );
            }
        }
        
        else if ( button.id == dropButton.id )
        {
            HelperGui.dropStack( stack );
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
        
        String s = stack.hasTagCompound() ? stack.getTagCompound().toString() : "{}";
        
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse( s );
        s = gson.toJson( je );
        
        prettyNBTList.clear();
        for ( String str : s.split( "\\n" ) )
        {
            prettyNBTList.add( str );
        }
        
        drawHoveringText( prettyNBTList, 0, this.height );
        GlStateManager.popMatrix();
        
        // drawRect(width/3, height/3, width*2/3, height*2/3, NBTHelper.ColorNBTHelper.getColorAsInt(stack));
        
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();
        GlStateManager.scale( 5, 5, 1 );
        this.itemRender.renderItemAndEffectIntoGUI( stack, (this.width / 10) - 8, 8 );
        
        GlStateManager.scale( 0.2, 0.2, 1 );
        int j = 0;
        for ( EnumDyeColor c : EnumDyeColor.values() )
        {
            this.itemRender.renderItemAndEffectIntoGUI( new ItemStack( Items.DYE, 1, c.getDyeDamage() ), 2 + blueSlider.x + 20 * (j % 8), 2 + blueSlider.y + blueSlider.height + 10 + 20 * (j / 8) );
            j++;
        }
        
        GlStateManager.popMatrix();
        
        this.drawCenteredString( this.fontRenderer, this.title, this.width / 2, 15, HelperGui.MAIN_PURPLE );
        
        drawRect( redSlider.x - 5, redSlider.y - 5, blueSlider.x + blueSlider.width + 5, blueSlider.y + blueSlider.height + 5, HelperGui.getColorFromRGB( 100, redSlider.getValueInt(), greenSlider.getValueInt(), blueSlider.getValueInt() ) );
        
        drawRect( redSlider.x - 2, redSlider.y - 2, redSlider.x + redSlider.width + 2, redSlider.y + redSlider.height + 2, HelperGui.getColorFromRGB( 255, redSlider.getValueInt(), 0, 0 ) );
        drawRect( greenSlider.x - 2, greenSlider.y - 2, greenSlider.x + greenSlider.width + 2, greenSlider.y + greenSlider.height + 2, HelperGui.getColorFromRGB( 255, 0, greenSlider.getValueInt(), 0 ) );
        drawRect( blueSlider.x - 2, blueSlider.y - 2, blueSlider.x + blueSlider.width + 2, blueSlider.y + blueSlider.height + 2, HelperGui.getColorFromRGB( 255, 0, 0, blueSlider.getValueInt() ) );
        
        int size = 20;
        
        int i = 0;
        for ( EnumDyeColor c : EnumDyeColor.values() )
        {
            drawRect( blueSlider.x + size * (i % 8), blueSlider.y + blueSlider.height + 10 + size * (i / 8), blueSlider.x + size * ((i % 8) + 1), blueSlider.y + blueSlider.height + 10 + size + size * (i / 8), 0x9f000000 + c.getColorValue() );
            i++;
        }
        
        hexText.drawTextBox();
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
