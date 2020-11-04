package ruukas.infinityeditor.gui.nbt;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import ruukas.infinityeditor.gui.GuiNBTAdvanced;
import ruukas.infinityeditor.gui.HelperGui;

public class NBTListCompound extends NBTListElement
{
    protected List<NBTListElement> children;
    protected boolean closed;
    
    public static ItemStack openIcon = new ItemStack( Blocks.CHEST );
    public static ItemStack closedIcon = new ItemStack( Blocks.ENDER_CHEST );
    
    public NBTListCompound(String key, NBTTagCompound tag, boolean closed, int x, int y) {
        this( key, tag, closed ? closedIcon : openIcon, x, y );
        this.closed = closed;
    }
    
    public NBTListCompound(String key, NBTTagCompound tag, ItemStack iconStack, int x, int y) {
        super( key, tag, iconStack, x, y );
        this.closed = true;
        
        if ( tag != null )
        {
            children = new ArrayList<>();
            
            int length = 20;
            
            for ( String childKey : tag.getKeySet() )
            {
                NBTBase child = tag.getTag( childKey );
                int lengthToAdd;
                
                if ( child instanceof NBTTagCompound )
                    lengthToAdd = addChild( new NBTListCompound( childKey, (NBTTagCompound) child, closedIcon, x + 15, y + length ) );
                else
                    lengthToAdd = addChild( new NBTListElement( childKey, child, new ItemStack( Items.PAPER ), x + 15, y + length ) );
                
                length += lengthToAdd;
            }
        }
    }
    
    public int addChild( NBTListElement child )
    {
        child.parent = this;
        children.add( child );
        
        return (child instanceof NBTListCompound) ? ((NBTListCompound) child).getLength() + 20 : 20;
    }
    
    public NBTTagCompound getTagCompound()
    {
        return (NBTTagCompound) tag;
    }
    
    @Override
    public String getText()
    {
        return children != null ? getKey() + " (" + children.size() + ")" : getKey();
    }
    
    @Override
    public String getTypeName()
    {
        return "Compound Tag";
    }
    
    @Override
    public void drawIcon( RenderItem itemRender )
    {
        super.drawIcon( itemRender );
        
        if ( closed )
            return;
        
        if ( tag != null && !children.isEmpty() )
            for ( NBTListElement e : children )
            {
                e.drawIcon( itemRender );
            }
    }
    
    @Override
    public void draw( Minecraft mc, int mouseX, int mouseY )
    {
        super.draw( mc, mouseX, mouseY );
        
        if ( !(this instanceof NBTListRoot) && isMouseOver( mouseX, mouseY ) )
            GuiNBTAdvanced.setInfoStatic( I18n.format( closed ? "gui.nbt.comp.open" : "gui.nbt.comp.close" ), HelperGui.getColorFromRGB( 255, 230, 115, 30 ) );
        
        if ( closed )
        {
            return;
        }
        
        if ( tag != null )
            for ( NBTListElement e : children )
            {
                drawHorizontalStructureLine( e.getX() - 13, e.getY(), 11 );
                e.draw( mc, mouseX, mouseY );
            }
        
        int length = getLength();
        
        if ( length > 0 )
            drawVerticalStructureLine( getX(), getY(), getLength() );
    }
    
    public List<NBTListCompound> getCompoundChildren()
    {
        List<NBTListCompound> compounds = new ArrayList<>();
        
        for ( NBTListElement c : children )
            if ( c instanceof NBTListCompound )
                compounds.add( (NBTListCompound) c );
            
        return compounds;
    }
    
    public int getLength()
    {
        if ( !closed && getTagCompound() != null )
        {
            int length = getTagCompound().getSize() * 20;
            
            for ( NBTListCompound c : getCompoundChildren() )
                length += c.getLength();
            
            return length;
        }
        else
            return 0;
    }
    
    @Override
    public void mouseClicked( int mouseX, int mouseY, int mouseButton )
    {
        super.mouseClicked( mouseX, mouseY, mouseButton );
        
        if ( this instanceof NBTListRoot )
            return;
        
        if ( isMouseOver( mouseX, mouseY ) && mouseButton == 0 )
        {
            closed = !closed;
            icon = closed ? closedIcon : openIcon;
            ((NBTListCompound) getRoot()).redoPositions();
        }
        
        else if ( !closed && children != null && !children.isEmpty() )
            for ( NBTListElement e : children )
                e.mouseClicked( mouseX, mouseY, mouseButton );
    }
    
    public void redoPositions()
    {
        int length = 20;
        
        for ( NBTListElement e : children )
        {
            e.setY( this.getY() + length );
            
            length += 20;
            
            if ( e.tag instanceof NBTTagCompound )
            {
                NBTListCompound tag = ((NBTListCompound) e);
                length += tag.getLength();
                tag.redoPositions();
            }
        }
    }
}
