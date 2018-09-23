package ruukas.infinity.render;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly( Side.CLIENT )
public class TileEntityHeadRenderer extends TileEntitySpecialRenderer<TileEntitySkull>
{
    /*
     * private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation( "textures/entity/skeleton/skeleton.png" ); private static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation( "textures/entity/skeleton/wither_skeleton.png" ); private static final ResourceLocation ZOMBIE_TEXTURES = new ResourceLocation( "textures/entity/zombie/zombie.png" ); private static final ResourceLocation CREEPER_TEXTURES = new ResourceLocation( "textures/entity/creeper/creeper.png" ); private static final ResourceLocation DRAGON_TEXTURES = new ResourceLocation( "textures/entity/enderdragon/dragon.png" ); private final ModelDragonHead dragonHead = new ModelDragonHead( 0.0F );
     */
    public static TileEntityHeadRenderer instance;
    private final ModelSkeletonHead skeletonHead = new ModelSkeletonHead( 0, 0, 64, 32 );
    private final ModelSkeletonHead humanoidHead = new ModelHumanoidHead();
    private final Map<GameProfile, ResourceLocation> rlCache = new ConcurrentHashMap<>();
    private final AtomicInteger atoi = new AtomicInteger( 0 );
    
    public void render( TileEntitySkull te, double x, double y, double z, float partialTicks, int destroyStage, float alpha )
    {
        EnumFacing enumfacing = EnumFacing.getFront( te.getBlockMetadata() & 7 );
        float f = te.getAnimationProgress( partialTicks );
        this.renderSkull( (float) x, (float) y, (float) z, enumfacing, (float) (te.getSkullRotation() * 360) / 16.0F, te.getSkullType(), te.getPlayerProfile(), destroyStage, f );
    }
    
    public void setRendererDispatcher( TileEntityRendererDispatcher rendererDispatcherIn )
    {
        super.setRendererDispatcher( rendererDispatcherIn );
        instance = this;
    }
    
    public void renderSkull( float x, float y, float z, EnumFacing facing, float rotationIn, int skullType, @Nullable GameProfile profile, int destroyStage, float animateTicks )
    {
        ModelBase modelbase = this.skeletonHead;
        
        if ( destroyStage >= 0 )
        {
            this.bindTexture( DESTROY_STAGES[destroyStage] );
            GlStateManager.matrixMode( 5890 );
            GlStateManager.pushMatrix();
            GlStateManager.scale( 4.0F, 2.0F, 1.0F );
            GlStateManager.translate( 0.0625F, 0.0625F, 0.0625F );
            GlStateManager.matrixMode( 5888 );
        }
        else
        {
            modelbase = this.humanoidHead;
            ResourceLocation resourcelocation = DefaultPlayerSkin.getDefaultSkinLegacy();
            
            if ( profile != null )
            {
                Minecraft mc = Minecraft.getMinecraft();
                if(atoi.get() <= 3) {
                    if (rlCache.containsKey( profile ) )
                    {
                        resourcelocation = rlCache.get( profile );
                    }
                    else
                    {
                        rlCache.put( profile, resourcelocation );
                        
                        atoi.incrementAndGet();
                        
                        mc.getSkinManager().loadProfileTextures( profile, new SkinAvailableCallback() {
                            @Override
                            public void skinAvailable( Type typeIn, ResourceLocation location, MinecraftProfileTexture profileTexture )
                            {
                                if ( typeIn == Type.SKIN )
                                {
                                    rlCache.put( profile, location );
                                    atoi.decrementAndGet();
                                }
                            }
                        }, false );
                    }
                }
            }
            
            this.bindTexture( resourcelocation );
            
        }
        
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        
        if ( facing == EnumFacing.UP )
        {
            GlStateManager.translate( x + 0.5F, y, z + 0.5F );
        }
        else
        {
            switch ( facing )
            {
                case NORTH:
                    GlStateManager.translate( x + 0.5F, y + 0.25F, z + 0.74F );
                    break;
                case SOUTH:
                    GlStateManager.translate( x + 0.5F, y + 0.25F, z + 0.26F );
                    rotationIn = 180.0F;
                    break;
                case WEST:
                    GlStateManager.translate( x + 0.74F, y + 0.25F, z + 0.5F );
                    rotationIn = 270.0F;
                    break;
                case EAST:
                default:
                    GlStateManager.translate( x + 0.26F, y + 0.25F, z + 0.5F );
                    rotationIn = 90.0F;
            }
        }
        
        // float f = 0.0625F;
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale( -1.0F, -1.0F, 1.0F );
        GlStateManager.enableAlpha();
        
        if ( skullType == 3 )
        {
            GlStateManager.enableBlendProfile( GlStateManager.Profile.PLAYER_SKIN );
        }
        
        modelbase.render( (Entity) null, animateTicks, 0.0F, 0.0F, rotationIn, 0.0F, 0.0625F );
        GlStateManager.popMatrix();
        
        if ( destroyStage >= 0 )
        {
            GlStateManager.matrixMode( 5890 );
            GlStateManager.popMatrix();
            GlStateManager.matrixMode( 5888 );
        }
    }
}