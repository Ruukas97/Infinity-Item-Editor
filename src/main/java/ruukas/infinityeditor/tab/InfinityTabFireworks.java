package ruukas.infinityeditor.tab;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.qualityorder.util.nbt.itemstack.ItemStackTag.DisplayTag;

public class InfinityTabFireworks extends InfinityTab
{
    public InfinityTabFireworks(int i) {
        super( i, "fireworks" );
    }
    
    // TODO Add ability to combine colors
    @Override
    public void displayAllRelevantItems( NonNullList<ItemStack> stackList )
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        
        int i = 0;
        ItemStack currentStar = null;
        ItemStack currentRocket = null;
        
        for ( ItemStack stack : player.inventory.mainInventory )
        {
            if ( stack != null && stack != ItemStack.EMPTY )
            {
                if ( stack.getItem() == Items.FIREWORK_CHARGE )
                {
                    currentStar = stack;
                }
                else if ( stack.getItem() == Items.FIREWORKS )
                {
                    currentRocket = stack;
                }
            }
            i++;
            if ( !InventoryPlayer.isHotbar( i ) )
                break;
        }
        
        if ( currentRocket != null && currentRocket != ItemStack.EMPTY )
        {
            if ( currentRocket.getTagCompound().hasKey( "Fireworks" ) )
            {
                for ( i = 1 ; i < 5 ; i++ )
                {
                    stackList.add( new ItemStack( Items.GUNPOWDER ) );
                    ItemStack newRocket = new ItemStack( Items.FIREWORKS );
                    newRocket.setTagCompound( currentRocket.getTagCompound().copy() );
                    newRocket.setCount( 1 );
                    // newRocket.setTagCompound(QualityNBTHelper.setLore(newRocket.getTagCompound().copy(), ""));
                    newRocket.getTagCompound().getCompoundTag( "Fireworks" ).setTag( "Flight", new NBTTagByte( (byte) i ) );
                    stackList.add( newRocket );
                }
                stackList.add( new ItemStack( Items.GUNPOWDER ) );
            }
        }
        
