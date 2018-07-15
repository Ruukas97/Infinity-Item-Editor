package ruukas.infinity.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinity.gui.action.GuiNumberField;
import ruukas.infinity.nbt.itemstack.tag.InfinityEnchantmentList;
import ruukas.infinity.nbt.itemstack.tag.ench.InfinityEnchantmentTag;

@SideOnly( Side.CLIENT )
public class GuiEnchanting extends GuiInfinity
{            
    private GuiNumberField level;
    
    private int rotOff = 0;
    private int mouseDist = 0;
    private List<Enchantment> enchants = new ArrayList<>();
    private ItemStack enchantBook;
        
    public GuiEnchanting(GuiScreen lastScreen, ItemStack stack) {
    	super(lastScreen, stack);
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
    	
        Keyboard.enableRepeatEvents( true );
        
        level = new GuiNumberField( 100, fontRenderer, 15, height - 33, 40, 18, 5 );
        level.minValue = 1;
        level.maxValue = 32767;
        level.setValue( 1 );
        
        
        enchants.clear();
        for ( Enchantment e : Enchantment.REGISTRY )
        {
            if ( e.canApply( stack ) )
            {
                enchants.add( e );
            }
        }
        
        enchantBook = new ItemStack( Items.ENCHANTED_BOOK );
        
        if ( !enchants.isEmpty() )
        {
            EnchantmentData dat = new EnchantmentData( enchants.get( 0 ), 1 );
            ItemEnchantedBook.addEnchantment( enchantBook, dat );
        }
    }
    
    @Override
    public void onGuiClosed()
    {
    	super.onGuiClosed();
        Keyboard.enableRepeatEvents( false );
    }
    
    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
    	super.updateScreen();
        level.updateCursorCounter();
        if ( Math.abs( mouseDist - (height / 3) ) >= 16 )
            rotOff++;
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
            level.textboxKeyTyped( typedChar, keyCode );
        }
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        
        level.mouseClicked( mouseX, mouseY, mouseButton );
        
        InfinityEnchantmentList list = new InfinityEnchantmentList( stack );
        InfinityEnchantmentTag[] activeEnchants = list.getAll();
        int start = midY - 5 * activeEnchants.length;
        if ( activeEnchants.length > 0 && HelperGui.isMouseInRegion( mouseX, mouseY, 0, start, 5 + fontRenderer.getStringWidth( "Unbreaking 32767" ), 10 * activeEnchants.length ) )
        {
            list.removeEnchantment( (mouseY - start) / 10 );
            return;
        }
        
        int r = height / 3;

        // mouseDist = (int) Math.sqrt(distX * distX + distY * distY);
        if ( Math.abs( mouseDist - r ) < 16 )
        {
            double angle = (2 * Math.PI) / enchants.size();

            int lowDist = Integer.MAX_VALUE;
            Enchantment enchantment = null;
            
            for ( int i = 0 ; i < enchants.size() ; i++ )
            {
                double angleI = (((double) (rotOff) / 60d)) + (angle * i);
                
                int x = (int) (midX + (r * Math.cos( angleI )));
                int y = (int) (midY + (r * Math.sin( angleI )));
                int distX = x - mouseX;
                int distY = y - mouseY;
                
                int dist = (int) Math.sqrt( distX * distX + distY * distY );
                
                if ( dist < 10 && dist < lowDist )
                {
                    lowDist = dist;
                    enchantment = enchants.get( i );
                }
            }
            
            if ( enchantment != null )
            {
                new InfinityEnchantmentList( stack ).set( enchantment, (short) (enchantment.getMaxLevel() == 1 ? 1 : level.getIntValue()) );
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

        InfinityEnchantmentTag[] enchantmentTags = new InfinityEnchantmentList( stack ).getAll();
        for ( int i = 0 ; i < enchantmentTags.length ; i++ )
        {
            InfinityEnchantmentTag e = enchantmentTags[i];
            drawString( fontRenderer, e.getEnchantment().getTranslatedName( e.getLevel() ).replace( "enchantment.level.", "" ), 5, midY + i * 10 - enchantmentTags.length * 5, HelperGui.MAIN_PURPLE );
        }
        
        level.drawTextBox();
                
        
        int distX = midX - mouseX;
        int distY = midY - mouseY;
        mouseDist = (int) Math.sqrt( distX * distX + distY * distY );
        
        int r = height / 3;
        
        double angle = (2 * Math.PI) / enchants.size();
        
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();
        
        GlStateManager.scale( 5, 5, 1 );
        GlStateManager.translate( (width / 10), (height / 10), 0 );
        GlStateManager.rotate( rotOff * 3, 0.0f, 0.0f, -1.0f );
        this.itemRender.renderItemAndEffectIntoGUI( stack, -8, -8 );
        GlStateManager.rotate( rotOff * 3, 0.0f, 0.0f, 1.0f );
        GlStateManager.translate( -(width / 10), -(height / 10), 0 );
        
        GlStateManager.scale( 0.2, 0.2, 1 );

        for ( int i = 0 ; i < enchants.size() ; i++ )
        {
            double angleI = (((double) (rotOff + (double) (Math.abs( mouseDist - r ) >= 16 ? partialTicks : 0d)) / 60d)) + (angle * i);
            int x = (int) (midX + (r * Math.cos( angleI )));
            int y = (int) (midY + (r * Math.sin( angleI )));
            GlStateManager.translate( 0, 0, 300 );
            this.drawCenteredString( this.fontRenderer, TextFormatting.getTextWithoutFormattingCodes( enchants.get( i ).getTranslatedName( enchants.get( i ).getMaxLevel() == 1 ? 1 : level.getIntValue() ).replace( "enchantment.level.", "" ) ), x, y - 17, HelperGui.MAIN_PURPLE );
            GlStateManager.translate( 0, 0, -300 );

            this.itemRender.renderItemAndEffectIntoGUI( enchantBook, x - 8, y - 8 );
            
            drawRect(x-1, y-1, x+1, y+1, HelperGui.getColorFromRGB(255, 255, 255, 255));
        }
        GlStateManager.popMatrix();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
    }
 

	@Override
	protected String getNameUnlocalized() {
		return "enchanting";
	}
}
