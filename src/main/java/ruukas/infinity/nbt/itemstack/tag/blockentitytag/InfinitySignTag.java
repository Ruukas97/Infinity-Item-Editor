package ruukas.infinity.nbt.itemstack.tag.blockentitytag;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraftforge.common.util.Constants.NBT;
import ruukas.infinity.nbt.itemstack.tag.InfinityBlockEntityTag;

public class InfinitySignTag
{
    private final InfinityBlockEntityTag tileTag;
    
    public InfinitySignTag(InfinityBlockEntityTag tileTag) {
        this.tileTag = tileTag;
    }
    
    public InfinitySignTag(ItemStack stack) {
        this( new InfinityBlockEntityTag( stack ) );
    }
    
    public InfinitySignTag setLine( int line, String text )
    {
        if ( text == null || text.length() < 1 )
        {
            tileTag.getTag().removeTag( "Text" + (line + 1) );
            tileTag.checkEmpty();
        }
        else
        {
            tileTag.getTag().setString( "Text" + (line + 1), text );
        }
        return this;
    }
    
    public InfinitySignTag setLine( int line, ITextComponent text )
    {
        return setLine( line, ITextComponent.Serializer.componentToJson( text ) );
    }
    
    public InfinitySignTag setLineUnformatted( int line, String text )
    {
        ITextComponent comp = new TextComponentString( text );;
        Style style = null;
        if ( hasLine( line ) )
        {
            style = getLineComponent( line ).getStyle();
        }

        return setLine( line, comp.setStyle( style ) );
    }
    
    public String getLine( int line )
    {
        return tileTag.getTag().getString( "Text" + (line + 1) );
    }
    
    public String getLineFormatted( int line )
    {
        return getLineComponent( line ).getFormattedText();
    }
    
    public ITextComponent getLineComponent( int line )
    {
        return ITextComponent.Serializer.fromJsonLenient( getLine( line ) );
    }
    
    public boolean hasLine( int line )
    {
        return tileTag.exists() && tileTag.getTag().hasKey( "Text" + (line + 1), NBT.TAG_STRING );
    }
    
    public boolean hasCommand()
    {
        if ( !hasLine( 0 ) )
        {
            return false;
        }
        
        Style style = getLineComponent( 0 ).getStyle();
        
        if ( style != null && style.getClickEvent() != null )
        {
            ClickEvent clickevent = style.getClickEvent();
            
            if ( clickevent.getAction() == ClickEvent.Action.RUN_COMMAND )
            {
                return true;
            }
        }
        
        return false;
    }
    
    public InfinitySignTag setCommand( String command )
    {
        boolean remove = command == null || command.length() < 1;
        
        if ( !hasLine( 0 ) )
        {
            if(remove){
                return this;
            }
            else setLine( 0, "" );
        }
        
        ITextComponent comp = getLineComponent( 0 );
        setLine( 0, comp.setStyle( remove ? null : comp.getStyle().setClickEvent( new ClickEvent( Action.RUN_COMMAND, command ) ) ) );
        
        return this;
    }
    
    public String getCommand()
    {
        if ( !hasLine( 0 ) )
        {
            return null;
        }
        
        Style style = getLineComponent( 0 ).getStyle();
        
        if ( style != null && style.getClickEvent() != null )
        {
            ClickEvent clickevent = style.getClickEvent();
            
            if ( clickevent.getAction() == ClickEvent.Action.RUN_COMMAND )
            {
                return clickevent.getValue();
            }
        }
        
        return null;
    }
}
