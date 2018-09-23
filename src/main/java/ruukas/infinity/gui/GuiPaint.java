package ruukas.infinity.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinity.nbt.itemstack.tag.infinity.InfinityTextureTag;

@SideOnly( Side.CLIENT )
public class GuiPaint extends GuiInfinity
{
    private InfinityTextureTag tag;
    private int currentColor;
    
    public GuiPaint(GuiScreen lastScreen, ItemStackHolder stackHolder) {
        super( lastScreen, stackHolder );
        tag = new InfinityTextureTag( getItemStack() );
        currentColor = 255;
    }
    
    public int getPixel( int x, int y )
    {
        int p = x + y * 16;
        
        int t = p % 4 * 8;
        int mask = 0x000000FF;
        
        return tag.getPixels()[p / 4] >>> t & mask;
    }
    
    public void setPixel( int x, int y, int value )
    {
        value = Math.min( 255, Math.max( 0, value ) );
        
        int p = x + y * 16;
        int t = p % 4 * 8;
        
        if ( x == 1 && y == 0 )
        {
            System.out.println( t );
        }
        
        int mask = 0x000000FF << t;
        
        int[] pixels = tag.getPixels();
        int pixWo = pixels[p / 4] - (pixels[p / 4] & mask);
        pixels[p / 4] = pixWo + (value << t);
        tag.setPixels( pixels );
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
    }
    
    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
    }
    
    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        
        int half = 256;
        int quarter = half / 2;
        int part = half / 16;
        
        int left = midX - quarter;
        int top = midY - quarter;
        
        if ( HelperGui.isMouseInRegion( mouseX, mouseY, left, top, half, half ) )
        {
            int x = (mouseX - (left)) / part;
            int y = (mouseY - (top)) / part;
            System.out.println( "x: " + x + ", y: " + y );
            System.out.println( "pixel: " + getPixel( x, y ) );
            setPixel( x, y, currentColor );
        }
        else if ( HelperGui.isMouseInRegion( mouseX, mouseY, left, top + half + 20, half, 10 ) )
        {
            currentColor = (int) (((mouseX - left) / (double) half) * 255);
        }
    }
    
    @Override
    protected void mouseClickMove( int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick )
    {
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        super.drawScreen( mouseX, mouseY, partialTicks );
        
        int half = 256;
        int quarter = half / 2;
        int part = half / 16;
        
        int left = midX - quarter;
        int top = midY - quarter;
        
        for ( int i = 0 ; i < 16 ; i++ )
        {
            for ( int j = 0 ; j < 16 ; j++ )
            {
                int pixel = getPixel( i, j );
                int pixel24 = 0xFF000000 + ((pixel & 0xE0) << 16) | ((pixel & 0x1C) << 11) | ((pixel & 0x03) << 6);
                drawRect( left + part * i, top + part * j, left + part * (i + 1), top + part * (j + 1), pixel24 );
            }
        }
        
        double smallerPart = part / 16d;
        
        for ( int i = 0 ; i < 255 ; i++ )
        {
            int pixel24 = 0xFF000000 + ((i & 0xE0) << 16) | ((i & 0x1C) << 11) | ((i & 0x03) << 6);
            drawRect( left + (int) (smallerPart * i), top + half + 20, left + (int) (smallerPart * (i + 1)), top + half + 30, pixel24 );
        }
        
        // drawRect( midX - quarter, midY - quarter, midX + quarter, midY + quarter, HelperGui.getColorFromRGB( 255, 50, 50, 50 ) );
    }
    
    @Override
    protected String getNameUnlocalized()
    {
        return "paint";
    }
}
