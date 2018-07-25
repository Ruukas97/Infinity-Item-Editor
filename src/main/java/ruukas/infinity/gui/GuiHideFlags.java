package ruukas.infinity.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class GuiHideFlags extends GuiInfinity
{
    
    private static enum Flags {
        ENCHANTMENTS( 1, "flag.enchantment" ), ATTRIBUTEMODIFIERS( 2, "flag.attributemod" ), UNBREAKABLE( 4, "flag.unbreakable" ), CANDESTROY( 8, "flag.candestroy" ), CANPLACEON( 16, "flag.canplaceon" ), ITEMINFO( 32, "flag.iteminfo" );
        
        private int denom;
        private String key;
        
        private Flags(int denom, String key) {
            this.denom = denom;
            this.key = key;
        }
        
        public int getDenom()
        {
            return denom;
        }
        
        public String getKey()
        {
            return key;
        }
        
        public String getTranslatedName()
        {
            return I18n.format( key );
        }
        
        public boolean hidden( int value )
        {
            return (value & denom) == 0;
        }
    }
    
    protected GuiHideFlags(GuiScreen lastScreen, ItemStack stack) {
        super( lastScreen, stack );
        
        renderTooltip = true;
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
        setRenderStack( true, midX, 40, 1 );

    }
    
    @Override
    protected void mouseClicked( int mouseX, int mouseY, int mouseButton ) throws IOException
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        
    }
    
    @Override
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        super.drawScreen( mouseX, mouseY, partialTicks );
        
        int i = 0;
        int size = 6;
        
        for(Flags f : Flags.values()){
            //drawCenteredString( fontRendererIn, text, x, y, color );
        }
    }
    
    @Override
    protected String getNameUnlocalized()
    {
        return "hideflags";
    }
    
}
