package ruukas.infinity.nbt.itemstack.tag;

import com.google.gson.stream.MalformedJsonException;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants.NBT;
import ruukas.infinity.nbt.itemstack.InfinityItemTag;

public class InfinityBookTags
{
    private final InfinityItemTag itemTag;
    
    private static final String generationKey = "generation";
    private static final String authorKey = "author";
    private static final String titleKey = "title";
    private static final String pagesKey = "pages";
    private static final String resolvedKey = "resolved";
    
    public static class SignedData
    {
        private final NBTTagList pages;
        private final int generation;
        private final String author;
        private final String title;
        private final boolean resolved;
        
        public SignedData(NBTTagList pages, int generation, String author, String title, boolean resolved) {
            this.pages = pages;
            this.generation = generation;
            this.author = author;
            this.title = title;
            this.resolved = resolved;
        }
        
        protected SignedData(NBTTagCompound itemTag) {
            this.pages = itemTag.getTagList( pagesKey, NBT.TAG_STRING );
            this.generation = itemTag.getInteger( generationKey );
            this.author = itemTag.getString( authorKey );
            this.title = itemTag.getString( titleKey );
            this.resolved = itemTag.getBoolean( resolvedKey );
        }
    }
    
    public InfinityBookTags(InfinityItemTag itemTag) {
        this.itemTag = itemTag;
    }
    
    public InfinityBookTags(ItemStack stack) {
        this( new InfinityItemTag( stack ) );
    }
    
    public int getGeneration()
    {
        return itemTag.getTag().getInteger( generationKey );
    }
    
    public InfinityBookTags addGeneration()
    {
        int gen = (getGeneration() + 1) % 4;
        if ( gen == 0 )
        {
            itemTag.getTag().removeTag( generationKey );
            itemTag.checkEmpty();
        }
        else
        {
            itemTag.getTag().setInteger( generationKey, gen );
        }
        return this;
    }
    
    public String getAuthor()
    {
        return itemTag.getTag().getString( authorKey );
    }
    
    public InfinityBookTags setAuthor( String author )
    {
        itemTag.getTag().setString( authorKey, author );
        return this;
    }
    
    public InfinityBookTags clearAuthor()
    {
        itemTag.getTag().setTag( authorKey, null );
        return this;
    }
    
    public String getTitle()
    {
        return itemTag.getTag().getString( titleKey );
    }
    
    public InfinityBookTags setTitle( String title )
    {
        itemTag.getTag().setString( titleKey, title );
        return this;
    }
    
    public InfinityBookTags clearTitle()
    {
        itemTag.getTag().setTag( titleKey, null );
        return this;
    }
    
    public SignedData unsign()
    {
        SignedData data = new SignedData( itemTag.getTag() );
        
        if ( itemTag.getTag().hasKey( pagesKey, NBT.TAG_LIST ) || !(itemTag.getTag().getTagList( pagesKey, NBT.TAG_STRING ).hasNoTags()) )
        {
            NBTTagList pagesList = itemTag.getTag().getTagList( pagesKey, NBT.TAG_STRING );
            
            for ( int i = 0 ; i < pagesList.tagCount() ; i++ )
            {
                try
                {
                    ITextComponent comp = ITextComponent.Serializer.jsonToComponent( pagesList.getStringTagAt( i ) );
                    pagesList.set( i, new NBTTagString( comp.getUnformattedText() ) );
                }
                catch ( Exception e )
                {
                    // e.printStackTrace();
                }
            }
            
        }
        itemTag.getTag().removeTag( generationKey );
        itemTag.getTag().removeTag( authorKey );
        itemTag.getTag().removeTag( titleKey );
        itemTag.getTag().removeTag( resolvedKey );
        
        itemTag.checkEmpty();
        
        return data;
    }
    
    public InfinityBookTags resign( SignedData data )
    {
        itemTag.getTag().setTag( pagesKey, data.pages );
        itemTag.getTag().setInteger( generationKey, data.generation );
        itemTag.getTag().setString( authorKey, data.author );
        itemTag.getTag().setString( titleKey, data.title );
        itemTag.getTag().setBoolean( resolvedKey, data.resolved );
        
        itemTag.checkEmpty();
        
        return this;
    }
    
    public boolean getResolved()
    {
        return itemTag.getTag().getBoolean( resolvedKey );
    }
    
    public InfinityBookTags setResolved( boolean resolved )
    {
        if ( resolved )
        {
            itemTag.getTag().setBoolean( resolvedKey, resolved );
        }
        else
        {
            itemTag.getTag().removeTag( resolvedKey );
        }
        
        itemTag.checkEmpty();
        
        return this;
    }
    
    public InfinityBookTags toggleResolved()
    {
        return setResolved( !getResolved() );
    }
}
