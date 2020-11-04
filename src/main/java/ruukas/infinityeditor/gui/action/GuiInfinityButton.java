package ruukas.infinityeditor.gui.action;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import ruukas.infinityeditor.data.InfinityConfig;
import ruukas.infinityeditor.gui.HelperGui;

public class GuiInfinityButton extends GuiButton
{
    
    public GuiInfinityButton(int buttonId, int x, int y, String buttonText) {
        super( buttonId, x, y, buttonText );
    }
    
    public GuiInfinityButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super( buttonId, x, y, widthIn, heightIn, buttonText );
    }
    
    @Override
    public void drawButton( Minecraft mc, int mouseX, int mouseY, float partialTicks )
    {
        if ( this.visible )
        {
            FontRenderer fontrenderer = mc.fontRenderer;
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getHoverState( this.hovered );
            drawRect( x, y, x + width, y + height, InfinityConfig.ALT_COLOR );
            // Light shade
            drawRect( x, y, x + width, y + 1, HelperGui.getColorFromRGB( 26, 255, 255, 255 ) );
            drawRect( x, y + 1, x + 1, y + height, HelperGui.getColorFromRGB( 26, 255, 255, 255 ) );
            // Dark shade
            drawRect( x, y + height - 1, x + width, y + height, HelperGui.getColorFromRGB( 50, 0, 0, 0 ) );
            drawRect( x + width - 1, y, x + width, y + height, HelperGui.getColorFromRGB( 50, 0, 0, 0 ) );
            this.mouseDragged( mc, mouseX, mouseY );
            int color = InfinityConfig.MAIN_COLOR;
            
            switch ( i )
            {
                case 0:
                    color = HelperGui.BAD_RED;
                    break;
                case 2:
                    color = InfinityConfig.CONTRAST_COLOR;
                default:
                    break;
            }
            
            this.drawCenteredString( fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, color );
        }
    }
}
