package ruukas.infinity.gui.action;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import ruukas.infinity.gui.GuiInfinity.ItemStackHolder;

public abstract class GuiActionButton extends GuiInfinityButton
{
    
    private boolean wasIn = false;
    private ItemStackHolder stackHolder = new ItemStackHolder();
    
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
    
    public boolean addOnCondition( int id, List<GuiButton> buttonList, ItemStackHolder stackHolder, int x, int y, int width, int heigth )
    {
        this.stackHolder = stackHolder;
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
    
    public void add( int id, List<GuiButton> buttonList, ItemStackHolder stackHolder, int x, int y, int width, int heigth )
    {
        this.stackHolder = stackHolder;
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = heigth;
        this.displayString = getText();
        buttonList.add( this );
    }
    
    public ItemStack getItemStack()
    {
        return getItemStackHolder().getStack();
    }
    
    public ItemStackHolder getItemStackHolder()
    {
        return this.stackHolder;
    }
}
