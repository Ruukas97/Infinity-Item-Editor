package ruukas.infinityeditor.gui;

import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinityeditor.data.InfinityConfig;
import ruukas.infinityeditor.gui.action.GuiInfinityButton;
import ruukas.infinityeditor.nbt.NBTHelper;

@SideOnly( Side.CLIENT )
public class GuiHead extends GuiScreen
{
    private final ItemStack stack;
    
    private final GuiScreen lastScreen;
    
    private GuiInfinityButton headButton, saveSkullButton, listButton;
    private GuiInfinityButton backButton, resetButton, dropButton;
    
    private GuiTextField skullOwnerTextField;
    
    protected String title = I18n.format( "gui.head" );
    
    protected ArrayList<String> prettyNBTList = new ArrayList<>();
    
    public GuiHead(GuiScreen lastScreen, ItemStack stack) {
        this.lastScreen = lastScreen;
        this.stack = stack;
    }
    
    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents( true );
        
        this.headButton = new GuiInfinityButton( 100, (this.width / 2) - 55, 50, 110, 20, I18n.format( "tag.head." + stack.getItemDamage() ) );
        this.headButton.enabled = false;
        this.addButton( this.headButton );
        this.addButton( new GuiInfinityButton( 101, (this.width / 2) - 75, 50, 20, 20, "<" ) );
        this.addButton( new GuiInfinityButton( 102, (this.width / 2) + 55, 50, 20, 20, ">" ) );
        
        skullOwnerTextField = new GuiTextField( 103, this.fontRenderer, (this.width / 2) - 54, 82, 88, 16 );
        GameProfile profile = NBTHelper.SkullNBTHelper.getSkullOwner( stack );
        skullOwnerTextField.setText( profile != null && profile.getName() != null ? profile.getName() : I18n.format( "tag.skullowner" ) );
        listButton = this.addButton( new GuiInfinityButton( 104, (this.width / 2) - 75, 80, 20, 20, "[L]" ) );
        listButton.enabled = false;
        saveSkullButton = this.addButton( new GuiInfinityButton( 105, (this.width / 2) + 35, 80, 40, 20, I18n.format( "gui.save" ) ) );
        
        backButton = addButton( new GuiInfinityButton( 200, this.width / 2 - 90, this.height - 25, 60, 20, I18n.format( "gui.back" ) ) );
        resetButton = addButton( new GuiInfinityButton( 201, this.width / 2 - 30, this.height - 25, 60, 20, I18n.format( "gui.reset" ) ) );
        dropButton = addButton( new GuiInfinityButton( 202, this.width / 2 + 30, this.height - 25, 60, 20, I18n.format( "gui.drop" ) ) );
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
        skullOwnerTextField.updateCursorCounter();
    }
    
    public void changeHeadType( boolean right )
    {
        if ( right )
        {
            stack.setItemDamage( (stack.getItemDamage() + 1) % 6 );
        }
        else
        {
            if ( stack.getItemDamage() == 0 )
            {
                stack.setItemDamage( 5 );
            }
            else
            {
                stack.setItemDamage( stack.getItemDamage() - 1 );
            }
        }
        
        headButton.displayString = I18n.format( "tag.head." + stack.getItemDamage() );
        
        boolean playerhead = stack.getItemDamage() == 3;
        skullOwnerTextField.setEnabled( playerhead );
        skullOwnerTextField.setVisible( playerhead );
        saveSkullButton.enabled = playerhead;
        saveSkullButton.visible = playerhead;
        listButton.enabled = false;
        listButton.visible = playerhead;
        
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
            this.actionPerformed( this.saveSkullButton );
        }
        else
        {
            skullOwnerTextField.textboxKeyTyped( typedChar, keyCode );
        }
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        
        skullOwnerTextField.mouseClicked( mouseX, mouseY, mouseButton );
    }
    
    @Override
    protected void actionPerformed( GuiButton button )
    {
        if ( button.id == 101 )
        {
            changeHeadType( false );
        }
        
        else if ( button.id == 102 )
        {
            changeHeadType( true );
        }
        
        else if ( button.id == saveSkullButton.id )
        {
            NBTHelper.SkullNBTHelper.setSkullOwner( stack, skullOwnerTextField.getText() );
        }
        
        else if ( button.id == backButton.id )
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
            if ( str.trim().startsWith( "\"Signature\": " ) )
            {
                prettyNBTList.add( "          \"Signature:\": (snip)" );
            }
            else if ( str.startsWith( "          \"Value\": " ) )
            {
                prettyNBTList.add( "          \"Value:\": (snip)" );
            }
            else
            {
                prettyNBTList.add( str );
                // System.out.println(str);
            }
        }
        
        drawHoveringText( prettyNBTList, 0, this.height );
        
        GlStateManager.popMatrix();
        
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();
        this.itemRender.renderItemAndEffectIntoGUI( stack, (this.width / 2) - 8, 30 );
        GlStateManager.popMatrix();
        
        this.drawCenteredString( this.fontRenderer, this.title, this.width / 2, 15, InfinityConfig.MAIN_COLOR );
        
        skullOwnerTextField.drawTextBox();
        
        super.drawScreen( mouseX, mouseY, partialTicks );
        
        HelperGui.addTooltipTranslated( saveSkullButton, mouseX, mouseY, I18n.format( "gui.head.save.tooltip" ) );
        HelperGui.addTooltipTranslated( listButton, mouseX, mouseY, I18n.format( "gui.head.list.tooltip" ) );
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
