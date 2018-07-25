package ruukas.infinity.event;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import ruukas.infinity.Infinity;
import ruukas.infinity.gui.GuiItem;
import ruukas.infinity.gui.HelperGui;

@Mod.EventBusSubscriber( modid = Infinity.MODID )
public class InfinityEventHandler
{
    
    @SubscribeEvent( )
    public static void onKeyPress( KeyInputEvent event )
    {
        if ( Infinity.keybind.isPressed() && Minecraft.getMinecraft().world != null )
        {
            ItemStack currentStack = Minecraft.getMinecraft().player.getHeldItemMainhand();
            if ( currentStack == null || currentStack == ItemStack.EMPTY )
            {
                return;
            }
            
            Minecraft.getMinecraft().displayGuiScreen( new GuiItem( Minecraft.getMinecraft().currentScreen, currentStack.copy() ) );
        }
        
        if ( Infinity.keybindCopy.isPressed() && Minecraft.getMinecraft().world != null )
        {
            Minecraft mc = Minecraft.getMinecraft();
            EntityPlayer player = Minecraft.getMinecraft().player;
            
            /*
             * RayTraceResult res = Minecraft.getMinecraft().player.rayTrace( 15, Minecraft.getMinecraft().getRenderPartialTicks() );
             * 
             * if ( res != null && res.typeOfHit != null ) { Minecraft.getMinecraft().player.sendMessage( new TextComponentString( "" + res.typeOfHit ) );
             * 
             * if ( res.typeOfHit == Type.ENTITY && res.entityHit instanceof EntityPlayer ) { EntityPlayer playerHit = (EntityPlayer) res.entityHit;
             * 
             * Minecraft.getMinecraft().player.sendMessage( new TextComponentString( "Copying " + playerHit.getDisplayNameString() ) ); for ( ItemStack s : playerHit.getEquipmentAndArmor() ) { Minecraft.getMinecraft().player.sendMessage( s.getTextComponent() ); } } }
             */
            
            if ( Minecraft.getMinecraft().pointedEntity != null )
            {
                Entity entityHit = Minecraft.getMinecraft().pointedEntity;
                if ( entityHit instanceof EntityPlayer || entityHit instanceof EntityArmorStand || entityHit instanceof EntityLiving )
                {
                    player.sendMessage( new TextComponentString( "Copying " ).appendSibling( entityHit.getDisplayName() ) );
                    
                    ItemStack[] stacks = new ItemStack[ 6 ];
                    
                    int i = 0;
                    for ( ItemStack stack : entityHit.getEquipmentAndArmor() )
                    {
                        stacks[i++] = stack;
                    }
                    
                    // Main hand
                    if ( stacks != null && stacks.length > 0 )
                    {
                        player.inventory.setPickedItemStack( stacks[0] );
                        mc.playerController.sendSlotPacket( stacks[0], player.inventory.currentItem + 36 ); // 36 is the index of the actionbar (5 crafting, 4 armor, and 27 inventory, if I remember correctly).
                        
                        mc.playerController.sendSlotPacket( stacks[1], 45 ); // 36 is the index of the actionbar (5 crafting, 4 armor, and 27 inventory, if I remember correctly).
                        
                        mc.playerController.sendSlotPacket( stacks[2], 8 ); // 36 is the index of the actionbar (5 crafting, 4 armor, and 27 inventory, if I remember correctly).
                        mc.playerController.sendSlotPacket( stacks[3], 7 ); // 36 is the index of the actionbar (5 crafting, 4 armor, and 27 inventory, if I remember correctly).
                        mc.playerController.sendSlotPacket( stacks[4], 6 ); // 36 is the index of the actionbar (5 crafting, 4 armor, and 27 inventory, if I remember correctly).
                        mc.playerController.sendSlotPacket( stacks[5], 5 ); // 36 is the index of the actionbar (5 crafting, 4 armor, and 27 inventory, if I remember correctly).
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onRenderTooltip( RenderTooltipEvent.Pre e )
    {
        if ( (e.getStack().getItem() == Items.BANNER || e.getStack().getItem() == Items.SHIELD) && Minecraft.getMinecraft().currentScreen instanceof GuiContainerCreative )
        {
            GuiContainerCreative gui = (GuiContainerCreative) Minecraft.getMinecraft().currentScreen;
            
            if ( gui.getSelectedTabIndex() == Infinity.BANNERS.getTabIndex() && gui.getSlotUnderMouse() != null && gui.getSlotUnderMouse().inventory == Minecraft.getMinecraft().player.inventory )
            {
                gui.initGui();
            }
        }
    }
    
    @Nullable
    private void getRayTraceResult( int range, float partialTicks )
    {
        Entity entity = Minecraft.getMinecraft().player;
        Minecraft mc = Minecraft.getMinecraft();
        Entity pointedEntity = null;
        
        if ( entity != null )
        {
            if ( mc.world != null )
            {
                mc.mcProfiler.startSection( "pick" );
                mc.pointedEntity = null;
                double d0 = (double) mc.playerController.getBlockReachDistance();
                mc.objectMouseOver = entity.rayTrace( d0, partialTicks );
                Vec3d vec3d = entity.getPositionEyes( partialTicks );
                boolean flag = false;
                // int i = 3;
                double d1 = d0;
                
                if ( mc.playerController.extendedReach() )
                {
                    d1 = 6.0D;
                    d0 = d1;
                }
                else
                {
                    if ( d0 > 3.0D )
                    {
                        flag = true;
                    }
                }
                
                if ( mc.objectMouseOver != null )
                {
                    d1 = mc.objectMouseOver.hitVec.distanceTo( vec3d );
                }
                
                Vec3d vec3d1 = entity.getLook( 1.0F );
                Vec3d vec3d2 = vec3d.addVector( vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0 );
                pointedEntity = null;
                Vec3d vec3d3 = null;
                // float f = 1.0F;
                List<Entity> list = mc.world.getEntitiesInAABBexcluding( entity, entity.getEntityBoundingBox().expand( vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0 ).grow( 1.0D, 1.0D, 1.0D ), Predicates.and( EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
                    public boolean apply( @Nullable Entity p_apply_1_ )
                    {
                        return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
                    }
                } ) );
                double d2 = d1;
                
                for ( int j = 0 ; j < list.size() ; ++j )
                {
                    Entity entity1 = list.get( j );
                    AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow( (double) entity1.getCollisionBorderSize() );
                    RayTraceResult raytraceresult = axisalignedbb.calculateIntercept( vec3d, vec3d2 );
                    
                    if ( axisalignedbb.contains( vec3d ) )
                    {
                        if ( d2 >= 0.0D )
                        {
                            pointedEntity = entity1;
                            vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
                            d2 = 0.0D;
                        }
                    }
                    else if ( raytraceresult != null )
                    {
                        double d3 = vec3d.distanceTo( raytraceresult.hitVec );
                        
                        if ( d3 < d2 || d2 == 0.0D )
                        {
                            if ( entity1.getLowestRidingEntity() == entity.getLowestRidingEntity() && !entity1.canRiderInteract() )
                            {
                                if ( d2 == 0.0D )
                                {
                                    pointedEntity = entity1;
                                    vec3d3 = raytraceresult.hitVec;
                                }
                            }
                            else
                            {
                                pointedEntity = entity1;
                                vec3d3 = raytraceresult.hitVec;
                                d2 = d3;
                            }
                        }
                    }
                }
                
                if ( pointedEntity != null && flag && vec3d.distanceTo( vec3d3 ) > 3.0D )
                {
                    pointedEntity = null;
                    mc.objectMouseOver = new RayTraceResult( RayTraceResult.Type.MISS, vec3d3, (EnumFacing) null, new BlockPos( vec3d3 ) );
                }
                
                if ( pointedEntity != null && (d2 < d1 || mc.objectMouseOver == null) )
                {
                    mc.objectMouseOver = new RayTraceResult( pointedEntity, vec3d3 );
                    
                    if ( pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame )
                    {
                        mc.pointedEntity = pointedEntity;
                    }
                }
                
                mc.mcProfiler.endSection();
            }
        }
    }
    
    /**
     * This registers the background that's used for the main hand slot in the Equipment GUI of armor stands
     * 
     * @param event
     */
    @SubscribeEvent
    public static void textureStich( TextureStitchEvent.Pre event )
    {
        event.getMap().registerSprite( HelperGui.EMPTY_ARMOR_SLOT_SWORD );
    }
}