        if ( currentStar == null || currentStar == ItemStack.EMPTY )
        {
            currentStar = new ItemStack( Items.FIREWORK_CHARGE );
            
            if ( !currentStar.hasTagCompound() )
                currentStar.setTagCompound( new NBTTagCompound() );
            
            NonNullList<ItemStack> dyes = NonNullList.<ItemStack>create();
            Items.DYE.getSubItems( Items.DYE.getCreativeTab(), dyes );
            
            for ( i = 0 ; i < 5 ; i++ )
            {
                ItemStack effect = null;
                switch ( i )
                {
                    case 0:
                        effect = new ItemStack( Items.SLIME_BALL );
                        effect.setStackDisplayName( "Small Ball" );
                        break;
                    case 1:
                        effect = new ItemStack( Items.FIRE_CHARGE );
                        effect.setStackDisplayName( "Large Ball" );
                        break;
                    case 2:
                        effect = new ItemStack( Items.GOLD_NUGGET );
                        effect.setStackDisplayName( "Star-Shaped" );
                        break;
                    case 3:
                        effect = new ItemStack( Items.SKULL );
                        effect.setItemDamage( 4 );
                        effect.setStackDisplayName( "Creeper-Shaped" );
                        break;
                    case 4:
                        effect = new ItemStack( Items.FEATHER );
                        effect.setStackDisplayName( "Burst" );
                        break;
                    default:
                        break;
                }
                
                if ( effect != null && effect != ItemStack.EMPTY )
                {
                    stackList.add( effect );
                    if ( i == 0 )
                    {
                        effect = new ItemStack( Items.SLIME_BALL );
                        effect.setStackDisplayName( "Small Ball" );
                    }
                }
                
                for ( ItemStack dye : dyes )
                {
                    ItemStack newStar = currentStar.copy();
                    
                    NBTTagCompound nbtTag = new NBTTagCompound();
                    
                    nbtTag.setByte( "Type", (byte) i );
                    
                    List<Integer> list = Lists.<Integer>newArrayList();
                    list.add( Integer.valueOf( ItemDye.DYE_COLORS[dye.getMetadata() & 15] ) );
                    
                    int[] colorTag = new int[ list.size() ];
                    
                    for ( int l2 = 0 ; l2 < colorTag.length ; ++l2 )
                    {
                        colorTag[l2] = ((Integer) list.get( l2 )).intValue();
                    }
                    
                    nbtTag.setIntArray( "Colors", colorTag );
                    
                    newStar.getTagCompound().setTag( "Explosion", nbtTag );
                    stackList.add( newStar );
                }
                
                if ( effect != null && effect != ItemStack.EMPTY )
                {
                    stackList.add( effect );
                }
            }
        }
        else
        {
            
            if ( !currentStar.hasTagCompound() )
                currentStar.setTagCompound( new NBTTagCompound() );
            
            ItemStack twinkleEffect = new ItemStack( Items.GLOWSTONE_DUST );
            if ( !twinkleEffect.hasTagCompound() )
            {
                twinkleEffect.setTagCompound( new NBTTagCompound() );
            }
            DisplayTag twinkleDisplayTag = new DisplayTag();
            twinkleDisplayTag.addLore( "Enable or disable the flicker effect!" );
            twinkleDisplayTag.setName( "Twinkle" );
            twinkleEffect.getTagCompound().setTag( DisplayTag.getKeyName(), twinkleDisplayTag );
            
            ItemStack trailEffect = new ItemStack( Items.DIAMOND );
            if ( !trailEffect.hasTagCompound() )
            {
                trailEffect.setTagCompound( new NBTTagCompound() );
            }
            DisplayTag trailDisplayTag = new DisplayTag();
            trailDisplayTag.addLore( "Enable or disable the trail effect!" );
            trailDisplayTag.setName( "Trail" );
            trailEffect.getTagCompound().setTag( DisplayTag.getKeyName(), trailDisplayTag );
            
            ItemStack newRocket = new ItemStack( Items.FIREWORKS );
            newRocket.setStackDisplayName( "Infinity Rocket" );
            newRocket.setCount( 1 );
            
            if ( currentRocket != null && currentRocket != ItemStack.EMPTY )
            {
                if ( currentRocket.hasTagCompound() && currentStar.hasTagCompound() )
                {
                    newRocket.setTagCompound( currentRocket.getTagCompound().copy() );
                    NBTTagCompound rocketCompound = newRocket.getTagCompound();
                    NBTTagCompound starCompound = currentStar.getTagCompound();
                    if ( starCompound.hasKey( "Explosion" ) )
                    {
                        NBTTagCompound starExplosionCompound = starCompound.getCompoundTag( "Explosion" );
                        if ( starExplosionCompound.hasKey( "Colors" ) )
                        {
                            if ( currentRocket.getTagCompound().hasKey( "Fireworks" ) )
                            {
                                NBTTagCompound rocketFireworksCompound = rocketCompound.getCompoundTag( "Fireworks" );
                                NBTTagList rocketExplosionsTagList = rocketFireworksCompound.getTagList( "Explosions", Constants.NBT.TAG_COMPOUND );
                                for ( int j = 0 ; j < rocketExplosionsTagList.tagCount() ; ++j )
                                {
                                    rocketExplosionsTagList.getCompoundTagAt( j ).setIntArray( "FadeColors", starExplosionCompound.getIntArray( "Colors" ) );
                                    ;
                                }
                            }
                        }
                    }
                }
            }
            else
            {
                // newRocket.setTagCompound(
                // QualityNBTHelper.setLore(new NBTTagCompound(), "Take this, when you are ready to continue!"));
                newRocket.getTagCompound().setTag( "Fireworks", new NBTTagCompound() );
                NBTTagList explosionTaglist = new NBTTagList();
                explosionTaglist.appendTag( currentStar.getOrCreateSubCompound( "Explosion" ).copy() );
                newRocket.getTagCompound().getCompoundTag( "Fireworks" ).setTag( "Explosions", explosionTaglist );
            }
            
            for ( i = 0 ; i < 4 ; i++ )
            {
                if ( i == 0 )
                {
                    stackList.add( twinkleEffect );
                }
                else if ( i == 2 )
                {
                    stackList.add( twinkleEffect );
                    stackList.add( newRocket );
                    stackList.add( trailEffect );
                }
                
                ItemStack newStar = currentStar.copy();
                NBTTagCompound nbtTag = newStar.getOrCreateSubCompound( "Explosion" );
                
                nbtTag.setByte( i > 2 ? "Trail" : "Flicker", (byte) (i % 2) );
                
                newStar.getTagCompound().setTag( "Explosion", nbtTag );
                stackList.add( newStar );
                
                if ( i == 3 )
                {
                    stackList.add( trailEffect );
                }
            }
        }
    }
    
    @SideOnly( Side.CLIENT )
    @Override
    public ItemStack getTabIconItem()
    {
        return new ItemStack( Items.FIREWORKS );
    }
}