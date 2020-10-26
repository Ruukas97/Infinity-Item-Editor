package ruukas.infinityeditor.tab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InfinityTabSkulls extends InfinityTab
{
    
    private static final String[] mhfSkulls = { "MHF_Alex", "MHF_Blaze", "MHF_CaveSpider", "MHF_Chicken", "MHF_Cow", "MHF_Creeper", "MHF_Enderman", "MHF_Ghast", "MHF_Golem", "MHF_Herobrine", "MHF_LavaSlime", "MHF_MushroomCow", "MHF_Ocelot", "MHF_Pig", "MHF_PigZombie", "MHF_Sheep", "MHF_Skeleton", "MHF_Slime", "MHF_Spider", "MHF_Squid", "MHF_Steve", "MHF_Villager", "MHF_Wolf", "MHF_WSkeleton", "MHF_Zombie", "MHF_Cactus", "MHF_Cake", "MHF_Chest", "MHF_CoconutB", "MHF_CoconutG", "MHF_Melon", "MHF_OakLog", "MHF_Present1", "MHF_Present2", "MHF_Pumpkin", "MHF_TNT", "MHF_TNT2", "MHF_ArrowUp", "MHF_ArrowDown", "MHF_ArrowLeft", "MHF_ArrowRight", "MHF_Exclamation", "MHF_Question" };
    
    public InfinityTabSkulls(int i) {
        super( i, "skulls" );
    }
    
    @Override
    public void displayAllRelevantItems( NonNullList<ItemStack> stackList )
    {
        stackList.add( clientSkull() );
        
        for ( int i = 0 ; i < Minecraft.getMinecraft().world.playerEntities.size() ; i++ )
        {
            if ( Minecraft.getMinecraft().world.playerEntities.get( i ) instanceof EntityOtherPlayerMP )
            {
                String owner = ((EntityOtherPlayerMP) Minecraft.getMinecraft().world.playerEntities.get( i )).getDisplayNameString();
                ItemStack nearbySkull = new ItemStack( Items.SKULL, 1, 3 );
                nearbySkull.setTagCompound( new NBTTagCompound() );
                nearbySkull.getTagCompound().setTag( "SkullOwner", new NBTTagString( owner ) );
                stackList.add( nearbySkull );
            }
        }
        
        for ( String owner : mhfSkulls )
        {
            ItemStack hardCodedSkull = new ItemStack( Items.SKULL, 1, 3 );
            hardCodedSkull.setTagCompound( new NBTTagCompound() );
            hardCodedSkull.getTagCompound().setString( "SkullOwner", owner );
            if ( owner.contains( "MHF_" ) )
            {
                hardCodedSkull.getTagCompound().setTag( "display", new NBTTagCompound() );
                NBTTagList loreTagList = new NBTTagList();
                loreTagList.appendTag( new NBTTagString( "\"Marc's Head Format\"" ) );
                loreTagList.appendTag( new NBTTagString( "This will never change," ) );
                loreTagList.appendTag( new NBTTagString( "as it was made by Mojang!" ) );
                hardCodedSkull.getTagCompound().getCompoundTag( "display" ).setTag( "Lore", loreTagList );
            }
            stackList.add( hardCodedSkull );
        }
        
        /*
         * for ( String[] skullStringArray : AlphabetSkulls.skulls ) { if ( skullStringArray.length == 3 ) { ItemStackTagSkull skullTag = new ItemStackTagSkull();
         * 
         * skullTag.setOwner( new Owner( skullStringArray[1], skullStringArray[2] ) );
         * 
         * ItemStack skull = new ItemStack( Items.SKULL, 1, 3 );
         * 
         * skull.setTagCompound( skullTag ); skull.setStackDisplayName( skullStringArray[0] );
         * 
         * stackList.add( skull ); } }
         */
    }
    
    private static final ItemStack clientSkull()
    {
        ItemStack skull = new ItemStack( Items.SKULL, 1, 3 );
        skull.setTagCompound( new NBTTagCompound() );
        skull.getTagCompound().setTag( "SkullOwner", new NBTTagString( Minecraft.getMinecraft().player.getDisplayNameString() ) );
        return skull;
    }
    
    @SideOnly( Side.CLIENT )
    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack( Items.SKULL, 1, 3 );
    };
}