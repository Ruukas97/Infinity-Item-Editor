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
        if ( text.getFormattedText().length() < 1 && text.getStyle().getClickEvent() == null )
        {
            return setLine( line, (String) null );
        }
        
        return setLine( line, ITextComponent.Serializer.componentToJson( text ) );
    }
    
    public InfinitySignTag setLineUnformatted( int line, String text )
    {
        if ( text.length() < 1 && !hasCommand() )
        {
            return setLine( line, (String) null );
        }
        
        ITextComponent comp = new TextComponentString( text );
        
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
        return hasLine( 0 ) && getLineComponent( 0 ) != null && getLineComponent( 0 ).getStyle().getClickEvent() != null && getLineComponent( 0 ).getStyle().getClickEvent().getAction() == ClickEvent.Action.RUN_COMMAND;
    }
    
    public InfinitySignTag setCommand( String command )
    {
        boolean remove = command == null || command.length() < 1;
        boolean noLine = !hasLine( 0 );
        
        if ( noLine && remove )
        {
            return this;
        }
        
        ITextComponent comp = noLine ? new TextComponentString( "" ) : getLineComponent( 0 );
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
