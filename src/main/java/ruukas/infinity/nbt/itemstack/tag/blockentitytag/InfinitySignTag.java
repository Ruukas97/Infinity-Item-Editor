package ruukas.infinity.nbt.itemstack.tag.blockentitytag;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
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
        return setLine( line, new TextComponentString( text ) );
    }
    
    public String getLine( int line )
    {
        return tileTag.getTag().getString( "Text" + (line + 1) );
    }
    
    public String getLineFormatted(int line){
        return getLineComponent( line ).getFormattedText();
    }
    
    public ITextComponent getLineComponent( int line )
    {
        return ITextComponent.Serializer.fromJsonLenient( getLine( line ) );
    }
    
    
    public boolean hasLine( int line){
        return tileTag.exists() && tileTag.getTag().hasKey( "Text" + ( line + 1), NBT.TAG_STRING );
    }
}
