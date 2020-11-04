package ruukas.infinityeditor.gui.monsteregg;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly( Side.CLIENT )
public class GuiTagRowList extends GuiListExtended
{
    
    private final List<GuiTagRowList.Row> rows = Lists.<GuiTagRowList.Row>newArrayList();
    public final ItemStack stack;
    public final GuiScreen parentScreen;
    
    public GuiTagRowList(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn, GuiScreen parentScreen, ItemStack stack, MobTag... mobTags) {
        super( mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn );
        this.centerListVertically = false;
        
        this.stack = stack;
        this.parentScreen = parentScreen;
        
        for ( int i = 0 ; i < mobTags.length ; i += 2 )
        {
            MobTag tags = mobTags[i];
            MobTag tags1 = i < mobTags.length - 1 ? mobTags[i + 1] : null;
            GuiButton guibutton = this.createButton( i + 1, widthIn / 2 - 155, 0, tags );
            GuiButton guibutton1 = this.createButton( i + 2, widthIn / 2 - 155 + 160, 0, tags1 );
            this.rows.add( new Row( guibutton, guibutton1, this ) );
        }
        
    }
    
    private GuiButton createButton( int id, int x, int y, MobTag mobTag )
    {
        if ( mobTag == null )
        {
            return null;
        }
        else
        {
            if ( mobTag instanceof MobTagSlider )
            {
                return new GuiTagSlider( id, x, y, (MobTagSlider) mobTag, stack );
            }
            else if ( mobTag instanceof MobTagToggle )
            {
                return new GuiTagButton( id, x, y, mobTag, MonsterPlacerUtils.getButtonText( mobTag, stack ) );
            }
            else if ( mobTag instanceof MobTagString )
            {
                return new GuiTagButtonString( id, x, y, (MobTagString) mobTag, this.parentScreen, stack );
            }
            return new GuiTagButton( id, x, y, mobTag, MonsterPlacerUtils.getButtonText( mobTag, stack ) );
        }
    }
    
    /**
     * Gets the IGuiListEntry object for the given index
     */
    public GuiTagRowList.Row getListEntry( int index )
    {
        return this.rows.get( index );
    }
    
    protected int getSize()
    {
        return this.rows.size();
    }
    
    /**
     * Gets the width of the list
     */
    public int getListWidth()
    {
        return 400;
    }
    
    protected int getScrollBarX()
    {
        return super.getScrollBarX() + 32;
    }
    
    @SideOnly( Side.CLIENT )
    public static class Row implements GuiListExtended.IGuiListEntry
    {
        
        private final Minecraft client = Minecraft.getMinecraft();
        private final GuiButton buttonA;
        private final GuiButton buttonB;
        private final GuiTagRowList listOn;
        private int releasedAmount = 0;
        
        public Row(GuiButton buttonAIn, GuiButton buttonBIn, GuiTagRowList list) {
            this.buttonA = buttonAIn;
            this.buttonB = buttonBIn;
            this.listOn = list;
        }
        
        public void drawEntry( int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks )
        {
            if ( this.buttonA != null )
            {
                this.buttonA.y = y;
                this.buttonA.drawButton( this.client, mouseX, mouseY, partialTicks );
            }
            
            if ( this.buttonB != null )
            {
                this.buttonB.y = y;
                this.buttonB.drawButton( this.client, mouseX, mouseY, partialTicks );
            }
        }
        
        /**
         * Called when the mouse is clicked within this entry. Returning true means that something within this entry was clicked and the list should not be dragged.
         */
        public boolean mousePressed( int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY )
        {
            if ( this.buttonA.mousePressed( this.client, mouseX, mouseY ) )
            {
                if ( this.buttonA instanceof GuiTagButton )
                {
                    MobTag tag = ((GuiTagButton) buttonA).returnMobTag();
                    MonsterPlacerUtils.setOptionValue( tag, listOn.stack, 1 );
                    buttonA.displayString = MonsterPlacerUtils.getButtonText( tag, listOn.stack );
                    // this.client.gameSettings.setOptionValue(((GuiOptionButton)this.buttonA).returnEnumOptions(),
                    // 1);
                    // this.buttonA.displayString = this.client.gameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(this.buttonA.id));
                }
                
                return true;
            }
            else if ( this.buttonB != null && this.buttonB.mousePressed( this.client, mouseX, mouseY ) )
            {
                if ( this.buttonB instanceof GuiTagButton )
                {
                    MobTag tag = ((GuiTagButton) buttonB).returnMobTag();
                    MonsterPlacerUtils.setOptionValue( tag, listOn.stack, 1 );
                    buttonB.displayString = MonsterPlacerUtils.getButtonText( tag, listOn.stack );
                    // this.client.gameSettings.setOptionValue(((GuiOptionButton)
                    // this.buttonB).returnEnumOptions(), 1);
                    // this.buttonB.displayString =
                    // this.client.gameSettings.getKeyBinding(GameSettings.Options.getEnumOptions(this.buttonB.id));
                }
                
                return true;
            }
            else
            {
                return false;
            }
        }
        
        /**
         * Fired when the mouse button is released. Arguments: index, x, y, mouseEvent, relativeX, relativeY
         */
        public void mouseReleased( int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY )
        {
            if ( releasedAmount > 0 )
            {
                if ( this.buttonA != null )
                {
                    this.buttonA.mouseReleased( x, y );
                }
                
                if ( this.buttonB != null )
                {
                    this.buttonB.mouseReleased( x, y );
                }
            }
            releasedAmount++;
        }
        
        public void updatePosition( int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_ )
        {
        }
    }
}
