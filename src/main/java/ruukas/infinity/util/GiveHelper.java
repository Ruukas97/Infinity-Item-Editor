package ruukas.infinity.util;

import net.minecraft.command.CommandBase;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import ruukas.infinity.Infinity;

public class GiveHelper
{
    public static ItemStack getItemStackFromString( String command )
    {
        if ( command == null || command.length() < 1 || !command.startsWith( "/give" ) )
        {
            return ItemStack.EMPTY;
        }
        
        //Remove "/give <player>" part
        command = command.substring( command.indexOf( ' ' ) + 1 );
        command = command.substring( command.indexOf( ' ' ) + 1 );
        
        String[] args = command.trim().split( " " );
        
        if ( args.length < 1 )
        {
            return ItemStack.EMPTY;
        }
        
        Item item = Item.getByNameOrId( args[0] );
        
        int count = 1;
        try
        {
            count = args.length >= 2 ? CommandBase.parseInt( args[1] ) : 1;
            System.out.println( "Count: " + count );
        }
        catch ( NumberInvalidException e )
        {
            Infinity.logger.error( "Couldn't parse \"" + args[1] + "\" as number for stack count." );
        }
        
        int meta = 0;
        try
        {
            meta = args.length >= 3 ? CommandBase.parseInt( args[2] ) : 0;
            System.out.println( "Meta: " + meta );
        }
        catch ( NumberInvalidException e )
        {
            Infinity.logger.error( "Couldn't parse \"" + args[2] + "\" as number for meta data." );
        }
        
        NBTTagCompound tag = null;
        String tagString = "";
        if ( args.length >= 4 )
        {
            for ( int i = 3 ; i < args.length ; i++ )
            {
                tagString += " " + args[i];
            }
            try
            {
                tag = JsonToNBT.getTagFromJson( tagString );
            }
            catch ( NBTException e )
            {
                Infinity.logger.error( "Couldn't parse \"" + tagString + "\" as NBT for item." );
            }
        }
        
        ItemStack stack = new ItemStack( item, count, meta );
        stack.setTagCompound( tag );
        
        return stack;
    }
    
    public static String getStringFromItemStack( ItemStack stack )
    {
        String id = stack.getItem().getRegistryName().toString();
        
        String command = "/give @p " + id;
        
        boolean shouldAddTag = stack.hasTagCompound();
        boolean shouldAddMeta = shouldAddTag || stack.getMetadata() != 0;
        boolean shouldAddCount = shouldAddMeta || stack.getCount() != 1;
        
        if ( shouldAddCount )
        {
            command += " " + stack.getCount();
        }
        
        if ( shouldAddMeta )
        {
            command += " " + stack.getMetadata();
        }
        
        if ( shouldAddTag )
        {
            command += " " + stack.getTagCompound().toString();
        }
        
        return command;
    }
}
