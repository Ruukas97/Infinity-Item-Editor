package ruukas.infinityeditor.gui;

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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import ruukas.infinityeditor.InfinityEditor;
import ruukas.infinityeditor.data.InfinityConfig;
import ruukas.infinityeditor.gui.action.GuiActionTextField;
import ruukas.infinityeditor.gui.action.GuiInfinityButton;
import ruukas.infinityeditor.nbt.NBTHelper.ColorNBTHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

@SideOnly( Side.CLIENT )
public class GuiColor extends GuiScreen
{
    
    private final ItemStack stack;
    
    private final GuiScreen lastScreen;
    
    private GuiInfinityButton backButton, resetButton, dropButton;
    
    private GuiSlider redSlider, greenSlider, blueSlider;
    
    private GuiActionTextField hexText;

    private GuiInfinityButton randomButton;
    
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
        int buttonWidth = 60;
        randomButton = addButton(new GuiInfinityButton(102, (this.width - buttonWidth) / 2, this.height / 2 + 65, buttonWidth, 20, "Random"));
        hexText = new GuiActionTextField( 100, this.fontRenderer, (this.width / 2) - 25, this.height / 2 - 85, 50, 20 );
        hexText.setMaxStringLength( 7 );
        String hexS = Integer.toHexString( ColorNBTHelper.getColorAsInt( stack ) );
        StringBuilder zeroes = new StringBuilder();
        for ( int i = 0 ; i < 6 - hexS.length() ; i++ )
        {
            zeroes.append("0");
        }
        hexText.setText( "#" + zeroes + hexS );
        hexText.action = () -> {
            if(!(redSlider.dragging || greenSlider.dragging || blueSlider.dragging)){
                String text = hexText.getText();
                int length = text.length();
                
                if (length == 6 && !text.startsWith( "#" )){
                    hexText.setText( "#" + text );
                }
                else if ( length == 7 )
                {
                    text = text.substring( 1 );
                }
                else return;

                updateColor(text);
            }
        };
        
        backButton = addButton( new GuiInfinityButton( 200, this.width / 2 - 90, this.height - 25, 60, 20, I18n.format( "gui.back" ) ) );
        resetButton = addButton( new GuiInfinityButton( 201, this.width / 2 - 30, this.height - 25, 60, 20, I18n.format( "gui.reset" ) ) );
        dropButton = addButton( new GuiInfinityButton( 202, this.width / 2 + 30, this.height - 25, 60, 20, I18n.format( "gui.drop" ) ) );
        
        redSlider = this.addButton( new GuiSlider( 300, this.width / 2 - 80, this.height / 2 - 50, 160, 20, "Red: ", "", 0d, 255d, ColorNBTHelper.getRed( stack ), false, true, slider -> {
            ColorNBTHelper.setRed( stack, slider.getValueInt() );
            String hexS1 = Integer.toHexString( ColorNBTHelper.getColorAsInt( stack ) );
            StringBuilder zeroes1 = new StringBuilder();
            for (int i = 0; i < 6 - hexS1.length() ; i++ )
            {
                zeroes1.append("0");
            }
            hexText.setText( "#" + zeroes1 + hexS1);
        }) );
        
        greenSlider = this.addButton( new GuiSlider( 301, this.width / 2 - 80, this.height / 2 - 10, 160, 20, "Green: ", "", 0d, 255d, ColorNBTHelper.getGreen( stack ), false, true, slider -> {
            ColorNBTHelper.setGreen( stack, slider.getValueInt() );
            String hexS12 = Integer.toHexString( ColorNBTHelper.getColorAsInt( stack ) );
            StringBuilder zeroes12 = new StringBuilder();
            for (int i = 0; i < 6 - hexS12.length() ; i++ )
            {
                zeroes12.append("0");
            }
            hexText.setText( "#" + zeroes12 + hexS12);
        }) );
        
