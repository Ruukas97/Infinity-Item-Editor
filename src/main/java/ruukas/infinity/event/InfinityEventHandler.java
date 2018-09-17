package ruukas.infinity.event;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import io.netty.channel.ChannelDuplexHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import ruukas.infinity.Infinity;
import ruukas.infinity.data.InfinityConfig;
import ruukas.infinity.data.thevoid.VoidController;
import ruukas.infinity.gui.GuiItem;
import ruukas.infinity.gui.HelperGui;
import ruukas.infinity.util.GiveHelper;

@Mod.EventBusSubscriber( modid = Infinity.MODID )
public class InfinityEventHandler
{
    
    @SubscribeEvent
    public void onConfigChanged( ConfigChangedEvent.OnConfigChangedEvent eventArgs )
    {
        if ( eventArgs.getModID().equals( Infinity.MODID ) )
            ConfigManager.sync( Infinity.MODID, Type.INSTANCE );
    }
    
    @SubscribeEvent
    public static void onKeyPress( KeyInputEvent event )
    {
        if ( Infinity.keybind.isPressed() && Minecraft.getMinecraft().world != null )
        {
            ItemStack currentStack = Minecraft.getMinecraft().player.getHeldItemMainhand();
            
            Minecraft.getMinecraft().displayGuiScreen( new GuiItem( Minecraft.getMinecraft().currentScreen, currentStack.copy(), -1 ) );
        }
        
        if ( Infinity.keybindSave.isPressed() && Minecraft.getMinecraft().world != null )
        {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            ItemStack currentStack = player.getHeldItemMainhand();
            Infinity.infinitySettings.addItemStack( player, currentStack.copy() );
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
    public static void onKeyboardInput( GuiScreenEvent.KeyboardInputEvent.Pre e )
    {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        if ( Infinity.keybind != null && Keyboard.isKeyDown( Infinity.keybind.getKeyCode() ) && mc.world != null && player != null && e.getGui() instanceof GuiContainer )
        {
            ItemStack cursorStack = player.inventory.getItemStack();
            if ( cursorStack == ItemStack.EMPTY || cursorStack.getItem() == Items.AIR )
            {
                GuiContainer gui = (GuiContainer) e.getGui();
                Slot s = gui.getSlotUnderMouse();
                if ( s != null && s.inventory == player.inventory )
                {
                    int i = s.getSlotIndex();
                    
                    if ( i <= 8 )
                    {
                        i += 36;
                    }
                    else if ( 36 <= i && i <= 39 )
                    {
                        i = 8 - (i % 4);
                        // 39 -> 5: Head
                        // 38 -> 6: Chest
                        // 37 -> 7: Legs
                        // 36 -> 8: Feet
                    }
                    else if ( i == 40 )
                    {
                        i = 45;
                    }
                    
                    mc.displayGuiScreen( new GuiItem( mc.currentScreen, s.getStack().copy(), i ) );
                    e.setCanceled( true );
                }
            }
        }
        
        if ( GameSettings.isKeyDown( Infinity.keybindSave ) && player != null )
        {
            if ( e.getGui() != null && e.getGui() instanceof GuiContainer )
            {
                GuiContainer gui = (GuiContainer) e.getGui();
                Slot slot = gui.getSlotUnderMouse();
                if ( slot != null )
                {
                    if ( (gui instanceof GuiContainerCreative && ((GuiContainerCreative) gui).getSelectedTabIndex() == Infinity.REALM.getTabIndex()) && !(slot.inventory instanceof InventoryPlayer) )
                    {
                        Infinity.infinitySettings.removeItemStack( player, slot.getStack() );
                    }
                    else
                    {
                        Infinity.infinitySettings.addItemStack( player, slot.getStack().copy() );
                    }
                    
                    if ( InfinityConfig.getIsVoidEnabled() )
                    {
                        new VoidController( slot.getStack() ).addItemStack( player, slot.getStack().copy(), player.getUniqueID().toString().replace( "-", "" ) );
                    }
                    
                    e.setCanceled( true );
                }
            }
        }
        
        if ( e.getGui() != null && e.getGui() instanceof GuiContainer )
        {
            GuiContainer gui = (GuiContainer) e.getGui();
            Slot slot = gui.getSlotUnderMouse();
            
            if ( slot != null )
            {
                if ( GuiScreen.isKeyComboCtrlC( Keyboard.isKeyDown( 46 ) ? 46 : 0 ) && slot.getHasStack() )
                {
                    String s = GiveHelper.getStringFromItemStack( slot.getStack() );
                    GuiScreen.setClipboardString( s );
                }
                
                else if ( GuiScreen.isKeyComboCtrlV( Keyboard.isKeyDown( 47 ) ? 47 : 0 ) && slot.inventory == player.inventory )
                {
                    ItemStack stack = GiveHelper.getItemStackFromString( GuiScreen.getClipboardString() );
                    int i = slot.getSlotIndex();
                    
                    if ( i <= 8 )
                    {
                        i += 36;
                    }
                    else if ( 36 <= i && i <= 39 )
                    {
                        i = 8 - (i % 4);
                    }
                    else if ( i == 40 )
                    {
                        i = 45;
                    }
                    
                    mc.playerController.sendSlotPacket( stack, i );
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onRenderTooltip( RenderTooltipEvent.Pre e )
    {
        if ( Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiContainerCreative && (e.getStack().getItem() == Items.BANNER || e.getStack().getItem() == Items.SHIELD || e.getStack().getItem() == Items.FIREWORK_CHARGE || e.getStack().getItem() == Items.FIREWORKS) )
        {
            GuiContainerCreative gui = (GuiContainerCreative) Minecraft.getMinecraft().currentScreen;
            
            if ( (gui.getSelectedTabIndex() == Infinity.BANNERS.getTabIndex() || gui.getSelectedTabIndex() == Infinity.FIREWORKS.getTabIndex()) && gui.getSlotUnderMouse() != null && gui.getSlotUnderMouse().inventory == Minecraft.getMinecraft().player.inventory )
            {
                gui.initGui();
            }
        }
    }
    
    @SubscribeEvent
    public static void onChatReceived( ClientChatReceivedEvent e )
    {
        if ( !InfinityConfig.getIsVoidEnabled() )
        {
            return;
        }
        
        for ( ITextComponent comp : e.getMessage() )
        {
            if ( comp.getStyle().getHoverEvent() != null && comp.getStyle().getHoverEvent().getAction() == HoverEvent.Action.SHOW_ITEM )
            {
                ItemStack itemstack = ItemStack.EMPTY;
                
                try
                {
                    NBTBase nbtbase = JsonToNBT.getTagFromJson( comp.getStyle().getHoverEvent().getValue().getUnformattedText() );
                    
                    if ( nbtbase instanceof NBTTagCompound )
                    {
                        itemstack = new ItemStack( (NBTTagCompound) nbtbase );
                    }
                }
                catch ( NBTException var9 )
                {
                }
                
                new VoidController( itemstack ).addItemStack( Minecraft.getMinecraft().player, itemstack, "chat" );
            }
        }
        
    }
    
    @SubscribeEvent
    public static void onServerConnection( ClientConnectedToServerEvent e )
    {
        if ( InfinityConfig.getIsVoidEnabled() )
        {
            e.getManager().channel().pipeline().addBefore( "packet_handler", "void_handler", new ChannelDuplexHandler() {
                public void channelRead( io.netty.channel.ChannelHandlerContext ctx, Object msg ) throws Exception
                {
                    if ( msg instanceof SPacketEntityEquipment )
                    {
                        SPacketEntityEquipment packet = (SPacketEntityEquipment) msg;
                        ItemStack stack = packet.getItemStack().copy();
                        Entity ent = Minecraft.getMinecraft().world.getEntityByID( packet.getEntityID() );
                        String uuid = null;
                        if ( ent instanceof EntityPlayer )
                        {
                            uuid = ((EntityPlayer) ent).getUniqueID().toString().replace( "-", "" );
                        }
                        new VoidController( stack ).addItemStack( Minecraft.getMinecraft().player, stack, uuid );
                    }
                    super.channelRead( ctx, msg );
                };
            } );
        }
    }
    
    /*
     * @SubscribeEvent public static void onInitGuiPost(GuiScreenEvent.InitGuiEvent.Post e){ }
     * 
     * @SubscribeEvent public static void onActionPerformed(GuiScreenEvent.ActionPerformedEvent e){
     * 
     * }
     */
    
    /*
     * @SubscribeEvent public void updateTooltip(ItemTooltipEvent event) { ItemStack stack = event.getItemStack(); Item item = event.getItemStack().getItem(); List<String> tooltip = event.getToolTip();
     * 
     * boolean isAdvanced = event.getFlags().isAdvanced();
     * 
     * if (item instanceof ItemFood) { ItemFood food = (ItemFood) item; tooltip.add(TextFormatting.GOLD + "Food points: " + food.getHealAmount(event.getItemStack())); if (isAdvanced) { tooltip.add(TextFormatting.GOLD + "Saturation modifier: " + ItemStack.DECIMALFORMAT.format(food.getSaturationModifier(event.getItemStack())));
     * 
     * tooltip.add(TextFormatting.GOLD + "Quality: " + ItemStack.DECIMALFORMAT.format(QualityHelper.getFoodQuality(event.getItemStack().getItem()))); } } else if (item == Items.CAKE) { tooltip.add(TextFormatting.GOLD + "Food points: 14"); if (isAdvanced) { tooltip.add(TextFormatting.GOLD + "Saturation modifier: " + ItemStack.DECIMALFORMAT.format(0.1d)); tooltip.add(TextFormatting.GOLD + "Quality: " + ItemStack.DECIMALFORMAT.format(16.8d)); } } else if (item == Items.ARMOR_STAND) { tooltip.add(TextFormatting.DARK_PURPLE + "Quality Order info:"); if (stack.hasTagCompound() && stack.getTagCompound().hasKey("EntityTag", Constants.NBT.TAG_COMPOUND)) { NBTTagCompound entityTag = stack.getTagCompound().getCompoundTag("EntityTag");
     * 
     * if (entityTag.getByte("Small") == 1) { tooltip.add(TextFormatting.GOLD + "Small"); } else { tooltip.add(TextFormatting.GOLD + "Big"); }
     * 
     * if (entityTag.getByte("ShowArms") == 1) { tooltip.add(TextFormatting.GOLD + "Arms"); } else { tooltip.add(TextFormatting.GOLD + "No Arms"); }
     * 
     * if (entityTag.getByte("NoBasePlate") == 1) { tooltip.add(TextFormatting.GOLD + "No Base Plate"); } else { tooltip.add(TextFormatting.GOLD + "Base Plate"); }
     * 
     * if (stack.getTagCompound().getBoolean("QOArmor")) { tooltip.add(TextFormatting.ITALIC + "" + TextFormatting.GOLD + "Armor"); } else { tooltip.add(TextFormatting.ITALIC + "" + TextFormatting.GOLD + "No Armor"); } } else { tooltip.add(TextFormatting.ITALIC + "Default Armor Stand"); tooltip.add("Big"); tooltip.add("No Arms"); tooltip.add("Base Plate"); tooltip.add(TextFormatting.ITALIC + "No Armor"); } }else if(item == Items.DYE){ tooltip.add(1, "" + QualityHelper.getTextFormatFromDye(EnumDyeColor.byDyeDamage(stack.getItemDamage())) + EnumDyeColor.byDyeDamage(stack.getItemDamage())); }else if(item == Items.SKULL){ if(stack.hasTagCompound() && stack.getTagCompound().hasKey("SkullOwner", Constants.NBT.TAG_COMPOUND)){ if(stack.getTagCompound().getCompoundTag("SkullOwner").hasKey("Name", Constants.NBT.TAG_STRING)){ tooltip.add(1, TextFormatting.BLUE + stack.getTagCompound().getCompoundTag("SkullOwner").getString("Name")); }else{ tooltip.add(1, TextFormatting.GRAY + "Unavailable"); } } }else if(item == Items.NAME_TAG){ for(String[] specialNameArray : TabSpawnegg.specialMobNames){ if (stack.getDisplayName() == specialNameArray[0]) { tooltip.add(TextFormatting.GOLD + specialNameArray[1]); } } }else if(QualityHelper.getStackDPS(stack) > 0){ tooltip.add(TextFormatting.GOLD + "DPS: " + ItemStack.DECIMALFORMAT.format(QualityHelper.getStackDPS(stack))); if(item == QualityHelper.getStrongestSword()){ tooltip.add("Strongest Sword"); } }
     * 
     * if (isAdvanced) { if (item instanceof ItemBlock) { event.getToolTip().add(TextFormatting.DARK_GRAY + "Class: " + ((ItemBlock) item).getBlock().getClass().getSimpleName()); } else { event.getToolTip().add(TextFormatting.DARK_GRAY + "Class: " + event.getItemStack().getItem().getClass().getSimpleName()); } } }
     */
    
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
