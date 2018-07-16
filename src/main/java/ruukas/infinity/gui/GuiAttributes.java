package ruukas.infinity.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinity.gui.action.GuiInfinityButton;
import ruukas.infinity.gui.action.GuiNumberField;
import ruukas.infinity.nbt.itemstack.tag.InfinityAttributeModifierList;
import ruukas.infinity.nbt.itemstack.tag.attributemodifiers.InfinityAttributeModifierTag;

@SideOnly( Side.CLIENT )
public class GuiAttributes extends GuiInfinity
{
    private static final ItemStack note = new ItemStack( Items.PAPER );
    
    private static final IAttribute playerReach = EntityPlayer.REACH_DISTANCE;
    private static final IAttribute parrotFlying = SharedMonsterAttributes.FLYING_SPEED;
    // private IAttribute horseJump = EntityHorse.JUMP_STRENGTH (It's protected)
    // private static final IAttribute zombieReinforcements = EntityZombie.SPAWN_REINFORCEMENTS_CHANCE; (protected)
    
    private static final IAttribute[] sharedAttributes = { SharedMonsterAttributes.MAX_HEALTH, SharedMonsterAttributes.FOLLOW_RANGE, SharedMonsterAttributes.KNOCKBACK_RESISTANCE, SharedMonsterAttributes.MOVEMENT_SPEED, SharedMonsterAttributes.ATTACK_DAMAGE, SharedMonsterAttributes.ATTACK_SPEED, SharedMonsterAttributes.ARMOR, SharedMonsterAttributes.ARMOR_TOUGHNESS, SharedMonsterAttributes.LUCK, playerReach, parrotFlying };
    
    private GuiNumberField level;
    private GuiNumberField levelDecimal;
    private GuiInfinityButton slotButton, operationButton;
    
    private GuiInfinityButton negativeButton;
    private boolean negativeAmount = false;
    
    private int rotOff = 0;
    private int mouseDist = 0;
    private int slot = 0, operation = 0;
    
    public GuiAttributes(GuiScreen lastScreen, ItemStack stack) {
        super( lastScreen, stack );
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
        
        Keyboard.enableRepeatEvents( true );
        
        operationButton = addButton( new GuiInfinityButton( 250, 15, height - 93, 80, 20, I18n.format( "gui.attributes.operation." + operation ) ) );
        
        slotButton = addButton( new GuiInfinityButton( 251, 15, height - 63, 80, 20, I18n.format( "gui.attributes.slot." + slot ) ) );
        
        negativeButton = addButton( new GuiInfinityButton( 252, 15, height - 33, 20, 20, negativeAmount ? "-" : "+" ) );
        
        if ( level != null )
        {
            level.y = height - 32;
        }
        else
        {
            level = new GuiNumberField( 100, fontRenderer, 38, height - 32, 55, 18, 8 );
            level.minValue = 0;
            level.maxValue = 99999999;
            level.setValue( 0 );
        }
        
        if ( levelDecimal != null )
        {
            levelDecimal.y = height - 32;
        }
        else
        {
            levelDecimal = new GuiNumberField( 101, fontRenderer, 100, height - 32, 25, 18, 3 );
            levelDecimal.minValue = 0;
            levelDecimal.maxValue = 999;
            levelDecimal.setValue( 0 );
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
        levelDecimal.updateCursorCounter();
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
            levelDecimal.textboxKeyTyped( typedChar, keyCode );
        }
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        
        level.mouseClicked( mouseX, mouseY, mouseButton );
        levelDecimal.mouseClicked( mouseX, mouseY, mouseButton );
        
        InfinityAttributeModifierList list = new InfinityAttributeModifierList( stack );
        InfinityAttributeModifierTag[] activeModifiers = list.getAll();
        
        int start = midY - 5 * activeModifiers.length;
        if ( activeModifiers.length > 0 && HelperGui.isMouseInRegion( mouseX, mouseY, 0, start, 5 + fontRenderer.getStringWidth( "Unbreaking 32767" ), 10 * activeModifiers.length ) )
        {
            list.removeModifier( (mouseY - start) / 10 );
            return;
        }
        
        int r = height / 3;
        
        // mouseDist = (int) Math.sqrt(distX * distX + distY * distY);
        if ( Math.abs( mouseDist - r ) < 16 )
        {
            double angle = (2 * Math.PI) / sharedAttributes.length;
            
            int lowDist = Integer.MAX_VALUE;
            IAttribute attribute = null;
            
            for ( int i = 0 ; i < sharedAttributes.length ; i++ )
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
                    attribute = sharedAttributes[i];
                }
            }
            
            if ( attribute != null )
            {
                double amount = (negativeAmount ? -1.0d : 1.0d) * (((double) level.getIntValue()) + (((double) levelDecimal.getIntValue()) / 1000));
                
                InfinityAttributeModifierTag tag = new InfinityAttributeModifierTag( new InfinityAttributeModifierList( stack ), new AttributeModifier( attribute.getName(), amount, 0 ) );
                tag.setOperation( operation );
                tag.setSlot( slot );
            }
        }
    }
    
    @Override
    protected void actionPerformed( GuiButton button ) throws IOException
    {
        if ( button.id == operationButton.id )
        {
            operation = (operation + 1) % 3;
            operationButton.displayString = I18n.format( "gui.attributes.operation." + operation );
        }
        else if ( button.id == slotButton.id )
        {
            slot = (slot + 1) % 7;
            slotButton.displayString = I18n.format( "gui.attributes.slot." + slot );
        }
        else if ( button.id == negativeButton.id )
        {
            negativeAmount = !negativeAmount;
            initGui();
        }
        else
            super.actionPerformed( button );
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        super.drawScreen( mouseX, mouseY, partialTicks );
        
        InfinityAttributeModifierTag[] modifierTags = new InfinityAttributeModifierList( stack ).getAll();
        for ( int i = 0 ; i < modifierTags.length ; i++ )
        {
            InfinityAttributeModifierTag m = modifierTags[i];
            drawString( fontRenderer, m.getDisplayString(), 5, midY + i * 10 - modifierTags.length * 5, HelperGui.MAIN_PURPLE );
        }
        
        level.drawTextBox();
        levelDecimal.drawTextBox();
        drawString( fontRenderer, ".", 96, height - 26, HelperGui.MAIN_PURPLE );
        
        int distX = midX - mouseX;
        int distY = midY - mouseY;
        mouseDist = (int) Math.sqrt( distX * distX + distY * distY );
        
        int r = height / 3;
        
        double angle = (2 * Math.PI) / sharedAttributes.length;
        
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
        
        for ( int i = 0 ; i < sharedAttributes.length ; i++ )
        {
            double angleI = (((double) (rotOff + (double) (Math.abs( mouseDist - r ) >= 16 ? partialTicks : 0d)) / 60d)) + (angle * i);
            int x = (int) (midX + (r * Math.cos( angleI )));
            int y = (int) (midY + (r * Math.sin( angleI )));
            this.drawCenteredString( this.fontRenderer, I18n.format( "attribute.name." + sharedAttributes[i].getName() ), x, y - 17, HelperGui.MAIN_PURPLE );
            
            this.itemRender.renderItemAndEffectIntoGUI( note, x - 8, y - 8 );
            
            drawRect( x - 1, y - 1, x + 1, y + 1, HelperGui.getColorFromRGB( 255, 255, 255, 255 ) );
        }
        GlStateManager.popMatrix();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
    }
    
    @Override
    protected String getNameUnlocalized()
    {
        return "attributes";
    }
}
