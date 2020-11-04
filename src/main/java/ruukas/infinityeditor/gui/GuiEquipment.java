package ruukas.infinityeditor.gui;

import java.io.IOException;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Rotations;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import ruukas.infinityeditor.gui.armorstand.ContainerEquipment;
import ruukas.infinityeditor.gui.armorstand.InventoryArmorStandEquipment;
import ruukas.infinityeditor.nbt.NBTHelper;

@SideOnly( Side.CLIENT )
public class GuiEquipment extends GuiContainer
{
    /** The old x position of the mouse pointer */
    private float oldMouseX;
    /** The old y position of the mouse pointer */
    private float oldMouseY;
    
    private ItemStack stack;
    // private EntityArmorStand armorStand;
    private final GuiScreen lastScreen;
    
    private static final ResourceLocation EQUIPMENT_BACKGROUND = new ResourceLocation( "infinity:textures/gui/equipment.png" );
    
    public GuiEquipment(GuiScreen lastScreen, ItemStack stack) {
        super( new ContainerEquipment( new InventoryArmorStandEquipment( stack ) ) );
        
        this.stack = stack;
        this.allowUserInput = false;
        this.lastScreen = lastScreen;
    }
    
    @Override
    public void initGui()
    {
        super.initGui();
    }
    
    @Nullable
    public EntityArmorStand getEntity()
    {
        return ((InventoryArmorStandEquipment) ((ContainerEquipment) this.inventorySlots).inventory).entityStand;
    }
    
    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer( int mouseX, int mouseY )
    {
        String s = I18n.format( "gui.equipment.inventoryclone" );
        this.fontRenderer.drawString( s, (width / 2) - guiLeft - (fontRenderer.getStringWidth( s ) / 2), -10, 0x7c2c87 );
    }
    
    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen( int mouseX, int mouseY, float partialTicks )
    {
        this.drawDefaultBackground();
        
        super.drawScreen( mouseX, mouseY, partialTicks );
        
        this.renderHoveredToolTip( mouseX, mouseY );
        this.oldMouseX = (float) mouseX;
        this.oldMouseY = (float) mouseY;
    }
    
    @Override
    protected void keyTyped( char typedChar, int keyCode ) throws IOException
    {
        super.keyTyped( typedChar, keyCode );
        
        if ( keyCode == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches( keyCode ) )
        {
            this.mc.displayGuiScreen( lastScreen );
        }
    }
    
    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    @Override
    protected void handleMouseClick( Slot slotIn, int slotId, int mouseButton, ClickType type )
    {
        if ( slotIn != null )
        {
            slotId = slotIn.slotNumber;
        }
        
        this.inventorySlots.slotClick( slotId, mouseButton, type, mc.player );
    }
    
    /**
     * Draws the background layer of this container (behind the items).
     */
    protected void drawGuiContainerBackgroundLayer( float partialTicks, int mouseX, int mouseY )
    {
        GlStateManager.color( 1.0F, 1.0F, 1.0F, 1.0F );
        
        this.mc.getTextureManager().bindTexture( EQUIPMENT_BACKGROUND );
        int i = this.guiLeft;
        int j = this.guiTop;
        this.drawTexturedModalRect( i, j, 0, 0, this.xSize, this.ySize );
        drawPlayerOnScreen( i + 51, j + 75, 30, (float) (i + 51) - this.oldMouseX, (float) (j + 75 - 50) - this.oldMouseY, this.mc.player );
        // applyItemDataToMob();
        EntityArmorStand ent = getEntity();
        if ( ent != null && !NBTHelper.ArmorStandNBTHelper.INVISIBLE.getValue( stack ) )
            drawArmorStandOnScreen( i + 126, j + 75, 30, (float) (i + 126) - this.oldMouseX, (float) (j + (NBTHelper.ArmorStandNBTHelper.SMALL.getValue( stack ) ? 44 : 25)) - this.oldMouseY, ent );
    }
    
    /**
     * Draws an entity on the screen looking toward the cursor.
     */
    public static void drawPlayerOnScreen( int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent )
    {
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
        GlStateManager.rotate( 135.0F, 0.0F, 1.0F, 0.0F );
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate( -135.0F, 0.0F, 1.0F, 0.0F );
        GlStateManager.rotate( -((float) Math.atan( (double) (mouseY / 40.0F) )) * 20.0F, 1.0F, 0.0F, 0.0F );
        ent.renderYawOffset = (float) Math.atan( (double) (mouseX / 40.0F) ) * 20.0F;
        ent.rotationYaw = (float) Math.atan( (double) (mouseX / 40.0F) ) * 40.0F;
        ent.rotationPitch = -((float) Math.atan( (double) (mouseY / 40.0F) )) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate( 0.0F, 0.0F, 0.0F );
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY( 180.0F );
        rendermanager.setRenderShadow( false );
        rendermanager.renderEntity( ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false );
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
    
    /**
     * Draws an entity on the screen looking toward the cursor.
     */
    public static void drawArmorStandOnScreen( int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent )
    {
        EntityArmorStand stand = (EntityArmorStand) ent;
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
        GlStateManager.rotate( 135.0F, 0.0F, 1.0F, 0.0F );
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate( -135.0F, 0.0F, 1.0F, 0.0F );
        GlStateManager.rotate( -((float) Math.atan( (double) (mouseY / 40.0F) )) * 20.0F, 1.0F, 0.0F, 0.0F );
        ent.renderYawOffset = (float) Math.atan( (double) (mouseX / 40.0F) ) * 20.0F;
        ent.rotationYaw = (float) Math.atan( (double) (mouseX / 40.0F) ) * 40.0F;
        ent.rotationPitch = -((float) Math.atan( (double) (mouseY / 40.0F) )) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        stand.setHeadRotation( new Rotations( -((float) Math.atan( (double) (mouseY / 40.0F) )) * 20.0F, (float) Math.atan( (double) (mouseX / 40.0F) ) * 40.0F, 0f ) );
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate( 0.0F, 0.0F, 0.0F );
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY( 180.0F );
        rendermanager.setRenderShadow( false );
        rendermanager.renderEntity( ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false );
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
}