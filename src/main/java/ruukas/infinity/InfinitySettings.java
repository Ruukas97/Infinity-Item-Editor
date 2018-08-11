package ruukas.infinity;

import java.io.File;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.entity.EntityPlayerSP;
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
public class InfinitySettings
{
    private static final Logger LOGGER = Infinity.logger;
    private final File dataFile;
    private final NonNullList<ItemStack> stackList = NonNullList.create();
    
    public InfinitySettings(File dataDir) {
        this.dataFile = new File( dataDir, "infinity.nbt" );
        
        this.read();
    }
    
    public void read()
    {
        try
        {
            NBTTagCompound root = CompressedStreamTools.read( this.dataFile );
            
            if ( root == null || root.getTagList( "realm", NBT.TAG_COMPOUND ) == null )
            {
                return;
            }
            
            for ( NBTBase tag : root.getTagList( "realm", NBT.TAG_COMPOUND ) )
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
            root.setTag( "realm", new NBTTagList() );
            NBTTagList realm = root.getTagList( "realm", NBT.TAG_COMPOUND );
            
            for ( int i = 0 ; i < stackList.size() ; ++i )
            {
                realm.appendTag( stackList.get( i ).writeToNBT( new NBTTagCompound() ) );
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
        if ( stack == null || stack.isEmpty() )
        {
            return;
        }
        
        for ( ItemStack s : stackList )
        {
            if ( ItemStack.areItemStacksEqual( s, stack ) )
            {
                player.sendMessage( new TextComponentString( "Didn't add " ).appendSibling( stack.getTextComponent() ).appendText( ", as it seems to already exist in the Infinity Realm." ) );
                return;
            }
        }
        
        player.sendMessage( new TextComponentString( "Added " ).appendSibling( stack.getTextComponent() ).appendText( " to Infinity Realm." ) );
        stackList.add( stack );
        write();
    }
    
    public void removeItemStack( EntityPlayerSP player, ItemStack stack )
    {
        if ( stack == null || stack.isEmpty() )
        {
            return;
        }
        
        for ( ItemStack s : stackList )
        {
            if ( ItemStack.areItemStacksEqual( s, stack ) )
            {
                player.sendMessage( new TextComponentString( "Banished " ).appendSibling( stack.getTextComponent() ).appendText( " from the Infinity Realm. If this was done by mistake, get it from the Thief tab." ) );
                stackList.remove( s );
                return;
            }
        }
        write();
    }
    
    public NonNullList<ItemStack> getStackList()
    {
        return stackList;
    }
}