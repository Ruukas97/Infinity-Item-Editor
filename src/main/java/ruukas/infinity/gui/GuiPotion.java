package ruukas.infinity.gui;

import java.io.IOException;
import java.util.Set;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinity.gui.action.GuiNumberField;
import ruukas.infinity.nbt.itemstack.tag.InfinityCustomPotionEffectList;
import ruukas.infinity.nbt.itemstack.tag.InfinityEnchantmentList;
import ruukas.infinity.nbt.itemstack.tag.custompotioneffects.InfinityPotionEffectTag;
import ruukas.infinity.nbt.itemstack.tag.ench.InfinityEnchantmentTag;

@SideOnly( Side.CLIENT )
public class GuiPotion extends GuiInfinity
{            
    private GuiNumberField level;
    private GuiNumberField time;
    
    private int rotOff = 0;
    private int mouseDist = 0;
    private ItemStack potionIcon;
        
    public GuiPotion(GuiScreen lastScreen, ItemStack stack) {
    	super(lastScreen, stack);
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
    	
        Keyboard.enableRepeatEvents( true );
        
        level = new GuiNumberField( 100, fontRenderer, 15, height - 33, 40, 18, 3 );
        level.minValue = 1;
        level.maxValue = 127;
        level.setValue( 1 );
        
        time = new GuiNumberField( 100, fontRenderer, 15, height - 60, 40, 18, 5 );
        time.minValue = 1;
        time.maxValue = 99999;
        time.setValue( 1 );

        
        potionIcon = new ItemStack( Items.POTIONITEM );
        
        PotionUtils.addPotionToItemStack(potionIcon, PotionType.REGISTRY.getObjectById(0));
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
        time.updateCursorCounter();
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
            time.textboxKeyTyped( typedChar, keyCode );
        }
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        
        level.mouseClicked( mouseX, mouseY, mouseButton );
        time.mouseClicked( mouseX, mouseY, mouseButton );
        
        InfinityCustomPotionEffectList list = new InfinityCustomPotionEffectList( stack );
        InfinityPotionEffectTag[] activeEffects = list.getAll();
        int start = midY - 5 * activeEffects.length;
        if ( activeEffects.length > 0 && HelperGui.isMouseInRegion( mouseX, mouseY, 0, start, 5 + fontRenderer.getStringWidth( "Unbreaking 32767" ), 10 * activeEffects.length ) )
        {
            list.removePotionEffect( (mouseY - start) / 10 );
            return;
        }
        
        int r = height / 3;

        // mouseDist = (int) Math.sqrt(distX * distX + distY * distY);
        if ( Math.abs( mouseDist - r ) < 16 )
        {
        	Set<ResourceLocation> keyset = Potion.REGISTRY.getKeys();
            double angle = (2 * Math.PI) / keyset.size();

            int lowDist = Integer.MAX_VALUE;
            Potion type = null;
            
            int i = 0;
            for (ResourceLocation key : keyset)
            {
                double angleI = (((double) (rotOff) / 60d)) + (angle * i++);
                
                int x = (int) (midX + (r * Math.cos( angleI )));
                int y = (int) (midY + (r * Math.sin( angleI )));
                int distX = x - mouseX;
                int distY = y - mouseY;
                
                int dist = (int) Math.sqrt( distX * distX + distY * distY );
                
                if ( dist < 10 && dist < lowDist )
                {
                    lowDist = dist;
                    type = Potion.REGISTRY.getObject(key);
                }
            }
            
            if ( type != null )
            {
                new InfinityCustomPotionEffectList( stack ).set(new PotionEffect(type, time.getIntValue()));
            }
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
        time.drawTextBox();
                
        
        int distX = midX - mouseX;
        int distY = midY - mouseY;
        mouseDist = (int) Math.sqrt( distX * distX + distY * distY );
        
        int r = height / 3;
        
    	Set<ResourceLocation> keyset = Potion.REGISTRY.getKeys();
        double angle = (2 * Math.PI) / keyset.size();
        
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

        int i = 0;
        for (ResourceLocation key : keyset)
        {
            double angleI = (((double) (rotOff + (double) (Math.abs( mouseDist - r ) >= 16 ? partialTicks : 0d)) / 60d)) + (angle * i++);
            int x = (int) (midX + (r * Math.cos( angleI )));
            int y = (int) (midY + (r * Math.sin( angleI )));
            
            PotionEffect potEff = new PotionEffect( Potion.REGISTRY.getObject( key ), 20, level.getIntValue() );
            String displayString = I18n.format( potEff.getEffectName() );

            if (potEff.getAmplifier() == 1)
            {
                displayString = displayString + " " + I18n.format("enchantment.level.2");
            }
            else if (potEff.getAmplifier() == 2)
            {
                displayString = displayString + " " + I18n.format("enchantment.level.3");
            }
            else if (potEff.getAmplifier() == 3)
            {
                displayString = displayString + " " + I18n.format("enchantment.level.4");
            }
            
            this.drawCenteredString( fontRenderer, TextFormatting.getTextWithoutFormattingCodes( displayString ), x, y - 17, HelperGui.MAIN_PURPLE );

            this.itemRender.renderItemAndEffectIntoGUI( potionIcon, x - 8, y - 8 );
            
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
