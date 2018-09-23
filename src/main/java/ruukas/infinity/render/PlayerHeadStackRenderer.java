package ruukas.infinity.render;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;

public class PlayerHeadStackRenderer extends TileEntityItemStackRenderer
{
    public void renderByItem( ItemStack stack, float partialTicks )
    {
        
        if ( stack.getItem() == Items.SKULL )
        {
            GameProfile gameprofile = null;
            
            if ( stack.hasTagCompound() )
            {
                NBTTagCompound nbttagcompound = stack.getTagCompound();
                
                if ( nbttagcompound.hasKey( "SkullOwner", 10 ) )
                {
                    gameprofile = NBTUtil.readGameProfileFromNBT( nbttagcompound.getCompoundTag( "SkullOwner" ) );
                }
                else if ( nbttagcompound.hasKey( "SkullOwner", 8 ) && !StringUtils.isBlank( nbttagcompound.getString( "SkullOwner" ) ) )
                {
                    GameProfile gameprofile1 = new GameProfile( (UUID) null, nbttagcompound.getString( "SkullOwner" ) );
                    gameprofile = TileEntitySkull.updateGameprofile( gameprofile1 );
                    nbttagcompound.removeTag( "SkullOwner" );
                    nbttagcompound.setTag( "SkullOwner", NBTUtil.writeGameProfile( new NBTTagCompound(), gameprofile ) );
                }
            }
            
            if ( TileEntitySkullRenderer.instance != null )
            {
                GlStateManager.pushMatrix();
                GlStateManager.disableCull();
                if ( stack.getItemDamage() != 3 )
                {
                    TileEntitySkullRenderer.instance.renderSkull( 0.0F, 0.0F, 0.0F, EnumFacing.UP, 180.0F, stack.getMetadata(), gameprofile, -1, 0.0F );
                }
                else
                {
                    TileEntityHeadRenderer.instance.renderSkull( 0.0F, 0.0F, 0.0F, EnumFacing.UP, 180.0F, stack.getMetadata(), gameprofile, -1, 0.0F );
                }
                GlStateManager.enableCull();
                GlStateManager.popMatrix();
            }
        }
    }
}
