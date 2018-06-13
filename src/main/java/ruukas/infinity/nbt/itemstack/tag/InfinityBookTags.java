package ruukas.infinity.nbt.itemstack.tag;

import net.minecraft.item.ItemStack;
import ruukas.infinity.nbt.itemstack.InfinityItemTag;

public class InfinityBookTags
{
    private final InfinityItemTag itemTag;
    
    private static final String generationKey = "generation";
    private static final String authorKey = "author";
    private static final String titleKey = "title";
    //private static final String pagesKey = "pages";
    
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
        itemTag.getTag().setInteger( generationKey, (getGeneration() + 1) % 4 );
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
    
    public String getTitle()
    {
        return itemTag.getTag().getString( titleKey );
    }
    
    public InfinityBookTags setTitle( String title )
    {
        itemTag.getTag().setString( titleKey, title );
        return this;
    }
    
    public InfinityBookTags unsign()
    {
        itemTag.getTag().removeTag( generationKey );
        itemTag.getTag().removeTag( authorKey );
        itemTag.getTag().removeTag( titleKey );
        return this;
    }
}
