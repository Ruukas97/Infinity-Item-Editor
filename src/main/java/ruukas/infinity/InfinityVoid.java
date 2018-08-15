package ruukas.infinity;

import java.io.File;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly( Side.CLIENT )
public class InfinityVoid
{
    private static final Logger LOGGER = Infinity.logger;
    private final File dataFile;
    private final NonNullList<ItemStack> stackList = NonNullList.create();
    
    public InfinityVoid(ItemStack stack) {
        this.dataFile = new File( Infinity.dataDir.getAbsolutePath() + File.separatorChar + "void", stack.getItem().getRegistryName().toString().replace( ':', '.' ) + ".nbt" );
        
        this.read();
    }
    
    public void read()
    {
        if ( !dataFile.exists() )
        {
            return;
        }
        
        try
        {
            NBTTagCompound root = CompressedStreamTools.read( this.dataFile );
            
            if ( root == null || root.getTagList( "stacks", NBT.TAG_COMPOUND ) == null )
            {
                return;
            }
            
            for ( NBTBase tag : root.getTagList( "stacks", NBT.TAG_COMPOUND ) )
            {
                stackList.add( new ItemStack( (NBTTagCompound) tag ) );
            }
            
        }
        catch ( Exception exception )
        {
            LOGGER.error( "Failed to load creative mode options", (Throwable) exception );
        }
    }
    
    public void write()
    {
        try
        {
            NBTTagCompound root = new NBTTagCompound();
            root.setTag( "stacks", new NBTTagList() );
            NBTTagList stacks = root.getTagList( "stacks", NBT.TAG_COMPOUND );
            
            for ( int i = 0 ; i < stackList.size() ; ++i )
            {
                stacks.appendTag( stackList.get( i ).writeToNBT( new NBTTagCompound() ) );
            }
            
            CompressedStreamTools.write( root, this.dataFile );
        }
        catch ( Exception exception )
        {
            LOGGER.error( "Failed to save creative mode options", (Throwable) exception );
        }
    }
    
    public void addItemStack( EntityPlayerSP player, ItemStack stack )
    {
        synchronized ( Infinity.dataDir )
        {
            if ( stack == null || stack.isEmpty() || !stack.hasTagCompound() )
            {
                return;
            }
            
            stack.setCount( 1 );
            if ( stack.getItem().isDamageable() )
            {
                stack.setItemDamage( 0 );
            }
            
            for ( ItemStack s : stackList )
            {
                if ( ItemStack.areItemStacksEqual( s, stack ) )
                {
                    // player.sendMessage( new TextComponentString( "Didn't add " ).appendSibling( stack.getTextComponent() ).appendText( " to Infinity Void, as it was already found." ) );
                    return;
                }
            }
            
            player.sendMessage( new TextComponentString( "Added " ).appendSibling( stack.getTextComponent() ).appendText( " to Infinity Void." ) );
            stackList.add( stack );
            write();
        }
    }
    
    public NonNullList<ItemStack> getStackList()
    {
        return stackList;
    }
    
    public static synchronized void loadVoid( NonNullList<ItemStack> list )
    {
        File dataFile = new File( Infinity.dataDir.getAbsolutePath() + File.separatorChar + "void" );
        
        for ( File files : dataFile.listFiles() )
        {
            Item item = Item.getByNameOrId( files.getName().substring( 0, files.getName().length() - 4 ).replace( '.', ':' ) );
            ItemStack stack = new ItemStack( item );
            
            synchronized ( Infinity.dataDir )
            {
                list.addAll( new InfinityVoid( stack ).getStackList() );
            }
        }
    }
}