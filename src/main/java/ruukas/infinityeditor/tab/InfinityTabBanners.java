package ruukas.infinityeditor.tab;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InfinityTabBanners extends InfinityTab
{
    public static ItemStack iconBanner = generateIconBanner();
    
    public InfinityTabBanners(int index) {
        super( index, "banners" );
    }
    
    @Override
    public void displayAllRelevantItems( NonNullList<ItemStack> stackList )
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        
        int i = 0;
        NonNullList<ItemStack> banners = NonNullList.create();
        Items.BANNER.getSubItems( CreativeTabs.DECORATIONS, banners );
        ItemStack currentBanner = null;
        
        for ( ItemStack stack : player.inventory.mainInventory )
        {
            if ( stack != null && (stack.getItem() == Items.SHIELD || stack.getItem() == Items.BANNER) )
            {
                currentBanner = stack;
                iconBanner = currentBanner.copy();
                break;
            }
            i++;
            if ( !InventoryPlayer.isHotbar( i ) )
                break;
        }
        if ( currentBanner == null )
        {
            iconBanner = generateIconBanner();
            Items.BANNER.getSubItems( CreativeTabs.DECORATIONS, stackList );
            ItemStack baseShield = new ItemStack( Items.SHIELD );
            
            for ( ItemStack banner : banners )
            {
                ItemStack newShield = baseShield.copy();
                
                NBTTagCompound nbttagcompound = banner.getSubCompound( "BlockEntityTag" );
                NBTTagCompound nbttagcompound1 = nbttagcompound == null ? new NBTTagCompound() : nbttagcompound.copy();
                nbttagcompound1.setInteger( "Base", banner.getMetadata() & 15 );
                newShield.setTagInfo( "BlockEntityTag", nbttagcompound1 );
                
                stackList.add( newShield );
            }
            
            for ( TileEntity tilee : player.world.loadedTileEntityList )
            {
                if ( tilee instanceof TileEntityBanner )
                {
                    stackList.add( ((TileEntityBanner) tilee).getItem() );
                }
            }
            
        }
        else
        {
            if ( !currentBanner.hasTagCompound() )
                currentBanner.setTagCompound( new NBTTagCompound() );
            
            ItemStack otherVariant = null;
            if ( currentBanner.getItem().equals( Items.BANNER ) )
            {
                otherVariant = new ItemStack( Items.SHIELD );
                NBTTagCompound currentTag = currentBanner.getSubCompound( "BlockEntityTag" );
                NBTTagCompound tileTag = currentTag == null ? new NBTTagCompound() : currentTag.copy();
                tileTag.setInteger( "Base", currentBanner.getMetadata() & 15 );
                otherVariant.setTagInfo( "BlockEntityTag", tileTag );
                
            }
            else if ( currentBanner.getItem().equals( Items.SHIELD ) )
            {
                otherVariant = new ItemStack( Items.BANNER );
                NBTTagCompound currentTag = currentBanner.getSubCompound( "BlockEntityTag" );
                NBTTagCompound tileTag = currentTag == null ? new NBTTagCompound() : currentTag.copy();
                otherVariant.setItemDamage( tileTag.getInteger( "Base" ) );
                otherVariant.setTagInfo( "BlockEntityTag", tileTag );
            }
            
            stackList.add( otherVariant );
            
            for ( BannerPattern loopPattern : BannerPattern.values() )
            {
                if ( loopPattern == BannerPattern.BASE )
                {
                    continue;
                }
                for ( EnumDyeColor color : EnumDyeColor.values() )
                {
                    ItemStack newBanner = currentBanner.copy();
                    
                    NBTTagCompound nbtTag = newBanner.getOrCreateSubCompound( "BlockEntityTag" );
                    if ( !nbtTag.hasKey( "Patterns", 9 ) )
                    {
                        nbtTag.setTag( "Patterns", new NBTTagList() );
                    }
                    
                    NBTTagList nbtPatterns = nbtTag.getTagList( "Patterns", 10 );
                    
                    NBTTagCompound nbtPattern = new NBTTagCompound();
                    nbtPattern.setString( "Pattern", loopPattern.getHashname() );
                    nbtPattern.setInteger( "Color", color.getDyeDamage() );
                    
                    nbtPatterns.appendTag( nbtPattern );
                    stackList.add( newBanner );
                }
            }
        }
    }
    
    private static ItemStack generateIconBanner()
    {
        ItemStack icon = new ItemStack( Items.BANNER, 1, 14 );
        
        NBTTagCompound nbtTag = icon.getOrCreateSubCompound( "BlockEntityTag" );
        if ( !nbtTag.hasKey( "Patterns", 9 ) )
        {
            nbtTag.setTag( "Patterns", new NBTTagList() );
        }
        
        NBTTagList nbtPatterns = nbtTag.getTagList( "Patterns", 10 );
        
        NBTTagCompound nbtPattern = new NBTTagCompound();
        nbtPattern.setString( "Pattern", BannerPattern.CROSS.getHashname() );
        nbtPatterns.appendTag( nbtPattern );
        
        NBTTagCompound nbtPattern1 = new NBTTagCompound();
        nbtPattern1.setString( "Pattern", BannerPattern.CURLY_BORDER.getHashname() );
        nbtPatterns.appendTag( nbtPattern1 );
        
        NBTTagCompound nbtPattern2 = new NBTTagCompound();
        nbtPattern2.setString( "Pattern", BannerPattern.STRAIGHT_CROSS.getHashname() );
        nbtPatterns.appendTag( nbtPattern2 );
        
        NBTTagCompound nbtPattern3 = new NBTTagCompound();
        nbtPattern3.setString( "Pattern", BannerPattern.FLOWER.getHashname() );
        nbtPattern3.setInteger( "Color", 14 );
        ;
        nbtPatterns.appendTag( nbtPattern3 );
        
        return icon;
    }
    
    @SideOnly( Side.CLIENT )
    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack( Items.BANNER );
    }
    
    @SideOnly( Side.CLIENT )
    @Override
    public ItemStack getIconItemStack()
    {
        return iconBanner;
    }
}