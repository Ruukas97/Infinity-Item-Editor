package ruukas.infinity.gui.action;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;

public abstract class GuiActionButton extends GuiInfinityButton
{
    
    private boolean wasIn = false;
    private ItemStack stack = ItemStack.EMPTY;
    
    public GuiActionButton() {
        super( 0, 0, 0, "" );
    }
    
    @Override
    public boolean mousePressed( Minecraft mc, int mouseX, int mouseY )
    {
        return wasIn = super.mousePressed( mc, mouseX, mouseY );
    }
    
    @Override
    public void mouseReleased( int mouseX, int mouseY )
    {
        if ( wasIn && isMouseOver() )
        {
            this.action();
        }
    }
    
    public abstract void action();
    
    public abstract boolean condition();
    
    public abstract String getText();
    
    public boolean addOnCondition( int id, List<GuiButton> buttonList, ItemStack stack, int x, int y, int width, int heigth )
    {
        this.stack = stack;
        if ( condition() )
        {
            this.id = id;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = heigth;
            this.displayString = getText();
            buttonList.add( this );
            return true;
        }
        
        return false;
    }
    
    public ItemStack getItemStack()
    {
        return this.stack;
    }
}
