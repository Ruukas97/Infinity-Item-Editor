package ruukas.infinity.gui;

import java.io.IOException;
import java.util.UUID;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemArmorStand;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinity.gui.action.GuiInfinityButton;
import ruukas.infinity.gui.monsteregg.GuiEntityTags;
import ruukas.infinity.gui.monsteregg.MobTag;
import ruukas.infinity.nbt.NBTHelper.ArmorStandNBTHelper;

@SideOnly( Side.CLIENT )
public class GuiArmorStand extends GuiInfinity
{
    
    private EntityArmorStand armorStand = null;
    
    private GuiInfinityButton entityButton, armsButton, smallButton, invisibleButton, baseButton, markerButton, inventoryButton, poseButton;
    
    public GuiArmorStand(GuiScreen lastScreen, ItemStackHolder stackHolder) {
        super( lastScreen, stackHolder );
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
        
        int buttons = 0;
        this.entityButton = addButton( new GuiInfinityButton( 100 + buttons, (this.width / 2) - 75, 50 + (30 * buttons++), 150, 20, I18n.format( "gui.spawnegg.entity" ) ) );
        this.armsButton = addButton( new GuiInfinityButton( 100 + buttons, (this.width / 2) - 75, 50 + (30 * buttons++), 150, 20, I18n.format( "tag.armorstand.arms." + ArmorStandNBTHelper.SHOW_ARMS.getByte( getItemStack() ) ) ) );
        this.smallButton = addButton( new GuiInfinityButton( 100 + buttons, (this.width / 2) - 75, 50 + (30 * buttons++), 150, 20, I18n.format( "tag.armorstand.small." + ArmorStandNBTHelper.SMALL.getByte( getItemStack() ) ) ) );
        this.invisibleButton = addButton( new GuiInfinityButton( 100 + buttons, (this.width / 2) - 75, 50 + (30 * buttons++), 150, 20, I18n.format( "tag.armorstand.invisible." + ArmorStandNBTHelper.INVISIBLE.getByte( getItemStack() ) ) ) );
        this.baseButton = addButton( new GuiInfinityButton( 100 + buttons, (this.width / 2) - 75, 50 + (30 * buttons++), 150, 20, I18n.format( "tag.armorstand.nobase." + ArmorStandNBTHelper.NO_BASE.getByte( getItemStack() ) ) ) );
        this.markerButton = addButton( new GuiInfinityButton( 100 + buttons, (this.width / 2) - 75, 50 + (30 * buttons++), 150, 20, I18n.format( "tag.armorstand.marker." + ArmorStandNBTHelper.SHOW_ARMS.getByte( getItemStack() ) ) ) );
        this.inventoryButton = addButton( new GuiInfinityButton( 100 + buttons, (this.width / 2) - 75, 50 + (30 * buttons++), 150, 20, I18n.format( "tag.armorstand.inventory" ) ) );
        this.poseButton = addButton( new GuiInfinityButton( 100 + buttons, (this.width / 2) - 75, 50 + (30 * buttons++), 150, 20, I18n.format( "tag.armorstand.pose" ) ) );
        
        updateArmorStand();
    }
    
    @Override
    protected void actionPerformed( GuiButton button ) throws IOException
    {
        if ( button.id == entityButton.id )
        {
            this.mc.displayGuiScreen( new GuiEntityTags( this, getItemStack(), MobTag.ENTITY_SPECIFIC ) );
        }
        
        else if ( button.id == armsButton.id )
        {
            ArmorStandNBTHelper.SHOW_ARMS.switchValue( getItemStack() );
            armsButton.displayString = I18n.format( "tag.armorstand.arms." + ArmorStandNBTHelper.SHOW_ARMS.getByte( getItemStack() ) );
            updateArmorStand();
        }
        
        else if ( button.id == smallButton.id )
        {
            ArmorStandNBTHelper.SMALL.switchValue( getItemStack() );
            smallButton.displayString = I18n.format( "tag.armorstand.small." + ArmorStandNBTHelper.SMALL.getByte( getItemStack() ) );
            updateArmorStand();
        }
        
        else if ( button.id == invisibleButton.id )
        {
            ArmorStandNBTHelper.INVISIBLE.switchValue( getItemStack() );
            invisibleButton.displayString = I18n.format( "tag.armorstand.invisible." + ArmorStandNBTHelper.INVISIBLE.getByte( getItemStack() ) );
            updateArmorStand();
        }
        
        else if ( button.id == baseButton.id )
        {
            ArmorStandNBTHelper.NO_BASE.switchValue( getItemStack() );
            baseButton.displayString = I18n.format( "tag.armorstand.nobase." + ArmorStandNBTHelper.NO_BASE.getByte( getItemStack() ) );
            updateArmorStand();
        }
        
        else if ( button.id == markerButton.id )
        {
            ArmorStandNBTHelper.MARKER.switchValue( getItemStack() );
            markerButton.displayString = I18n.format( "tag.armorstand.marker." + ArmorStandNBTHelper.MARKER.getByte( getItemStack() ) );
            updateArmorStand();
        }
        
        else if ( button.id == inventoryButton.id )
        {
            mc.displayGuiScreen( new GuiEquipment( this, getItemStack() ) );
        }
        
        else if ( button.id == poseButton.id )
        {
            mc.displayGuiScreen( new GuiPose( this, getItemStack() ) );
        }
        
        else
        {
            super.actionPerformed( button );
        }
    }
    
