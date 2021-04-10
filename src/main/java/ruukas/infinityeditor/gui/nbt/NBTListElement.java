package ruukas.infinityeditor.gui.nbt;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import ruukas.infinityeditor.gui.HelperGui;

public class NBTListElement extends Gui
{
    protected String key;
    protected NBTBase tag;
    protected ItemStack icon;
    protected NBTListCompound parent = null;
    
    private int x, y;
    
    public NBTListElement(String key, NBTBase tag, ItemStack iconStack, int x, int y) {
        this.key = key;
        this.tag = tag;
        this.icon = iconStack;
        this.x = x;
        this.y = y;
    }
    
    public String getKey()
    {
        return key;
    }
    
    public String getText()
    {
        return getKey() + " : " + tag.toString();
    }
    
    public String getTypeName()
    {
        return "Base";
    }
    
    public String getValue()
    {
        return getTag() != null ? getTag().toString() : null;
    }
    
    public NBTBase getTag()
    {
        return tag;
    }
    
    public ItemStack getIconStack()
    {
        return icon;
    }
    
    public void drawIcon( RenderItem itemRender )
    {
        itemRender.renderItemAndEffectIntoGUI( getIconStack(), x - 8, y - 9 );
    }
    
    public void draw( Minecraft mc, int mouseX, int mouseY )
    {
        boolean over = isMouseOver( mouseX, mouseY );
        drawString( mc.fontRenderer, mc.fontRenderer.trimStringToWidth( getText(), 300 ), x + 15, y - 5, over ? HelperGui.getColorFromRGB( 255, 230, 115, 30 ) : 0xffffff );
    }
    
    protected static void drawVerticalStructureLine( int x, int y, int length )
    {
        drawRect( x - 1, y - 1, x + 1, y + length + 1, HelperGui.getColorFromRGB( 255, 30, 200, 255 ) );
    }
    
    protected static void drawHorizontalStructureLine( int x, int y, int length )
    {
        drawRect( x - 1, y - 1, x + length + 1, y + 1, HelperGui.getColorFromRGB( 255, 160, 0, 255 ) );// Orange : GuiHelper.getColorFromRGB(255, 230, 115, 30)
    }
    
    public boolean isMouseOver( int mouseX, int mouseY )
    {
        if ( getRootAsRoot().getSelected() != null )
            return false;
        
        int left = x - 9;
        int textWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth( getText() );
        int right = left + 25 + textWidth;
        
        int top = y - 8;
        int bottom = y + 7;
        
        return mouseX > left && mouseX < right && mouseY > top && mouseY < bottom;
    }
    
    public void mouseClicked( int x, int y, int mouseButton )
    {
        if ( isMouseOver( x, y ) )
            if ( mouseButton == 1 )
            {
                NBTListRoot root = (NBTListRoot) getRoot();
                root.setSelected( this, x, y );
            }
            else
                ((NBTListRoot) getRoot()).setFocus( this );
    }
    
    public int getX()
    {
        return x;
    }
    
    public void setX( int x )
    {
        this.x = x;
    }
    
    public int getY()
    {
        return y;
    }
    
    public void setY( int y )
    {
        this.y = y;
    }
    
    public void setPos( int x, int y )
    {
        this.x = x;
        this.y = y;
    }
    
    public NBTListElement getRoot()
    {
        return parent != null ? parent.getRoot() : this;
    }
    
    public NBTListRoot getRootAsRoot()
    {
        NBTListElement root = getRoot();
        return root instanceof NBTListRoot ? (NBTListRoot) root : null;
    }
    
    public NBTOption[] getOptions()
    {
        List<NBTOption> options = new ArrayList<>();
        options.add( new NBTOption() {
            @Override
            public String getText()
            {
                return "Change value";
            }
            
            @Override
            public void action()
            {
                // System.out.println("Hello");
            }
        } );
        
        options.add( new NBTOption() {
            @Override
            public String getText()
            {
                return "Remove";
            }
            
            @Override
            public void action()
            {
                if ( parent != null && parent.children != null )
                {
                    for ( NBTListElement e : parent.children )
                    {
                        if ( e.equals( NBTListElement.this ) )
                        {
                            parent.children.remove( e );
                            break;
                        }
                    }
                    
                    parent.getTagCompound().removeTag( getKey() );
                    
                }
                NBTListRoot root = getRootAsRoot();
                root.clearSelected();
                root.redoPositions();
            }
        } );
        
        NBTOption[] optionsArray = new NBTOption[ options.size() ];
        options.toArray( optionsArray );
        
        return optionsArray;
        
    }
    
    @Override
    public boolean equals( Object obj )
    {
        if ( obj == this )
            return true;
        
        if ( !(obj instanceof NBTListElement) )
        {
            return false;
        }
        
        NBTListElement other = (NBTListElement) obj;

        return icon.isItemEqual(other.icon) && getKey().equals(other.getKey()) && getTag().equals(other.getTag()) && parent.equals(other.parent);
    }
}