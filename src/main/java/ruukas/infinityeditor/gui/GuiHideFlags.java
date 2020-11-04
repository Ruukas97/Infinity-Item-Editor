package ruukas.infinityeditor.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import ruukas.infinityeditor.gui.action.GuiInfinityButton;
import ruukas.infinityeditor.nbt.itemstack.InfinityItemTag;

public class GuiHideFlags extends GuiInfinity
{
    
    public static enum Flags {
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
        
        public String getTranslatedName( InfinityItemTag itemTag )
        {
            return I18n.format( key + "." + (itemTag.getFlagHidden( this ) ? "1" : "0") );
        }
        
        public boolean hidden( int value )
        {
            return (value & denom) > 0;
        }
    }
    
    protected GuiHideFlags(GuiScreen lastScreen, ItemStackHolder stackHolder) {
        super( lastScreen, stackHolder );
        
        renderTooltip = true;
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
        setRenderStack( true, midX, 40, 1 );
        
        InfinityItemTag itemTag = new InfinityItemTag( getItemStack() );
        int buttons = 0;
        for ( Flags f : Flags.values() )
        {
            addButton( new GuiInfinityButton( 300 + buttons, midX - 60, 60 + 30 * buttons++, 120, 20, f.getTranslatedName( itemTag ) ) );
        }
    }
    
    @Override
    protected void actionPerformed( GuiButton button ) throws IOException
    {
        if ( button.id >= 300 && button.id < 300 + Flags.values().length )
        {
            new InfinityItemTag( getItemStack() ).switchFlag( Flags.values()[button.id - 300] );
            initGui();
        }
        
        super.actionPerformed( button );
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
    }
    
    @Override
    protected String getNameUnlocalized()
    {
        return "hideflags";
    }
    
}