    @Override
    protected void reset()
    {
        if ( getItemStack().hasTagCompound() && getItemStack().getTagCompound().hasKey( "EntityTag", NBT.TAG_COMPOUND ) )
        {
            String id = null;
            if ( getItemStack().getSubCompound( "EntityTag" ).hasKey( "id" ) )
            {
                id = getItemStack().getSubCompound( "EntityTag" ).getString( "id" );
            }
            getItemStack().getTagCompound().removeTag( "EntityTag" );
            
            if ( id != null )
            {
                NBTTagCompound entityTag = new NBTTagCompound();
                entityTag.setString( "id", id );
                
                getItemStack().getTagCompound().setTag( "EntityTag", entityTag );
            }
        }
        updateArmorStand();
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        super.drawScreen( mouseX, mouseY, partialTicks );
        
        if ( armorStand != null )
        {
            drawArmorStand( (int) (this.width / 3 * 2.5), this.height - 20, 70 );
        }
        
        GlStateManager.pushMatrix();
        HelperGui.addTooltip( invisibleButton, mouseX, mouseY, I18n.format( "gui.armorstand.invisible.note" ) );
        
        HelperGui.addTooltip( markerButton, mouseX, mouseY, I18n.format( "gui.armorstand.marker.note" ) );
        GlStateManager.popMatrix();
    }
    
    public void updateArmorStand()
    {
        if ( getItemStack().getItem() instanceof ItemArmorStand )
        {
            EntityArmorStand entity = new EntityArmorStand( mc.world );
            
            if ( entity != null && entity instanceof EntityArmorStand )
            {
                armorStand = (EntityArmorStand) entity;
                applyItemDataToArmorStand();
            }
        }
    }
    
    public void applyItemDataToArmorStand()
    {
        NBTTagCompound tag = getItemStack().getTagCompound();
        
        if ( tag != null && tag.hasKey( "EntityTag", NBT.TAG_COMPOUND ) )
        {
            UUID uuid = armorStand.getUniqueID();
            armorStand.setUniqueId( uuid );
            armorStand.readFromNBT( tag.getCompoundTag( "EntityTag" ) );
        }
    }
    
    /**
     * Draws an entity on the screen looking toward the cursor.
     */
    public void drawArmorStand( int posX, int posY, int scale )
    {
        EntityArmorStand ent = armorStand;
        ent.ticksExisted = (int) mc.world.getWorldTime();
        
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate( (float) posX, (float) posY, 50.0F );
        GlStateManager.scale( (float) (-scale), (float) scale, (float) scale );
        GlStateManager.rotate( 180.0F, 0.0F, 0.0F, 1.0F );
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate( 40.0F, 0.0F, 1.0F, 0.0F );
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate( -50.0F, 0.0F, 1.0F, 0.0F );
        GlStateManager.rotate( 10F, 1.0F, 0.0F, 0.0F );
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate( 0.0F, 0.0F, 0.0F );
        RenderManager rendermanager = mc.getRenderManager();
        rendermanager.setPlayerViewY( 180.0F );
        rendermanager.setRenderShadow( false );
        
        rendermanager.renderEntity( armorStand, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false );
        
        rendermanager.setRenderShadow( true );
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture( OpenGlHelper.lightmapTexUnit );
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture( OpenGlHelper.defaultTexUnit );
    }
    
    @Override
    protected String getNameUnlocalized()
    {
        return "armorstand";
    }
}