        blueSlider = this.addButton( new GuiSlider( 302, this.width / 2 - 80, this.height / 2 + 30, 160, 20, "Blue: ", "", 0d, 255d, ColorNBTHelper.getBlue( stack ), false, true, slider -> {
            ColorNBTHelper.setBlue( stack, slider.getValueInt() );
            String hexS13 = Integer.toHexString( ColorNBTHelper.getColorAsInt( stack ) );
            StringBuilder zeroes13 = new StringBuilder();
            for (int i = 0; i < 6 - hexS13.length() ; i++ )
            {
                zeroes13.append("0");
            }
            hexText.setText( "#" + zeroes13 + hexS13);
        }) );
    }

    private void updateColor(String text) {
        try
        {
            ColorNBTHelper.setColor( stack, (int) Long.parseLong(text, 16 ) );
        }
        catch ( NumberFormatException e )
        {
            InfinityEditor.logger.error( "Could not parse " + text + " as a hex color." );
        }
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
        
        if ( ColorNBTHelper.isPotion( stack ) && mouseButton == 0 && HelperGui.isMouseInRegion( mouseX, mouseY, blueSlider.x, blueSlider.y + blueSlider.height + 10, 160, 40 ) )
        {
            int horizontal = (mouseX - blueSlider.x) / 20;
            int vertical = 8 * ((mouseY - (blueSlider.y + blueSlider.height + 10)) / 20);
            ColorNBTHelper.addDye( stack, EnumDyeColor.values()[horizontal + vertical] );
            
            hexText.setText( Integer.toHexString( ColorNBTHelper.getColorAsInt( stack ) ) );
            
            redSlider.setValue( ColorNBTHelper.getRed( stack ) );
            redSlider.updateSlider();
            greenSlider.setValue( ColorNBTHelper.getGreen( stack ) );
            greenSlider.updateSlider();
            blueSlider.setValue( ColorNBTHelper.getBlue( stack ) );
            blueSlider.updateSlider();
        }
    }
    
    @Override
    protected void actionPerformed( GuiButton button )
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

        else if ( button.id == randomButton.id )
        {
            Random random = new Random();
            int nextInt = random.nextInt(0xffffff + 1);
            String randomColor = String.format("%06x", nextInt);
            updateColor(randomColor);
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
        GlStateManager.scale( 4, 4, 1 );
        this.itemRender.renderItemAndEffectIntoGUI( stack, (this.width / 8) - 8, 5 );
        
        GlStateManager.scale( 0.25, 0.25, 1 );
        
        if ( !ColorNBTHelper.isPotion( stack ) )
        {
            int j = 0;
            for ( EnumDyeColor c : EnumDyeColor.values() )
            {
                this.itemRender.renderItemAndEffectIntoGUI( new ItemStack( Items.DYE, 1, c.getDyeDamage() ), 2 + blueSlider.x + 20 * (j % 8), 2 + blueSlider.y + blueSlider.height + 10 + 20 * (j / 8) );
                j++;
            }
        }
        
        GlStateManager.popMatrix();
        
        this.drawCenteredString( this.fontRenderer, this.title, this.width / 2, 15, InfinityConfig.MAIN_COLOR );
        
        drawRect( redSlider.x - 5, redSlider.y - 5, blueSlider.x + blueSlider.width + 5, blueSlider.y + blueSlider.height + 5, HelperGui.getColorFromRGB( 100, redSlider.getValueInt(), greenSlider.getValueInt(), blueSlider.getValueInt() ) );
        
        drawRect( redSlider.x - 2, redSlider.y - 2, redSlider.x + redSlider.width + 2, redSlider.y + redSlider.height + 2, HelperGui.getColorFromRGB( 255, redSlider.getValueInt(), 0, 0 ) );
        drawRect( greenSlider.x - 2, greenSlider.y - 2, greenSlider.x + greenSlider.width + 2, greenSlider.y + greenSlider.height + 2, HelperGui.getColorFromRGB( 255, 0, greenSlider.getValueInt(), 0 ) );
        drawRect( blueSlider.x - 2, blueSlider.y - 2, blueSlider.x + blueSlider.width + 2, blueSlider.y + blueSlider.height + 2, HelperGui.getColorFromRGB( 255, 0, 0, blueSlider.getValueInt() ) );
        
        if ( !ColorNBTHelper.isPotion( stack ) )
        {
            int size = 20;
            int i = 0;
            for ( EnumDyeColor c : EnumDyeColor.values() )
            {
                drawRect( blueSlider.x + size * (i % 8), blueSlider.y + blueSlider.height + 10 + size * (i / 8), blueSlider.x + size * ((i % 8) + 1), blueSlider.y + blueSlider.height + 10 + size + size * (i / 8), 0x9f000000 + c.getColorValue() );
                i++;
            }
        }
        randomButton.drawButton(mc, mouseX, mouseY, partialTicks);
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
