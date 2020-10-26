package ruukas.infinity.gui.monsteregg;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly( Side.CLIENT )
public class GuiTagSlider extends GuiButton
{
    
    private float sliderValue;
    public boolean dragging;
    private final MobTagSlider tag;
    public ItemStack stack;
    
    public GuiTagSlider(int buttonId, int x, int y, MobTagSlider tag, ItemStack stack) {
        super( buttonId, x, y, 150, 20, "" );
        this.sliderValue = 1.0F;
        this.tag = tag;
        this.sliderValue = tag.normalizeValue( tag.getFloat( stack ) );
        this.displayString = MonsterPlacerUtils.getButtonText( tag, stack );
        this.stack = stack;
    }
    
    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over this button.
     */
    protected int getHoverState( boolean mouseOver )
    {
        return 0;
    }
    
    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged( Minecraft mc, int mouseX, int mouseY )
    {
        if ( this.visible )
        {
            if ( this.dragging )
            {
                this.sliderValue = (float) (mouseX - (this.x + 4)) / (float) (this.width - 8);
                this.sliderValue = MathHelper.clamp( this.sliderValue, 0.0F, 1.0F );
                float f = this.tag.denormalizeValue( this.sliderValue );
                tag.setValue( stack, f );
                this.sliderValue = this.tag.normalizeValue( f );
                // this.displayString = mc.gameSettings.getKeyBinding(this.tag);
                this.displayString = MonsterPlacerUtils.getButtonText( tag, stack );
            }
            
            mc.getTextureManager().bindTexture( BUTTON_TEXTURES );
            GlStateManager.color( 1.0F, 1.0F, 1.0F, 1.0F );
            this.drawTexturedModalRect( this.x + (int) (this.sliderValue * (float) (this.width - 8)), this.y, 0, 66, 4, 20 );
            this.drawTexturedModalRect( this.x + (int) (this.sliderValue * (float) (this.width - 8)) + 4, this.y, 196, 66, 4, 20 );
        }
    }
    
    /**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent e).
     */
    public boolean mousePressed( Minecraft mc, int mouseX, int mouseY )
    {
        if ( super.mousePressed( mc, mouseX, mouseY ) )
        {
            this.sliderValue = (float) (mouseX - (this.x + 4)) / (float) (this.width - 8);
            this.sliderValue = MathHelper.clamp( this.sliderValue, 0.0F, 1.0F );
            // mc.gameSettings.setOptionFloatValue(this.tag,
            // this.tag.denormalizeValue(this.sliderValue));
            tag.setValue( stack, sliderValue );
            // this.displayString = mc.gameSettings.getKeyBinding(this.tag);
            this.displayString = MonsterPlacerUtils.getButtonText( tag, stack );
            this.dragging = true;
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased( int mouseX, int mouseY )
    {
        this.dragging = false;
    }
}
